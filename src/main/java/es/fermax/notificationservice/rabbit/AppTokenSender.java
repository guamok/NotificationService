package es.fermax.notificationservice.rabbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.fermax.notificationservice.config.RabbitConfig;
import es.fermax.notificationservice.rabbit.messages.AppTokenMessage;

@Service
public class AppTokenSender {
	
	private static final Logger log = LoggerFactory.getLogger(AppTokenSender.class);

    private final RabbitTemplate rabbitTemplate;

	private RabbitConfig rabbitConfig;
	
	@Autowired
    public AppTokenSender(final RabbitTemplate rabbitTemplate, RabbitConfig rabbitConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitConfig = rabbitConfig;
    }

	public void sendMessage(Integer userId, String token, String os, String osVersion, String appVersion,
			String locale, Boolean active) {
		AppTokenMessage appTokenMessage =  new AppTokenMessage(token, userId, locale, appVersion, os, osVersion, active);
        log.info("Sending AppToken Rabbit message...");
        rabbitTemplate.convertAndSend(rabbitConfig.apptokenExchange, RabbitConfig.QUEUE_APPTOKEN, appTokenMessage);		
	}
}
