package es.fermax.notificationservice.controller.dto;

import es.fermax.notificationservice.model.AppToken;
import es.fermax.notificationservice.rabbit.messages.AppTokenMessage;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppTokenDTO implements Serializable {

    private static final long serialVersionUID = -6332310442227376992L;

    @ApiModelProperty(value = "TOKEN", required = true)
    private String token;

    @ApiModelProperty(value = "USER_ID", example = "0")
    private Integer userId;

    @ApiModelProperty(value = "LOCALE", example = "ES")
    private String locale;

    @ApiModelProperty(value = "APP_VERSION")
    private String appVersion;

    @ApiModelProperty(value = "OS")
    private String os;

    @ApiModelProperty(value = "OS_VERSION")
    private String osVersion;

    @ApiModelProperty(value = "ACTIVE")
    private Boolean active;

    public AppTokenDTO(AppToken token) {
        this.token = token.getToken();
        this.userId = token.getUserId();
        this.locale = token.getLocale();
        this.appVersion = token.getAppVersion();
        this.os = token.getOs();
        this.osVersion = token.getOsVersion();
        this.active = token.getActive();
    }

    public AppTokenDTO(AppTokenMessage token) {
        this.token = token.getToken();
        this.userId = token.getUserId();
        this.locale = token.getLocale();
        this.appVersion = token.getAppVersion();
        this.os = token.getOs();
        this.osVersion = token.getOsVersion();
        this.active = token.getActive();
    }

}
