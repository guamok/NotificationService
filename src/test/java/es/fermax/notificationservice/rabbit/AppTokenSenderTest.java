package es.fermax.notificationservice.rabbit;

import es.fermax.notificationservice.config.RabbitConfig;
import es.fermax.notificationservice.rabbit.messages.AppTokenMessage;
import es.fermax.notificationservice.utils.UtilsTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class AppTokenSenderTest {


	@Mock
	RabbitConfig rabbitConfig;

	@Mock
	RabbitTemplate rabbitTemplate;

	AppTokenSender appTokenSender;

	@Before
	public void setup() {
		rabbitConfig.apptokenExchange="any";
		appTokenSender = new AppTokenSender(rabbitTemplate,rabbitConfig);
	}
	
	@Test 
    public void givenSendMessage_happyPath_ThenConvertAndSendOK() {
		// Object
		AppTokenMessage appTokenMessage = UtilsTest.getAppTokenMessage();

        //mock
		Mockito.doNothing().when(rabbitTemplate).convertAndSend(eq("any"),eq("apptoken-notification-queue"),eq(appTokenMessage));

        //when
		appTokenSender.sendMessage(1,"token", "os", "osVersion", "appVersion", "locale",true);

        //then
        verify(rabbitTemplate, atLeast(1)).convertAndSend(eq("any"),eq("apptoken-notification-queue"),eq(appTokenMessage));
    }

}
