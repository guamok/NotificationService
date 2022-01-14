package es.fermax.notificationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class UserIdForAdminMandatoryException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7421961469614340739L;
	private static final String MESSAGE = "userId is Mandatory for admin users: ";

	public UserIdForAdminMandatoryException() {
		super(MESSAGE);
	}

}
