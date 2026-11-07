package io.github.dsheirer.sdrplay;

/** Local oscillator mode of a tuner, which sets the first LO frequency used for up-conversion: {@code sdrplay_api_LoModeT}. */
public enum LoMode {
    /** Not set. */
    UNDEFINED(0, "sdrplay_api_LO_Undefined"),
    /** Chosen by the API, the default. */
    AUTO(1, "sdrplay_api_LO_Auto"),
    /** 120 MHz. */
    MHZ_120(2, "sdrplay_api_LO_120MHz"),
    /** 144 MHz. */
    MHZ_144(3, "sdrplay_api_LO_144MHz"),
    /** 168 MHz. */
    MHZ_168(4, "sdrplay_api_LO_168MHz");

    private final int code;
    private final String nativeName;

    LoMode(int code, String nativeName) {
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
     * Returns the value for an API code, or {@link #UNDEFINED} for an unknown code.
     *
     */
    public static LoMode fromCode(int code) {
        for (LoMode value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return UNDEFINED;
    }
}
