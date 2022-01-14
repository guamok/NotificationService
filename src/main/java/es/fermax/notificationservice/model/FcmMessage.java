package es.fermax.notificationservice.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import es.fermax.notificationservice.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "#{@environment.getProperty('spring.data.mongodb.prefix')}_notification_fcm_messages")
public class FcmMessage {

	@Id
	private String id;

	@Field(name = "FCM_ID")
	private String fcmId;

	@Field(name = "NOTIFICATION")
	private Notification notification;

	@NotNull
	@Builder.Default
	@Field(name = "DELIVERY_STATUS")
	private String deliveryStatus = StatusEnum.DRAFT.status;

	@Field(name = "ERROR_DESC")
	private String errorDesc;

	@Field(name = "ANDROID_CONFIG")
	private String androidConfig;

	@Field(name = "APNS_CONFIG")
	private String apnsConfig;

	@Field(name = "WEB_PUSH_CONFIG")
	private String webPushConfig;

	@Field(name = "FCM_OPTIONS_LABEL")
	private String fcmOptionsLabel;

	@Builder.Default
	@Field("DATA_ENTRY")
	Map<String, String> data = new HashMap<>();

	@NotNull
	@Field(name = "TARGET")
	private String target;

	@NotNull
	@Field(name = "TARGET_VALUE")
	private String targetValue;

	@CreatedDate
	@Field(name = "CREATION_DATE")
	private Date creationDate;

	@LastModifiedDate
	@Field(name = "UPDATE_DATE")
	private Date updateDate;
}
