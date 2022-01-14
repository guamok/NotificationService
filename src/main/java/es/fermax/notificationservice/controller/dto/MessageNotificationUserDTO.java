package es.fermax.notificationservice.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageNotificationUserDTO {

    @ApiModelProperty(value = "USER_ID", required = false)
    Integer userId;

    @ApiModelProperty(value = "TITLE", required = true)
    String title;
    
    @ApiModelProperty(value = "BODY", required = true)
    String body;

}
