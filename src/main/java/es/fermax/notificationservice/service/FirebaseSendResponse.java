package es.fermax.notificationservice.service;

import com.google.firebase.messaging.MessagingErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FirebaseSendResponse {
	
    boolean isSent;
    String fcmId;
    String failureReason;
    MessagingErrorCode errorCode;

}
