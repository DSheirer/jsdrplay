package io.github.dsheirer.sdrplay;

/** IF filter bandwidth of a tuner: {@code sdrplay_api_Bw_MHzT}. */
public enum Bandwidth {
    /** Not set. */
    UNDEFINED(0, "sdrplay_api_BW_Undefined"),
    /** 200 kHz, the default. */
    KHZ_200(200, "sdrplay_api_BW_0_200"),
    /** 300 kHz. */
    KHZ_300(300, "sdrplay_api_BW_0_300"),
    /** 600 kHz. */
    KHZ_600(600, "sdrplay_api_BW_0_600"),
    /** 1.536 MHz. */
    KHZ_1536(1536, "sdrplay_api_BW_1_536"),
    /** 5 MHz. */
    MHZ_5(5000, "sdrplay_api_BW_5_000"),
    /** 6 MHz. */
    MHZ_6(6000, "sdrplay_api_BW_6_000"),
    /** 7 MHz. */
    MHZ_7(7000, "sdrplay_api_BW_7_000"),
    /** 8 MHz. */
    MHZ_8(8000, "sdrplay_api_BW_8_000");

    private final int code;
    private final String nativeName;

    Bandwidth(int code, String nativeName) {
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

    /** Returns the bandwidth in Hz, or 0 for {@link #UNDEFINED}. */
    public long hertz() {
        return code * 1000L;
    }

    /**
     * Returns the value for an API code, or {@link #UNDEFINED} for an unknown code.
     *
     */
    public static Bandwidth fromCode(int code) {
        for (Bandwidth value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return UNDEFINED;
    }
}
