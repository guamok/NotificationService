package es.fermax.notificationservice.rabbit.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FcmMessageAck {

    String firebaseMessageId;
    Integer userId;
    Boolean attended;
}
