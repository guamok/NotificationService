package es.fermax.notificationservice.service;

import static com.google.firebase.messaging.MessagingErrorCode.UNREGISTERED;
import static es.fermax.notificationservice.model.DataKeys.TYPE_CALL_ATTEND;
import static es.fermax.notificationservice.model.DataKeys.TYPE_CALL_END;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.springframework.test.context.ActiveProfiles;

import es.fermax.notificationservice.config.LocalizationConfig;
import es.fermax.notificationservice.controller.dto.AppTokenDTO;
import es.fermax.notificationservice.controller.dto.MessageNotificationTokenDTO;
import es.fermax.notificationservice.controller.dto.MessageNotificationUserDTO;
import es.fermax.notificationservice.enums.NotificationCaseEnum;
import es.fermax.notificationservice.enums.OsEnum;
import es.fermax.notificationservice.model.AppToken;
import es.fermax.notificationservice.model.FcmMessage;
import es.fermax.notificationservice.model.NotificationBody;
import es.fermax.notificationservice.model.NotificationTitle;
import es.fermax.notificationservice.rabbit.AddInviteeSender;
import es.fermax.notificationservice.repo.AppTokenRepository;
import es.fermax.notificationservice.repo.FcmMessageRepository;

@RunWith(PowerMockRunner.class)
@ActiveProfiles("test")
public class FcmServiceTest {


	@InjectMocks
	FCMService fcmService = new FCMService();

	@Mock
	private AppTokenService appTokenService;

	@Mock
	private LocalizationConfig localizationConfig;

	@Mock
	private FcmMessageRepository repo;

	@Mock
	AppTokenRepository appTokenRepository;

	@Mock
	private FirebaseMessagingService firebaseMessagingService;

