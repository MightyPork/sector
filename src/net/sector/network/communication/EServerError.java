package net.sector.network.communication;


/**
 * Enum of server errors, for easier handling than numbers.
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public enum EServerError {

// * add level errors:
//
//	"FILE_NOT_FOUND" 		7  (level file added does not exist - ADD_LEVEL)
//	"BAD_FILE_FORMAT" 		8  (level file added is not XML - ADD_LEVEL)
//	"LEVEL_ALREADY_ADDED" 	9  (level file already registered in database - ADD_LEVEL)
//	"LEVEL_NAME_NOT_UNIQUE" 10 (title of the added level is already used by another level - ADD_LEVEL)

	/** no command requested - cmd is empty */
	NO_COMMAND(0),
	/** command requested does not exist or is disabled */
	INVALID_COMMAND(1),
	/** MySQL syntax or script error */
	INTERNAL_ERROR(2),
	/** missing some required arguments */
	INCOMPLETE_COMMAND(3),
	/** Entered user name already taken - REGISTER */
	NAME_NOT_UNIQUE(4),
	/** Name or password incorrect - LOG_IN */
	LOGIN_FAILED(5),
	/** bad token sent */
	INVALID_TOKEN(6),
	/** no level with such LID exists - GET_SCORES or SUBMIT_SCORE */
	NO_SUCH_LEVEL(11),
	/** all other errors that are not important to be distinguished */
	UNKNOWN_ERROR(-1),
	/** Bad response from server, which caused local error */
	INVALID_SERVER_RESPONSE(100);

	private EServerError(int code) {
		this.code = code;
	}

	/**
	 * Get error enum for server error code
	 * 
	 * @param code the error code
	 * @return corresponding enum constant
	 */
	public static EServerError getErrorForCode(int code) {
		for (EServerError e : values()) {
			if (e.code == code) return e;
		}
		return UNKNOWN_ERROR;
	}

	private int code = -1;
}
