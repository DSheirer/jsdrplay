package io.github.dsheirer.sdrplay.internal;

/** Kinds of power overload event: {@code sdrplay_api_PowerOverloadCbEventIdT}. */
public enum OverloadType {
    /** The input is overloaded. */
    DETECTED(0, "sdrplay_api_Overload_Detected"),
    /** The overload has been corrected. */
    CORRECTED(1, "sdrplay_api_Overload_Corrected");

    private final int code;
    private final String nativeName;

    OverloadType(int code, String nativeName) {
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
    public static OverloadType fromCode(int code) {
        for (OverloadType value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown sdrplay_api_PowerOverloadCbEventIdT value: " + code);
    }
}
