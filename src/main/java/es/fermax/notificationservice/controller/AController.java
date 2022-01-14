package es.fermax.notificationservice.controller;


/**
 * Common constants
 */
public abstract class AController {

    protected static final String TOKEN_NOT_FOUND = "Token not found";
    protected static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    protected static final String OK = "OK";
    protected static final String USER_ERROR = "User not found";
    protected static final String SUBSCRIBER_ERROR = "Subscriber not found";

    protected static final String BAD_PARAMETERS = "Wrong Parameters";
    protected static final String ERROR = "Fermax Notification Service Error";
    
    protected AController() {
    }
}
