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
import es.fermax.notificationservice.rabbit.messages.AddInviteeMessage;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class AddInviteeSenderTest {


	@Mock
	RabbitConfig rabbitConfig;

	@Mock
	RabbitTemplate rabbitTemplate;

	AddInviteeSender addInviteeSender;

	@Before
	public void setup() {
		rabbitConfig.addInviteeExchange="any";
		addInviteeSender = new AddInviteeSender(rabbitTemplate,rabbitConfig);
	}
	
	@Test 
    public void givenSendMessage_happyPath_ThenConvertAndSendOK() {
		// Object
		AddInviteeMessage addInviteeMessage = new AddInviteeMessage("token","room");

        //mock
		Mockito.doNothing().when(rabbitTemplate).convertAndSend(eq("any"),eq(""),eq(addInviteeMessage));

        //when
		addInviteeSender.sendMessage(addInviteeMessage.getTokenId(),addInviteeMessage.getRoomId());

        //then
        verify(rabbitTemplate, atLeast(1)).convertAndSend(eq("any"),eq(""),eq(addInviteeMessage));
    }

}
