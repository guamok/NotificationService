package es.fermax.notificationservice.model;

public class NotificationBody {
	public static final String REGISTRATION_CONFIRM = "Welcome_Notification_Body";
	public static final String MULTIPLE_LOGIN = "Multiple_Login_Notification_Body";
	
	private NotificationBody() {
	    throw new IllegalStateException("NotificationTitle Utility class");
	}
}
