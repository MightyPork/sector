package net.sector.network.communication;


/**
 * Error returned from the server
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class ServerError extends Exception {
	private EServerError error;
	private String cause;

	/**
	 * Server-side error
	 * 
	 * @param code error code
	 * @param message error message
	 * @param cause error cause (additional info for error)
	 */
	public ServerError(int code, String message, String cause) {
		super(message);
		this.cause = cause;
		error = EServerError.getErrorForCode(code);
	}

	/**
	 * Server-side error
	 * 
	 * @param code error code
	 * @param message error message
	 */
	public ServerError(int code, String message) {
		super(message);
		error = EServerError.getErrorForCode(code);
	}

	/**
	 * Get error
	 * 
	 * @return error
	 */
	public EServerError getError() {
		return error;
	}

	@Override
	public String getMessage() {
		return super.getMessage() + (getDescription().length() > 0 ? " - " + getDescription() : "");
	}

	/**
	 * Get aditional error description.
	 * 
	 * @return text
	 */
	public String getDescription() {
		return cause;
	}

	@Override
	@Deprecated
	public Throwable getCause() {
		return super.getCause();
	}
}
