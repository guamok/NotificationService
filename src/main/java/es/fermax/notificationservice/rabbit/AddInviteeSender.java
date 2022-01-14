package es.fermax.notificationservice.rabbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.fermax.notificationservice.config.RabbitConfig;
import es.fermax.notificationservice.rabbit.messages.AddInviteeMessage;

@Service
public class AddInviteeSender {
	
	private static final Logger log = LoggerFactory.getLogger(AddInviteeSender.class);

    private final RabbitTemplate rabbitTemplate;

	private RabbitConfig rabbitConfig;
	
	@Autowired
    public AddInviteeSender(final RabbitTemplate rabbitTemplate, RabbitConfig rabbitConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitConfig = rabbitConfig;
    }

	public void sendMessage(String token, String roomId) {
		AddInviteeMessage addInviteeMessage =  new AddInviteeMessage(token, roomId);
        log.info("Sending AddInvitee Rabbit message...");
        rabbitTemplate.convertAndSend(rabbitConfig.addInviteeExchange, "", addInviteeMessage);		
	}
}
