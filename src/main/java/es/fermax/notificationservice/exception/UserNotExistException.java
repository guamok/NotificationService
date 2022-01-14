package es.fermax.notificationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotExistException extends RuntimeException {

	private static final long serialVersionUID = -7329808526963275331L;
	private static final String USER_NOT_EXISTS = "User not exists";

	public UserNotExistException() {
		super(USER_NOT_EXISTS);
	}
}