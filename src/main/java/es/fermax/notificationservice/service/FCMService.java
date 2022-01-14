package es.fermax.notificationservice.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.ApnsConfig.Builder;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.ApsAlert;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.gson.Gson;

import es.fermax.notificationservice.config.LocalizationConfig;
import es.fermax.notificationservice.controller.dto.AppTokenDTO;
import es.fermax.notificationservice.controller.dto.MessageNotificationTokenDTO;
import es.fermax.notificationservice.controller.dto.MessageNotificationUserDTO;
import es.fermax.notificationservice.enums.NotificationCaseEnum;
import es.fermax.notificationservice.enums.OsEnum;
import es.fermax.notificationservice.enums.StatusEnum;
import es.fermax.notificationservice.enums.TargetEnum;
import es.fermax.notificationservice.exception.NotificationNotImplementedException;
import es.fermax.notificationservice.exception.TargetNotImplementedException;
import es.fermax.notificationservice.model.AppToken;
import es.fermax.notificationservice.model.DataKeys;
import es.fermax.notificationservice.model.FcmMessage;
import es.fermax.notificationservice.model.Notification;
import es.fermax.notificationservice.model.NotificationBody;
import es.fermax.notificationservice.model.NotificationTitle;
import es.fermax.notificationservice.rabbit.AddInviteeSender;
import es.fermax.notificationservice.repo.AppTokenRepository;
import es.fermax.notificationservice.repo.FcmMessageRepository;

@Service
public class FCMService {

	private static final String TITLE = "NotificationTitle";
	private static final String BODY = "NotificationBody";
	private static final String TTL_APNS = "apns-expiration";

	@Autowired
	FcmMessageRepository repo;

	@Autowired
	AppTokenRepository appTokenRepository;

	@Autowired
	LocalizationConfig localizationConfig;

	@Autowired
	AppTokenService appTokenService;

	@Autowired
	FirebaseMessagingService firebaseMessagingService;

	private static final Logger log = LoggerFactory.getLogger(FCMService.class);

	public FCMService() {
		super();
	}

	/**
	 * Send Notification to confirm successful registration.
	 *
	 * @param appToken - identifier for user-mobile_app on firebase
	 */
	public void sendRegistrationConfirmNotification(AppTokenDTO appToken) {

		log.info("Sending Confirmation on Successful Registration notification to user {}", appToken.getUserId());

		FcmMessage fcmMessage = createMessageContent(appToken, NotificationCaseEnum.REGISTRATION_CONFIRM);

		createAndSendMessage(fcmMessage, appToken.getOs());

		log.info("Confirmation on Successful Registration notification to user {} sent.", appToken.getUserId());

	}

	/**
	 * Save and send the message to Firebase
	 * 
	 * @param fcmMessage - message to perstis compatible with Firebase
	 * @param os         - android or iOS
	 */
	private void createAndSendMessage(FcmMessage fcmMessage, String os) {
		createAndSendMessage(fcmMessage, os, null);
	}

	/**
	 * Save and send the message to Firebase
	 * 
	 * @param fcmMessage - message to perstis compatible with Firebase
	 * @param os         - android or iOS
	 * @param ttl        - time to live
	 */
	private void createAndSendMessage(FcmMessage fcmMessage, String os, Integer ttl) {
		repo.save(fcmMessage);

		if (os.equalsIgnoreCase(OsEnum.WEB.name())) {
			fcmMessage.setDeliveryStatus(StatusEnum.SENT.status);
		} else {
			Message messageToSend = createFirebaseMessage(fcmMessage, os, ttl);
			FirebaseSendResponse response = firebaseMessagingService.sendMessage(messageToSend);

			if (response.isSent) {
				fcmMessage.setFcmId(response.getFcmId());
				fcmMessage.setDeliveryStatus(StatusEnum.SENT.status);
			} else {
				fcmMessage.setDeliveryStatus(StatusEnum.ERROR.status);
				fcmMessage.setErrorDesc(response.getFailureReason());
				// HANDLE ERRORS
				handleErrorCode(fcmMessage, response);
			}
		}
		repo.save(fcmMessage);
	}

