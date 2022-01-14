package es.fermax.notificationservice.rabbit.messages;

import java.io.Serializable;

import es.fermax.notificationservice.model.AppToken;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppTokenMessage implements Serializable {
	
	private static final long serialVersionUID = -358130396050802145L;
	private String token;
	private Integer userId;
	private String locale;
	private String appVersion;
	private String os;
	private String osVersion;
	private Boolean active;
	
	public AppTokenMessage(AppToken token) {
		this.token = token.getToken();
		this.userId = token.getUserId();
		this.locale = token.getLocale();
		this.appVersion = token.getAppVersion();
		this.os = token.getOs();
		this.osVersion = token.getOsVersion();
		this.active = token.getActive();
	}

}
