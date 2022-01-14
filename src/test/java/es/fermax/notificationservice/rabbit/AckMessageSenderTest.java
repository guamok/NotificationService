package es.fermax.notificationservice.rabbit;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import es.fermax.notificationservice.config.RabbitConfig;
import es.fermax.notificationservice.rabbit.messages.FcmMessageAck;
import es.fermax.notificationservice.utils.UtilsTest;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class AckMessageSenderTest {


	@Mock
	RabbitConfig rabbitConfig;

	@Mock
	RabbitTemplate rabbitTemplate;

	AckMessageSender ackMessageSenderSender;

	@Before
	public void setup() {
		rabbitConfig.ackNotificationExchange="any";
		ackMessageSenderSender = new AckMessageSender(rabbitTemplate,rabbitConfig);
	}
	
	@Test 
    public void givenSendMessage_happyPath_ThenConvertAndSendOK() {
		// Object
		FcmMessageAck fcmMessageAck = UtilsTest.getFcmMessageAck();

        //mock
		Mockito.doNothing().when(rabbitTemplate).convertAndSend(eq("any"),eq("ack-notification-queue"),eq(fcmMessageAck));

        //when
		ackMessageSenderSender.sendMessageAck(fcmMessageAck.getFirebaseMessageId(),fcmMessageAck.getUserId(),fcmMessageAck.getAttended());

        //then
        verify(rabbitTemplate, atLeast(1)).convertAndSend(eq("any"),eq("ack-notification-queue"),eq(fcmMessageAck));
    }

}