	/**
	 * If unregistered the appToken might be inactivated
	 * 
	 * @param fcmMessage - message entity
	 * @param response   - firebase response
	 */
	private void handleErrorCode(FcmMessage fcmMessage, FirebaseSendResponse response) {
		log.info("Handling error for: {}", response.getFailureReason());
		if (MessagingErrorCode.UNREGISTERED.equals(response.errorCode)) {
			log.info("Error Code: {}", response.getErrorCode());
			if (fcmMessage.getTarget().equals(TargetEnum.TOKEN.target)) {
				// user with token is unregistered (possible uninstalled the app)
				AppToken appToken = appTokenRepository.findByTokenAndActiveTrue(fcmMessage.getTargetValue()).stream().findAny()
						.orElse(null);
				if (appToken != null) {
					appToken.setActive(false);
					appTokenRepository.save(appToken);
					log.info("User apptoken inactivated. AppTokenId: {}", appToken.getId());
				}
			}
		}
	}

	/**
	 * Message to Persist
	 * 
	 * @param appToken         - User and Mobile app representation
	 * @param notificationCase - what is the case
	 * @return - message to persist
	 */
	private FcmMessage createMessageContent(AppTokenDTO appToken, NotificationCaseEnum notificationCase) {
		FcmMessage createdMessage = new FcmMessage();

		createdMessage.setTarget(TargetEnum.TOKEN.target);
		createdMessage.setTargetValue(appToken.getToken());

		Notification notification = new Notification();
		switch (notificationCase) {
		case REGISTRATION_CONFIRM:
			notification.setTitle(localizationConfig.get(NotificationTitle.REGISTRATION_CONFIRM, new Locale(appToken.getLocale())));
			notification.setBody(localizationConfig.get(NotificationBody.REGISTRATION_CONFIRM, new Locale(appToken.getLocale())));
			setMessageType(createdMessage, DataKeys.TYPE_INFO);
			break;
		case MULTIPLE_LOGIN:
			notification.setTitle(localizationConfig.get(NotificationTitle.MULTIPLE_LOGIN, new Locale(appToken.getLocale())));
			notification.setBody(localizationConfig.get(NotificationBody.MULTIPLE_LOGIN, new Locale(appToken.getLocale())));
			setMessageType(createdMessage, DataKeys.TYPE_INFO);
			break;
		case CUSTOM_NOTIFICATION:
			setMessageType(createdMessage, DataKeys.TYPE_INFO);
			break;
		default:
			log.warn("Not implemented message content");
			throw new NotificationNotImplementedException();
		}
		createdMessage.setNotification(notification);

		return createdMessage;
	}

	private void setMessageType(FcmMessage createdMessage, String type) {
		HashMap<String, String> data = new HashMap<>();
		data.put(DataKeys.TYPE, type);
		data.put(DataKeys.ACKNOWLEDGE, Boolean.TRUE.toString());
		createdMessage.setData(data);
	}

	/**
	 * Message to deliver through Firebase
	 * 
	 * @param fcmMessage - message to persist compatible with Firebase
	 * @param os         - android or iOS
	 * @param ttl        - time to live
	 * @return message entry
	 */
	private Message createFirebaseMessage(FcmMessage fcmMessage, String os, Integer ttl) {

		Message.Builder messageBuilder = Message.builder().putAllData(fcmMessage.getData());

		if (StringUtils.isEmpty(os)) {
			os = OsEnum.DEFAULT.toString();
		}

		switch (OsEnum.valueOf(os.toUpperCase())) {
		case ANDROID:
			addMessageToData(fcmMessage, messageBuilder, ttl);
			break;
		case IOS:
			addMessageToApnsConfig(fcmMessage, messageBuilder, ttl);
			break;
		default:
			addMessageToData(fcmMessage, messageBuilder, ttl);
			addMessageToApnsConfig(fcmMessage, messageBuilder, ttl);
			break;
		}

		if (fcmMessage.getTarget().equals(TargetEnum.TOKEN.target)) {
			messageBuilder.setToken(fcmMessage.getTargetValue());
		} else {
			log.warn("Not implemented message target {}", fcmMessage.getTarget());
			throw new TargetNotImplementedException();
		}

		return messageBuilder.build();
	}

	/**
	 * Set configuration on Android Firebase message and set in JSON on FcmMessage
	 * entity.
	 * 
	 * @param fcmMessage     - message to persist compatible with Firebase
	 * @param messageBuilder - Firebase message builder
	 * @param timeToLive     - time to live
	 */
	private void addMessageToData(FcmMessage fcmMessage, Message.Builder messageBuilder, Integer timeToLive) {
		Notification notification = fcmMessage.getNotification();

		if (timeToLive != null) {
			int ttl = timeToLive * 1000;
			AndroidConfig androidConfig = AndroidConfig.builder().setTtl(ttl).build();
			String configAsJson = new Gson().toJson(androidConfig);

			fcmMessage.setAndroidConfig(configAsJson);
			messageBuilder.setAndroidConfig(androidConfig);
		}
		messageBuilder.putData(TITLE, notification.getTitle()).putData(BODY, notification.getBody());
	}

