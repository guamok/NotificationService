package es.fermax.notificationservice.rabbit;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.fermax.notificationservice.config.RabbitConfig;
import es.fermax.notificationservice.model.AppToken;
import es.fermax.notificationservice.controller.dto.AppTokenDTO;
import es.fermax.notificationservice.rabbit.messages.AppTokenMessage;
import es.fermax.notificationservice.service.AppTokenService;
import es.fermax.notificationservice.service.FCMService;

@Service
public class AppTokenListener {
	
	@Autowired
	public
	AppTokenService appTokenService;

	@Autowired
	FCMService fcmService;
	
	public static final Logger log = LoggerFactory.getLogger(AppTokenListener.class);
	
	@RabbitListener(queues = RabbitConfig.QUEUE_APPTOKEN)
	public void receiveMessage(final AppTokenMessage appTokenMessage) {
		log.info("Received APPTOKEN Rabbit message {}", appTokenMessage);
		if(appTokenMessage.getActive() == null || appTokenMessage.getActive()){
			List<AppToken> appTokensByUser = appTokenService.getTokensByUser(appTokenMessage.getUserId());
			if(appTokensByUser.isEmpty()) {
				fcmService.sendRegistrationConfirmNotification(new AppTokenDTO(appTokenMessage));
			}else {
				List<AppToken> appActiveTokensByUser = appTokenService.getActiveTokens(appTokenMessage.getUserId());
				appActiveTokensByUser.stream().filter(token -> !token.getToken().equalsIgnoreCase(appTokenMessage.getToken()))
				.forEach(token -> fcmService.sendLoginAnotherDeviceNotification(new AppTokenDTO(token), appTokenMessage.getOs()));
			}
			appTokenService.deactivateOldTokens(appTokenMessage);
		}
		appTokenService.storeAppToken(appTokenMessage);
	}

}
