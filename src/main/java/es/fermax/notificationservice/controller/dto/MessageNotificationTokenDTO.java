package es.fermax.notificationservice.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageNotificationTokenDTO {

    @ApiModelProperty(value = "TOKEN", required = true)
    String token;

    @ApiModelProperty(value = "TITLE", required = true)
    String title;
    
    @ApiModelProperty(value = "BODY", required = true)
    String body;

}