	/**
	 * Set configuration on Apns Firebase message and set in JSON on FcmMessage
	 * entity.
	 * 
	 * @param fcmMessage     - message to persist compatible with Firebase
	 * @param messageBuilder - Firebase message builder
	 * @param timeToLive     - time to live
	 */
	private void addMessageToApnsConfig(FcmMessage fcmMessage, Message.Builder messageBuilder, Integer timeToLive) {
		Notification notification = fcmMessage.getNotification();
		Builder apnsConfigBuilder = ApnsConfig
				.builder().setAps(
						Aps.builder()
								.setAlert(ApsAlert.builder().setBody(notification.getBody() != null ? notification.getBody() : "-")
										.setTitle(notification.getTitle() != null ? notification.getTitle() : "Notification").build())
								.build());

		if (timeToLive != null) {
			String ttl = "0";

			if (timeToLive > 0) {
				// long epoch = System.currentTimeMillis() / 1000;
				ZonedDateTime gmt = LocalDateTime.now().atZone(ZoneId.of("GMT"));
				long epochGMT = gmt.toInstant().toEpochMilli() / 1000;
				ttl = String.valueOf(epochGMT + Long.valueOf(timeToLive));
				log.info("Notification details. Title: {}  -  Message: {}  -  Epoch: {}  -  TTL: {}  -  Epoch+TTL: {}",
						notification.getTitle(), notification.getBody(), epochGMT, timeToLive, ttl);
			}
			apnsConfigBuilder.putHeader(TTL_APNS, ttl);
		}

		// ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
		ApnsConfig apnsConfig = apnsConfigBuilder.build();
		String configAsJson = new Gson().toJson(apnsConfig);

		fcmMessage.setApnsConfig(configAsJson);
		messageBuilder.setApnsConfig(apnsConfig);
	}

	/**
	 * Report a login on another device
	 *
	 * @param appToken        - app id for user
	 * @param anotherDeviceOS - other os
	 */
	public void sendLoginAnotherDeviceNotification(AppTokenDTO appToken, String anotherDeviceOS) {

		log.info("Sending  Login on Another Device Notification to user {}", appToken.getUserId());
		FcmMessage fcmMessage = createMessageContent(appToken, NotificationCaseEnum.MULTIPLE_LOGIN);
		concatDeviceOsInBody(fcmMessage, anotherDeviceOS);
		createAndSendMessage(fcmMessage, appToken.getOs());
		log.info("Login on Another Device Notification to user {} sent.", appToken.getUserId());

	}

	private void concatDeviceOsInBody(FcmMessage fcmMessage, String os) {
		if (!StringUtils.isEmpty(os)) {
			String body = fcmMessage.getNotification().getBody().concat(StringUtils.SPACE).concat(os);
			fcmMessage.getNotification().setBody(body);
		}
	}

	/**
	 * Send a notification to token
	 *
	 * @param messageNotificationDTO - message
	 */
	public void sendCustomNotificationMessages(MessageNotificationTokenDTO messageNotificationDTO) {
		List<AppTokenDTO> appTokenList = appTokenService.getAppTokenDTOsByToken(messageNotificationDTO.getToken());
		if (appTokenList.isEmpty()) {
			log.warn("No registered tokens were found. Notification will not be delivered. Provided token {}",
					messageNotificationDTO.getToken());
		}
		appTokenList.forEach(
				token -> sendCustomNotificationMessage(token, messageNotificationDTO.getTitle(), messageNotificationDTO.getBody()));
	}



	/**
	 * Send a notification to user
	 *
	 * @param messageNotificationDTO - message
	 */
	public void sendCustomNotificationMessages(MessageNotificationUserDTO messageNotificationDTO) {
		List<AppTokenDTO> appTokenList = appTokenService.getAppTokenDTOsByUserId(messageNotificationDTO.getUserId());
		appTokenList.forEach(
				token -> sendCustomNotificationMessage(token, messageNotificationDTO.getTitle(), messageNotificationDTO.getBody()));
	}

	private void sendCustomNotificationMessage(AppTokenDTO appToken, String title, String body) {
		log.info("Sending Custom Notification to token {}", appToken.getToken());
		FcmMessage fcmMessage = createMessageContent(appToken, NotificationCaseEnum.CUSTOM_NOTIFICATION);
		fcmMessage.getNotification().setBody(body);
		fcmMessage.getNotification().setTitle(title);
		createAndSendMessage(fcmMessage, appToken.getOs());
		log.info("Custom Notification to user {} sent.", appToken.getToken());
	}

}
