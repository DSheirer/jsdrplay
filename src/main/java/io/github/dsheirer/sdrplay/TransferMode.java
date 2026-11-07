package io.github.dsheirer.sdrplay;

/** USB transfer mode of the device: {@code sdrplay_api_TransferModeT}. */
public enum TransferMode {
    /** Isochronous transfers, the default: guaranteed bandwidth. */
    ISOCHRONOUS(0, "sdrplay_api_ISOCH"),
    /** Bulk transfers, for USB hosts that handle isochronous transfers poorly. */
    BULK(1, "sdrplay_api_BULK");

    private final int code;
    private final String nativeName;

    TransferMode(int code, String nativeName) {
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
    public static TransferMode fromCode(int code) {
        for (TransferMode value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown sdrplay_api_TransferModeT value: " + code);
    }
}
