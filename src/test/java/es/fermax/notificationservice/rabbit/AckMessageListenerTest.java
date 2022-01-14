package es.fermax.notificationservice.rabbit;

import es.fermax.notificationservice.model.DataKeys;
import es.fermax.notificationservice.model.FcmMessage;
import es.fermax.notificationservice.enums.StatusEnum;
import es.fermax.notificationservice.enums.TargetEnum;
import es.fermax.notificationservice.rabbit.messages.FcmMessageAck;
import es.fermax.notificationservice.repo.FcmMessageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class AckMessageListenerTest {

    @Mock
    FcmMessageRepository repository;



    @InjectMocks
    AckMessageListener ackMessageListener = new AckMessageListener();


    @Test
    public void receiveAckMessageTest() {
        final String fcmId = "firebasemessageid1";

        FcmMessage existingMessage = new FcmMessage();
        existingMessage.setDeliveryStatus(StatusEnum.SENT.status);
        existingMessage.setFcmId(fcmId);
        existingMessage.setTarget(TargetEnum.TOKEN.target);
        existingMessage.setTargetValue("token1");

        given(repository.findByFcmId(fcmId)).willReturn( Optional.of(existingMessage));

        // when
        ackMessageListener.receiveAckMessage(new FcmMessageAck(fcmId, 1000, true ));

        // then
        assertEquals( StatusEnum.ATTENDED.status, existingMessage.getDeliveryStatus() );

    }

    @Test
    public void receiveAckMessage_IncommingCall_Test() {
        final String fcmId = "firebasemessageid1";

        FcmMessage existingMessage = new FcmMessage();
        existingMessage.setDeliveryStatus(StatusEnum.SENT.status);
        existingMessage.setFcmId(fcmId);
        existingMessage.setTarget(TargetEnum.TOKEN.target);
        existingMessage.setTargetValue("token1");
        existingMessage.getData().put(DataKeys.ROOM_ID, "room-123");
        existingMessage.getData().put(DataKeys.TYPE, DataKeys.TYPE_CALL);


        given(repository.findByFcmId(fcmId)).willReturn( Optional.of(existingMessage));

        // when
        ackMessageListener.receiveAckMessage(new FcmMessageAck(fcmId, 1000, true ));

        // then
        assertEquals( StatusEnum.ATTENDED.status, existingMessage.getDeliveryStatus() );
        verify(repository, atLeast(1)).findByFcmId( fcmId );
        verify(repository, atLeast(1)).save( any() );

    }



}