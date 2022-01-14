package es.fermax.notificationservice.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageAckDTO {

    @ApiModelProperty(value = "FCM_MESSAGE_ID", required = true)
    String fcmMessageId;

    @ApiModelProperty(value = "ATTENDED", required = true)
    Boolean attended;

}
