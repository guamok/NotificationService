package es.fermax.notificationservice.exception;

public class TargetNotImplementedException extends RuntimeException {

	private static final long serialVersionUID = -377409437911830804L;
	private static final String NOT_IMPLEMENTED_TARGET = "Not implemented message target";

	public TargetNotImplementedException() {
		super(NOT_IMPLEMENTED_TARGET);
	}
}