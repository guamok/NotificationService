package es.fermax.notificationservice.rabbit;

import es.fermax.notificationservice.config.RabbitConfig;
import es.fermax.notificationservice.enums.TargetEnum;
import es.fermax.notificationservice.model.DataKeys;
import es.fermax.notificationservice.model.FcmMessage;
import es.fermax.notificationservice.enums.StatusEnum;
import es.fermax.notificationservice.rabbit.messages.FcmMessageAck;
import es.fermax.notificationservice.repo.FcmMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AckMessageListener {

	private static final Logger log = LoggerFactory.getLogger(AckMessageListener.class);

	@Autowired
	FcmMessageRepository repository;


	/**
	 * Receive Event of Message reception on Mobile device
	 *
	 * @param message ack message
	 */
	@RabbitListener(queues = RabbitConfig.QUEUE_ACK_NOTIFICATION)
	public void receiveAckMessage(final FcmMessageAck message) {
		log.info("Received NOTIFICATION ACK Rabbit message");

		Optional<FcmMessage> fcmMessageOptional = repository.findByFcmId(message.getFirebaseMessageId());

		if (fcmMessageOptional.isPresent()) {
			FcmMessage fcmMessage = fcmMessageOptional.get();
			if ((Boolean.TRUE.equals(message.getAttended()))) {
				fcmMessage.setDeliveryStatus(StatusEnum.ATTENDED.status);
			}

			repository.save(fcmMessage);

		} else {
			log.error("Not found the message to acknowledge {} for user {}", message.getFirebaseMessageId(), message.getUserId());
		}
	}
}
