package es.fermax.notificationservice.rabbit;

import es.fermax.notificationservice.config.RabbitConfig;
import es.fermax.notificationservice.rabbit.messages.FcmMessageAck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AckMessageSender {
	
	private static final Logger log = LoggerFactory.getLogger(AckMessageSender.class);

    private final RabbitTemplate rabbitTemplate;

	private RabbitConfig rabbitConfig;
	
	@Autowired
    public AckMessageSender(final RabbitTemplate rabbitTemplate, RabbitConfig rabbitConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitConfig = rabbitConfig;
    }

    public boolean sendMessageAck(String fcmMessageId, Integer userId, Boolean attended) {
        FcmMessageAck message = new FcmMessageAck(fcmMessageId, userId, attended);
        log.info("Sending ACK Notification Rabbit message... NotificationId: {}. UserId: {}", fcmMessageId, userId);
        rabbitTemplate.convertAndSend(rabbitConfig.ackNotificationExchange, RabbitConfig.QUEUE_ACK_NOTIFICATION, message);
        return true;
    }
}
