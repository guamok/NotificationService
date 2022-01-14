package es.fermax.notificationservice.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.BDDMockito.given;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import es.fermax.notificationservice.controller.dto.AppTokenDTO;
import es.fermax.notificationservice.service.AppTokenService;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class AppTokenControllerTests {

	@Mock
	private AppTokenService appTokenService;
	
	@InjectMocks
	AppTokenController apkTokenController = new AppTokenController();
	
	private AppTokenDTO appTokenDTO;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		appTokenDTO = new AppTokenDTO("token", 1, "es", "1.15", "Android", "2.25", true);
	}

	@Test 
    public void givenException_whenUpdateAppTokenThenReturnKO() throws Exception {
        //mock
        when(appTokenService.updateAppToken(any())).thenThrow(NullPointerException.class);
        //when
        			
        ResponseEntity<Object> tokenResponse = apkTokenController.updateAppToken(appTokenDTO);

        //then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, tokenResponse.getStatusCode());
    }
	@Test 
	@WithMockUser(username = "K7GZBI52M4FE3YKQCSKL5U3HA24A", roles = {"ADMIN"})
    public void givenToken_whenUpdateUserAppToken_ThenReturnOK() throws Exception {
        //mock
		given(appTokenService.updateAppToken(any())).willReturn(true);
        //when
        ResponseEntity<Object> tokenResponse = apkTokenController.updateAppToken(appTokenDTO);

        //then
        assertEquals(HttpStatus.OK, tokenResponse.getStatusCode());
        assertNotNull(tokenResponse.getBody());
    }
	
	@Test 
	@WithMockUser(username = "K7GZBI52M4FE3YKQCSKL5U3HA24A", roles = {"ADMIN"})
    public void givenNullUser_whenUpdateUserAppToken_ThenReturnKO() throws Exception {
        //mock
		given(appTokenService.updateAppToken(any())).willReturn(true);
        //when
		appTokenDTO.setUserId(null);
        ResponseEntity<Object> tokenResponse = apkTokenController.updateAppToken(appTokenDTO);

        //then
        assertEquals(HttpStatus.PRECONDITION_FAILED, tokenResponse.getStatusCode());
        assertNotNull(tokenResponse.getBody());
    }
	
	@Test 
    public void givenException_whenUpdateUserAppTokenThenReturn500() throws Exception {
        //mock
        when(appTokenService.updateAppToken(any())).thenThrow(NullPointerException.class);
        //when
        ResponseEntity<Object> tokenResponse = apkTokenController.updateAppToken(appTokenDTO);

        //then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, tokenResponse.getStatusCode());
    }
}

