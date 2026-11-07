package io.github.dsheirer.sdrplay;

/** Intermediate frequency of a tuner: {@code sdrplay_api_If_kHzT}. */
public enum IfMode {
    /** Not set. */
    UNDEFINED(-1, "sdrplay_api_IF_Undefined"),
    /** Zero IF, the default. */
    ZERO(0, "sdrplay_api_IF_Zero"),
    /** Low IF of 450 kHz. */
    KHZ_450(450, "sdrplay_api_IF_0_450"),
    /** Low IF of 1.62 MHz. */
    KHZ_1620(1620, "sdrplay_api_IF_1_620"),
    /** Low IF of 2.048 MHz. */
    KHZ_2048(2048, "sdrplay_api_IF_2_048");

    private final int code;
    private final String nativeName;

    IfMode(int code, String nativeName) {
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

    /** Returns the intermediate frequency in Hz: 0 for zero IF, -1000 for {@link #UNDEFINED}. */
    public long hertz() {
        return code * 1000L;
    }

    /**
     * Returns the value for an API code, or {@link #UNDEFINED} for an unknown code.
     *
     */
    public static IfMode fromCode(int code) {
        for (IfMode value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return UNDEFINED;
    }
}