	private AppTokenDTO appTokenDTO;
	private AppTokenDTO iosAppTokenDTO;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		appTokenDTO = new AppTokenDTO("token", 1, "en", "1.15", "Android", "2.25", true);
		iosAppTokenDTO = new AppTokenDTO("token", 2, "en", "0.15", "IOS", "2.5", true);
		given(localizationConfig.get(NotificationTitle.MULTIPLE_LOGIN, Locale.ENGLISH)).willReturn("Title");
		given(localizationConfig.get(NotificationBody.MULTIPLE_LOGIN, Locale.ENGLISH)).willReturn("Body");
		given(localizationConfig.get(NotificationTitle.REGISTRATION_CONFIRM, Locale.ENGLISH)).willReturn("Title");
		given(localizationConfig.get(NotificationBody.REGISTRATION_CONFIRM, Locale.ENGLISH)).willReturn("Body");
	}

	@Test
	public void givenAppToken_whenSendRegistrationConfirmNotificationThenReturnOK() {
		// mock
		given(firebaseMessagingService.sendMessage(any())).willReturn(new FirebaseSendResponse(true, "fcmId", "", null));

		// when
		fcmService.sendRegistrationConfirmNotification(appTokenDTO);

		// then
		verify(repo, atLeast(2)).save(any());

	}

	@Test
	public void givenAppTokenWithError_whenSendRegistrationConfirmNotificationThenReturnKO() {
		// mock

		given(firebaseMessagingService.sendMessage(any())).willReturn(new FirebaseSendResponse(false, "fcmId", "", null));

		// when
		fcmService.sendRegistrationConfirmNotification(appTokenDTO);

		// then
		verify(repo, atLeast(2)).save(any());

	}

	@Test
	public void givenAppTokenAndroid_whenSendLoginAnotherDeviceNotificationThenReturnOK() {
		// mock
		given(firebaseMessagingService.sendMessage(any())).willReturn(new FirebaseSendResponse(true, "fcmId", "", null));

		// when
		fcmService.sendLoginAnotherDeviceNotification(iosAppTokenDTO, "Android");

		// then
		verify(repo, atLeast(2)).save(any());

	}

	@Test
	public void givenAppTokenIOS_whenSendLoginAnotherDeviceNotificationThenReturnOK() {
		// mock
		given(firebaseMessagingService.sendMessage(any())).willReturn(new FirebaseSendResponse(true, "fcmId", "", null));

		// when
		fcmService.sendLoginAnotherDeviceNotification(appTokenDTO, "iOS");

		// then
		verify(repo, atLeast(2)).save(any());

	}

	@Test
	public void givenAppTokenNULL_whenSendLoginAnotherDeviceNotificationThenReturnOK() {
		// mock
		given(firebaseMessagingService.sendMessage(any())).willReturn(new FirebaseSendResponse(true, "fcmId", "", null));

		// when
		fcmService.sendLoginAnotherDeviceNotification(appTokenDTO, null);

		// then
		verify(repo, atLeast(2)).save(any());

	}

	@Test
	public void givenAppTokenAndroid_whenSendLoginAnotherDeviceNotificationThenReturnERROR_Unregistered() {
		// mock
		given(firebaseMessagingService.sendMessage(any()))
				.willReturn(new FirebaseSendResponse(false, null, "Requested entity was not found.", UNREGISTERED));
		given(appTokenRepository.findByTokenAndActiveTrue(appTokenDTO.getToken()))
				.willReturn(Collections.singletonList(new AppToken(1000, appTokenDTO.getToken())));

		// when
		fcmService.sendLoginAnotherDeviceNotification(appTokenDTO, OsEnum.ANDROID.name());

		// then
		verify(repo, atLeast(2)).save(any());
		verify(appTokenRepository, atLeast(1)).save(any()); // SAVED as ACTIVE=TRUE
	}

	@Test
	public void givenMessageNotificationTokenDTO_whenSendCustomNotificationMessagesThenReturnOK() {
		List<AppTokenDTO> appTokenList = new ArrayList<>();
		appTokenList.add(appTokenDTO);
		// mock
		given(firebaseMessagingService.sendMessage(any())).willReturn(new FirebaseSendResponse(true, "fcmId", "", null));
		given(appTokenService.getAppTokenDTOsByToken(anyString())).willReturn(appTokenList);

		// when
		fcmService.sendCustomNotificationMessages(new MessageNotificationTokenDTO("token", "Hello", "Welcome"));

		// then
		verify(repo, atLeast(2)).save(any());

	}

	@Test
	public void givenMessageNotificationEmptyTokenDTO_whenSendCustomNotificationMessagesThenReturnOK() {

		// mock
		given(firebaseMessagingService.sendMessage(any())).willReturn(new FirebaseSendResponse(true, "fcmId", "", null));
		given(appTokenService.getAppTokenDTOsByToken(anyString())).willReturn(Collections.emptyList());

		// when
		fcmService.sendCustomNotificationMessages(new MessageNotificationTokenDTO("token", "Hello", "Welcome"));

		// then
		verify(repo, never()).save(any());

	}

	@Test
	public void givenMessageNotificationUserDTO_whenSendCustomNotificationMessagesThenReturnOK() {
		List<AppTokenDTO> appTokenList = new ArrayList<>();
		appTokenList.add(appTokenDTO);
		// mock
		given(firebaseMessagingService.sendMessage(any())).willReturn(new FirebaseSendResponse(true, "fcmId", "", null));
		given(appTokenService.getAppTokenDTOsByUserId(1)).willReturn(appTokenList);

		// when
		fcmService.sendCustomNotificationMessages(new MessageNotificationUserDTO(1, "Hello", "Welcome"));

		// then
		verify(repo, atLeast(2)).save(any());

	}


	/*
	 * @Test public void givenAddMessageToApnsConfig() throws Exception { //
	 * FcmMessage fcmMessage, Message.Builder messageBuilder, Integer timeToLive
	 * FcmMessage fcmMessage = UtilsTest.getFcmMessage(30); Message.Builder
	 * messageBuilder = Message.builder().putAllData(fcmMessage.getData());
	 * 
	 * // when FcmMessage a = WhiteboxImpl.invokeMethod(fcmService,
	 * "addMessageToApnsConfig", fcmMessage, messageBuilder, 30 ); // then
	 * assertEquals(TYPE_CALL_ATTEND,
	 * fcmMessage.getData().get(FermaxNotificationType)); }
	 */

	/*
	 * @Test public void givenANDROID() throws Exception { // FcmMessage fcmMessage,
	 * Message.Builder messageBuilder, Integer timeToLive FcmMessage fcmMessage =
	 * UtilsTest.getFcmMessage(30); Message.Builder messageBuilder =
	 * Message.builder().putAllData(fcmMessage.getData());
	 * 
	 * // when FcmMessage a = WhiteboxImpl.invokeMethod(fcmService,
	 * "addMessageToData", fcmMessage, messageBuilder, 30 ); // then
	 * assertEquals(TYPE_CALL_ATTEND,
	 * fcmMessage.getData().get(FermaxNotificationType)); }
	 * 
	 * @Test public void givencreateFirebaseMessage() throws Exception { //
	 * FcmMessage fcmMessage, Message.Builder messageBuilder, Integer timeToLive
	 * FcmMessage fcmMessage = UtilsTest.getFcmMessage(30); Message.Builder
	 * messageBuilder = Message.builder().putAllData(fcmMessage.getData());
	 * 
	 * // when FcmMessage a = WhiteboxImpl.invokeMethod(fcmService,
	 * "createFirebaseMessage", fcmMessage,"IOS", new Integer(30) ); // then
	 * assertEquals(TYPE_CALL_ATTEND,
	 * fcmMessage.getData().get(FermaxNotificationType)); }
	 */

}