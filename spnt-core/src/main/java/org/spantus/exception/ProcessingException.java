package org.spantus.exception;

public class ProcessingException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProcessingException() {
		super();
	}
	public ProcessingException(String message) {
		super(message);
	}

	public ProcessingException(Throwable t){
		super(t);
	}
	public ProcessingException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
