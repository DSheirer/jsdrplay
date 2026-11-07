package io.github.dsheirer.sdrplay;

/** Amount of debug output the API writes to its log: {@code sdrplay_api_DbgLvl_t}. */
public enum DebugLevel {
    /** No debug output. */
    DISABLED(0, "sdrplay_api_DbgLvl_Disable"),
    /** Everything. */
    VERBOSE(1, "sdrplay_api_DbgLvl_Verbose"),
    /** Warnings and errors. */
    WARNING(2, "sdrplay_api_DbgLvl_Warning"),
    /** Errors only. */
    ERROR(3, "sdrplay_api_DbgLvl_Error"),
    /** Messages. */
    MESSAGE(4, "sdrplay_api_DbgLvl_Message");

    private final int code;
    private final String nativeName;

    DebugLevel(int code, String nativeName) {
        this.code = code;
        this.nativeName = nativeName;
    }

    /** Returns the value the API uses. */
    public int code() {
        return code;
    }

    /** Returns the C name of the value. */
    public String nativeName() {
        return nativeName;
    }

    /**
     * Returns the value for an API code.
     *
     * @throws IllegalArgumentException if the code is unknown
     */
    public static DebugLevel fromCode(int code) {
        for (DebugLevel value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown sdrplay_api_DbgLvl_t value: " + code);
    }
}
