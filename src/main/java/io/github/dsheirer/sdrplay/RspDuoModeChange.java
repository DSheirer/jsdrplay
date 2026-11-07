package io.github.dsheirer.sdrplay;

/** Kind of RSPduo master and slave event in a {@link DeviceEvent.RspDuoModeChanged}: {@code sdrplay_api_RspDuoModeCbEventIdT}. */
public enum RspDuoModeChange {
    /** The master has started streaming. */
    MASTER_INITIALISED(0, "sdrplay_api_MasterInitialised"),
    /** A slave has selected the other tuner. */
    SLAVE_ATTACHED(1, "sdrplay_api_SlaveAttached"),
    /** The slave has released its tuner. */
    SLAVE_DETACHED(2, "sdrplay_api_SlaveDetached"),
    /** The slave has started streaming. */
    SLAVE_INITIALISED(3, "sdrplay_api_SlaveInitialised"),
    /** The slave has stopped streaming. */
    SLAVE_UNINITIALISED(4, "sdrplay_api_SlaveUninitialised"),
    /** The master application went away without releasing the device; the slave should release it too. */
    MASTER_DLL_DISAPPEARED(5, "sdrplay_api_MasterDllDisappeared"),
    /** The slave application went away without releasing its tuner. */
    SLAVE_DLL_DISAPPEARED(6, "sdrplay_api_SlaveDllDisappeared");

    private final int code;
    private final String nativeName;

    RspDuoModeChange(int code, String nativeName) {
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
    public static RspDuoModeChange fromCode(int code) {
        for (RspDuoModeChange value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown sdrplay_api_RspDuoModeCbEventIdT value: " + code);
    }
}
