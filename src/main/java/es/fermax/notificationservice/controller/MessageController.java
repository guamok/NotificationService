package es.fermax.notificationservice.controller;

import es.fermax.fermaxsecurity.UserIDEncryptorService;
import es.fermax.notificationservice.exception.UserIdForAdminMandatoryException;
import es.fermax.notificationservice.exception.UserNotExistException;
import es.fermax.notificationservice.controller.dto.AppTokenDTO;
import es.fermax.notificationservice.controller.dto.MessageAckDTO;
import es.fermax.notificationservice.controller.dto.MessageNotificationTokenDTO;
import es.fermax.notificationservice.controller.dto.MessageNotificationUserDTO;
import es.fermax.notificationservice.rabbit.AckMessageSender;
import es.fermax.notificationservice.service.AppTokenService;
import es.fermax.notificationservice.service.FCMService;
import es.fermax.notificationservice.util.UserUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = {"api/v1/"})
public class MessageController extends AController {


    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    UserIDEncryptorService userIDEncryptorService;

    @Autowired
    AckMessageSender ackMessageSender;

    @Autowired
    FCMService fcmService;

    @Autowired
    AppTokenService appTokenService;

    @ApiOperation(value = "Firebase Messaging ACK")
    @PostMapping(value = "/message/ack")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = OK, response = String.class),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)
    })
    public ResponseEntity<Object> messageAttend(@Valid @RequestBody MessageAckDTO messageDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Integer userId = Integer.valueOf(userIDEncryptorService.decrypt(authentication.getName()));
            log.info("Acknowldege FCM Message with ID {}", messageDTO.getFcmMessageId());
            ackMessageSender.sendMessageAck(messageDTO.getFcmMessageId(), userId, messageDTO.getAttended());
            return new ResponseEntity<>("Message Acknowledged", HttpStatus.OK);
        } catch (Exception e) {
            log.error(INTERNAL_SERVER_ERROR, e);
            return new ResponseEntity<>(ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Firebase Messaging Notification")
    @PostMapping(value = "/message/notification/token")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = OK, response = String.class),
            @ApiResponse(code = 400, message = BAD_PARAMETERS),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)
    })
    public ResponseEntity<Object> messageNotificationByToken(@Valid @RequestBody MessageNotificationTokenDTO messageDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Integer tokenUserId = 0;
            Integer userId = 0;
            if (!UserUtils.isRoleAuthorized(authentication)) {
                userId = Integer.valueOf(userIDEncryptorService.decrypt(authentication.getName()));
                if (!StringUtils.isEmpty(messageDTO.getToken())) {
                    List<AppTokenDTO> appTokenList = appTokenService.getAppTokenDTOsByToken(messageDTO.getToken());
                    if (!appTokenList.isEmpty()) {
                        tokenUserId = appTokenList.get(0).getUserId();
                    }
                }
            }
            if (!UserUtils.isRoleAuthorized(authentication) && !userId.equals(tokenUserId) || StringUtils.isEmpty(messageDTO.getToken())) {
                return new ResponseEntity<>(ERROR, HttpStatus.BAD_REQUEST);
            }
            log.info("Send new FCM Message to {}", StringUtils.isEmpty(messageDTO.getToken()));
            fcmService.sendCustomNotificationMessages(messageDTO);
            return new ResponseEntity<>("Message Sent", HttpStatus.OK);


        } catch (Exception e) {
            log.error(INTERNAL_SERVER_ERROR, e);
            return new ResponseEntity<>(ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Firebase Messaging Notification")
    @PostMapping(value = "/message/notification/user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = OK, response = String.class),
            @ApiResponse(code = 400, message = BAD_PARAMETERS),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR)
    })
    public ResponseEntity<Object> messageNotificationByUser(@Valid @RequestBody MessageNotificationUserDTO messageDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            messageDTO.setUserId(UserUtils.retrieveUserId(authentication, messageDTO.getUserId(), userIDEncryptorService));
            fcmService.sendCustomNotificationMessages(messageDTO);
            return new ResponseEntity<>("Message Sent", HttpStatus.OK);

        } catch (UserIdForAdminMandatoryException | UserNotExistException userException) {
            log.error(USER_ERROR, userException);
            return new ResponseEntity<>(USER_ERROR, HttpStatus.PRECONDITION_FAILED);

        } catch (Exception e) {
            log.error(INTERNAL_SERVER_ERROR, e);
            return new ResponseEntity<>(ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
