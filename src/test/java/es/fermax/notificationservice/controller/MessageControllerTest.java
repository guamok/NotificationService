package es.fermax.notificationservice.controller;

import es.fermax.fermaxsecurity.UserIDEncryptorService;
import es.fermax.notificationservice.controller.dto.AppTokenDTO;
import es.fermax.notificationservice.controller.dto.MessageAckDTO;
import es.fermax.notificationservice.controller.dto.MessageNotificationTokenDTO;
import es.fermax.notificationservice.controller.dto.MessageNotificationUserDTO;
import es.fermax.notificationservice.rabbit.AckMessageSender;
import es.fermax.notificationservice.service.AppTokenService;
import es.fermax.notificationservice.service.FCMService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class MessageControllerTest {

    private static final String TOKEN = "token";

    @Mock
    AckMessageSender ackMessageSender;

    @Mock
    AppTokenService appTokenService;

    @Mock
    FCMService fcmService;

    @Mock
    UserIDEncryptorService userIDEncryptorService;

    @InjectMocks
    MessageController controller = new MessageController();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @WithMockUser(username = "K7GZBI52M4FE3YKQCSKL5U3HA24A", roles = {"USER"})
    public void acknowledgeMessageDTOTest() {

        // mock
        try {
            given(userIDEncryptorService.decrypt(any())).willReturn("100");
        } catch (Exception e) {
            Assert.fail();
            e.printStackTrace();
        }
        given(ackMessageSender.sendMessageAck(anyString(), anyInt(), anyBoolean())).willReturn(true);

        // when
        ResponseEntity<Object> ackResponse = controller.messageAttend(new MessageAckDTO("fcmid1", true));

        // then
        assertEquals(HttpStatus.OK, ackResponse.getStatusCode());
        assertNotNull(ackResponse.getBody());

    }

    @Test
    @WithMockUser(username = "K7GZBI52M4FE3YKQCSKL5U3HA24A", roles = {"USER"})
    public void givenMessageNotificationByToken_happyPath_thenReturnKO() throws Exception {

        // mock
        MessageNotificationTokenDTO messageDTO = new MessageNotificationTokenDTO();
        messageDTO.setToken(TOKEN);
        AppTokenDTO appTokenDTO = new AppTokenDTO();
        appTokenDTO.setUserId(1);
        List<AppTokenDTO> appTokenList = new ArrayList<AppTokenDTO>();
        appTokenList.add(appTokenDTO);

        //given
        given(userIDEncryptorService.decrypt(any())).willReturn("1");
        given(appTokenService.getAppTokenDTOsByToken(anyString())).willReturn(appTokenList);
        Mockito.doNothing().when(fcmService).sendCustomNotificationMessages(messageDTO);

        // when
        ResponseEntity<Object> response = controller.messageNotificationByToken(messageDTO);

        // then
        verify(userIDEncryptorService, atLeast(1)).decrypt(any());
        verify(appTokenService, atLeast(1)).getAppTokenDTOsByToken(anyString());
        verify(fcmService, atLeast(1)).sendCustomNotificationMessages(messageDTO);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "K7GZBI52M4FE3YKQCSKL5U3HA24A", roles = {"USER"})
    public void givenMessageNotificationByToken_NoToken_thenReturnKO() throws Exception {

        // mock
        MessageNotificationTokenDTO messageDTO = new MessageNotificationTokenDTO();
        messageDTO.setToken(TOKEN);
        AppTokenDTO appTokenDTO = new AppTokenDTO();
        List<AppTokenDTO> appTokenList = new ArrayList<AppTokenDTO>();
        appTokenList.add(appTokenDTO);

        given(userIDEncryptorService.decrypt(any())).willReturn("1");
        given(appTokenService.getAppTokenDTOsByToken(anyString())).willReturn(appTokenList);

        // when
        ResponseEntity<Object> response = controller.messageNotificationByToken(messageDTO);

        // then
        verify(userIDEncryptorService, atLeast(1)).decrypt(any());
        verify(appTokenService, atLeast(1)).getAppTokenDTOsByToken(anyString());
        verify(fcmService, never()).sendCustomNotificationMessages(messageDTO);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "K7GZBI52M4FE3YKQCSKL5U3HA24A", roles = {"USER"})
    public void givenMessageNotificationByToken_Exception_thenReturnKO() throws Exception {

        // mock
        MessageNotificationTokenDTO messageDTO = new MessageNotificationTokenDTO();
        messageDTO.setToken(TOKEN);
        AppTokenDTO appTokenDTO = new AppTokenDTO();
        List<AppTokenDTO> appTokenList = new ArrayList<AppTokenDTO>();
        appTokenList.add(appTokenDTO);

        given(userIDEncryptorService.decrypt(any())).willReturn(TOKEN);
        given(appTokenService.getAppTokenDTOsByToken(anyString())).willReturn(appTokenList);

        // when
        ResponseEntity<Object> response = controller.messageNotificationByToken(messageDTO);

        // then
        verify(userIDEncryptorService, atLeast(1)).decrypt(any());
        verify(appTokenService, never()).getAppTokenDTOsByToken(anyString());
        verify(fcmService, never()).sendCustomNotificationMessages(messageDTO);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "K7GZBI52M4FE3YKQCSKL5U3HA24A", roles = {"USER"})
    public void givenMessageNotificationByUser_ReconciliationFailed_thenReturnKO() throws Exception {

        // mock
        MessageNotificationUserDTO messageDTO = new MessageNotificationUserDTO();
        messageDTO.setUserId(1);

        // when
        ResponseEntity<Object> response = controller.messageNotificationByUser(messageDTO);

        // then
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
    }

    @Test
    @WithMockUser(username = "K7GZBI52M4FE3YKQCSKL5U3HA24A", roles = {"USER"})
    public void givenMessageNotificationByUser_happyPath_thenReturnKO() throws Exception {

        // mock
        MessageNotificationUserDTO messageDTO = new MessageNotificationUserDTO();
        messageDTO.setUserId(1);

        //given
        given(userIDEncryptorService.decrypt(any())).willReturn("1");
        Mockito.doNothing().when(fcmService).sendCustomNotificationMessages(messageDTO);

        // when
        ResponseEntity<Object> response = controller.messageNotificationByUser(messageDTO);

        // then
        verify(userIDEncryptorService, atLeast(1)).decrypt(any());
        verify(fcmService, atLeast(1)).sendCustomNotificationMessages(messageDTO);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}