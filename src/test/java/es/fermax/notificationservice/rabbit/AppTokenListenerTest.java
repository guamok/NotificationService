package es.fermax.notificationservice.rabbit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import es.fermax.notificationservice.model.AppToken;
import es.fermax.notificationservice.rabbit.messages.AppTokenMessage;
import es.fermax.notificationservice.service.AppTokenService;
import es.fermax.notificationservice.service.FCMService;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class AppTokenListenerTest {
	
	@Mock
	AppTokenService appTokenService;

	@Mock
	FCMService fcmService;
	
	@InjectMocks
	AppTokenListener appTokenListenerService = new AppTokenListener();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test 
    public void givenAppTokenMessageNotPresent_whenReceiveMessage_ThenSaveOK() throws Exception {
		
        //mock
		given(appTokenService.getTokensByUser(anyInt())).willReturn(Collections.emptyList());
		given(appTokenService.getAppToken(anyString(),anyInt())).willReturn(Optional.empty());
		
        //when
        appTokenListenerService.receiveMessage(new AppTokenMessage("123abc", 1, null, null, null, null, true));

        //then
        verify(appTokenService, atLeast(1)).storeAppToken(any());
    }
	
	@Test 
    public void givenAppTokenMessagePresent_whenReceiveMessage_ThenSaveOK() throws Exception {
		
        //mock
		given(appTokenService.getTokensByUser(anyInt())).willReturn(Collections.emptyList());
		given(appTokenService.getAppToken(anyString(),anyInt())).willReturn(Optional.of(new AppToken(1, "123abc")));
        //when
        appTokenListenerService.receiveMessage(new AppTokenMessage("123abc", 1, null, null, null, null, true));

        //then
        verify(appTokenService, atLeast(1)).storeAppToken(any());
    }
	
	@Test(expected = NullPointerException.class)
    public void givenAppTokenMessagePresent_whenReceiveMessage_ThenSaveKO() throws Exception {
		
        //mock
		given(appTokenService.getTokensByUser(anyInt())).willThrow(NullPointerException.class);
        
        //when
        appTokenListenerService.receiveMessage(new AppTokenMessage("123abc", 1, null, null, null, null,true));

        //then
        verify(appTokenService, times(0)).save(any());
    }
}
