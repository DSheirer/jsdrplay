package io.github.dsheirer.sdrplay.internal;

/** Event identifiers of the event callback: {@code sdrplay_api_EventT}. */
public enum EventType {
    /** The gain changed. */
    GAIN_CHANGE(0, "sdrplay_api_GainChange"),
    /** An overload was detected or corrected. */
    POWER_OVERLOAD_CHANGE(1, "sdrplay_api_PowerOverloadChange"),
    /** The device was unplugged. */
    DEVICE_REMOVED(2, "sdrplay_api_DeviceRemoved"),
    /** An RSPduo master or slave changed state. */
    RSPDUO_MODE_CHANGE(3, "sdrplay_api_RspDuoModeChange"),
    /** The device stopped working. */
    DEVICE_FAILURE(4, "sdrplay_api_DeviceFailure");

    private final int code;
    private final String nativeName;

    EventType(int code, String nativeName) {
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
    public static EventType fromCode(int code) {
        for (EventType value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown sdrplay_api_EventT value: " + code);
    }
}
