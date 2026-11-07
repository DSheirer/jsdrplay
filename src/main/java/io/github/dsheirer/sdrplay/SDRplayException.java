package io.github.dsheirer.sdrplay;

/** Thrown when an SDRplay API function reports an error, or when the API cannot be used at all. */
public class SDRplayException extends Exception {
    private static final long serialVersionUID = 1L;

    private final int code;

    /**
     * Creates an exception for a failed API function.
     *
     * @param function the C name of the function
     * @param code the {@code sdrplay_api_ErrT} it returned
     * @param detail the API's description of the error, or null
     */
    SDRplayException(String function, int code, String detail) {
        super(message(function, code, detail));
        this.code = code;
    }

    /** Creates an exception for one of the jsdrplay errors, such as {@link SDRplayError#API_NOT_FOUND}. */
    SDRplayException(SDRplayError error, String message, Throwable cause) {
        super(message, cause);
        this.code = error.code();
    }

    private static String message(String function, int code, String detail) {
        SDRplayError error = SDRplayError.fromCode(code);
        String name = error.nativeName() == null ? "unknown error" : error.nativeName();
        String text = function + " failed: " + name + " (" + code + ")";
        return detail == null || detail.isBlank() ? text : text + ": " + detail;
    }

    /** Returns the {@code sdrplay_api_ErrT} value, or the negative code of a jsdrplay error. */
    public int code() {
        return code;
    }

    /** Returns the error as an enum, or {@link SDRplayError#OTHER} for an unknown code. */
    public SDRplayError error() {
        return SDRplayError.fromCode(code);
    }
}
