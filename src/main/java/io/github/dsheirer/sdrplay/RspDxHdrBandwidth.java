package io.github.dsheirer.sdrplay;

/** RSPdx and RSPdx-R2 bandwidth in HDR mode: {@code sdrplay_api_RspDx_HdrModeBwT}. */
public enum RspDxHdrBandwidth {
    /** 200 kHz. */
    KHZ_200(0, "sdrplay_api_RspDx_HDRMODE_BW_0_200"),
    /** 500 kHz. */
    KHZ_500(1, "sdrplay_api_RspDx_HDRMODE_BW_0_500"),
    /** 1.2 MHz. */
    KHZ_1200(2, "sdrplay_api_RspDx_HDRMODE_BW_1_200"),
    /** 1.7 MHz, the default. */
    KHZ_1700(3, "sdrplay_api_RspDx_HDRMODE_BW_1_700");

    private final int code;
    private final String nativeName;

    RspDxHdrBandwidth(int code, String nativeName) {
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
    public static RspDxHdrBandwidth fromCode(int code) {
        for (RspDxHdrBandwidth value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown sdrplay_api_RspDx_HdrModeBwT value: " + code);
    }
}
