package es.fermax.notificationservice.model;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

	@Field(name = "notification_title")
	private String title;

	@Field(name = "notification_body")
	private String body;

	@Field(name = "notification_image_url")
	private String imageUrl;
}
