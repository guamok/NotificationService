package es.fermax.notificationservice.model;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "#{@environment.getProperty('spring.data.mongodb.prefix')}_notification_app_tokens")
public class AppToken {

	@Id
	private String id;

	@NotNull
	@Field(name = "TOKEN")
	private String token;

	@NotNull
	@Field(name = "USER_ID")
	private Integer userId;

	@NotNull
	@Field(name = "LOCALE")
	private String locale = "ES";

	@Field(name = "APP_VERSION")
	private String appVersion;

	@Field(name = "OS")
	private String os;

	@Field(name = "OS_VERSION")
	private String osVersion;

	@CreatedDate
	@Field(name = "CREATION_DATE")
	private Date creationDate;

	@LastModifiedDate
	@Field(name = "UPDATE_DATE")
	private Date updateDate;

	@NotNull
	@Field(name = "ACTIVE")
	private Boolean active = Boolean.TRUE;

	public AppToken(Integer userId, String token) {
		this.userId = userId;
		this.token = token;
	}
}
