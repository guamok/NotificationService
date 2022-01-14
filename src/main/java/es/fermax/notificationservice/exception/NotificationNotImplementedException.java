package es.fermax.notificationservice.exception;

public class NotificationNotImplementedException extends RuntimeException {

	private static final long serialVersionUID = -377409437911830804L;
	private static final String NOT_IMPLEMENTED_NOTIFICATION = "Not implemented message content";

	public NotificationNotImplementedException() {
		super(NOT_IMPLEMENTED_NOTIFICATION);
	}
}