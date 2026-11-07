package io.github.dsheirer.sdrplay;

/** Automatic gain control loop: {@code sdrplay_api_AgcControlT}. */
public enum AgcMode {
    /** AGC off: the gain reduction set by the application applies. */
    DISABLED(0, "sdrplay_api_AGC_DISABLE"),
    /** AGC with a 100 Hz loop bandwidth. */
    HZ_100(1, "sdrplay_api_AGC_100HZ"),
    /** AGC with a 50 Hz loop bandwidth, the default. */
    HZ_50(2, "sdrplay_api_AGC_50HZ"),
    /** AGC with a 5 Hz loop bandwidth. */
    HZ_5(3, "sdrplay_api_AGC_5HZ"),
    /** AGC using the attack, decay and threshold values of {@link RxChannel#setAgc(AgcMode, int, int, int, int, int)}. */
    CONTROL_ENABLED(4, "sdrplay_api_AGC_CTRL_EN");

    private final int code;
    private final String nativeName;

    AgcMode(int code, String nativeName) {
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
    public static AgcMode fromCode(int code) {
        for (AgcMode value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown sdrplay_api_AgcControlT value: " + code);
    }
}
