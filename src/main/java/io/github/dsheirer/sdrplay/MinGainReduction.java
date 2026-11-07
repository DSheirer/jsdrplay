package io.github.dsheirer.sdrplay;

/** Lower limit of the baseband gain reduction: {@code sdrplay_api_MinGainReductionT}. */
public enum MinGainReduction {
    /** Gain reduction down to 0 dB. */
    EXTENDED(0, "sdrplay_api_EXTENDED_MIN_GR"),
    /** Gain reduction down to 20 dB, the default. */
    NORMAL(20, "sdrplay_api_NORMAL_MIN_GR");

    private final int code;
    private final String nativeName;

    MinGainReduction(int code, String nativeName) {
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
    public static MinGainReduction fromCode(int code) {
        for (MinGainReduction value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown sdrplay_api_MinGainReductionT value: " + code);
    }
}
