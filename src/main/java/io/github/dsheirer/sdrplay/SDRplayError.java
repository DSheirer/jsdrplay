package io.github.dsheirer.sdrplay;

/**
 * Error codes of {@code sdrplay_api_ErrT}, plus two that jsdrplay reports
 * itself and that have no C name.
 */
public enum SDRplayError {
    /** The call succeeded. */
    SUCCESS(0, "sdrplay_api_Success"),
    /** General failure. */
    FAIL(1, "sdrplay_api_Fail"),
    /** A parameter was not valid. */
    INVALID_PARAM(2, "sdrplay_api_InvalidParam"),
    /** A parameter was out of range. */
    OUT_OF_RANGE(3, "sdrplay_api_OutOfRange"),
    /** A gain update failed, often because the previous one had not completed. */
    GAIN_UPDATE_ERROR(4, "sdrplay_api_GainUpdateError"),
    /** A frequency update failed, often because the previous one had not completed. */
    RF_UPDATE_ERROR(5, "sdrplay_api_RfUpdateError"),
    /** A sample rate update failed, often because the previous one had not completed. */
    FS_UPDATE_ERROR(6, "sdrplay_api_FsUpdateError"),
    /** The hardware reported an error. */
    HW_ERROR(7, "sdrplay_api_HwError"),
    /** The sample rate and decimation would alias. */
    ALIASING_ERROR(8, "sdrplay_api_AliasingError"),
    /** The device is already streaming. */
    ALREADY_INITIALISED(9, "sdrplay_api_AlreadyInitialised"),
    /** The device is not streaming. */
    NOT_INITIALISED(10, "sdrplay_api_NotInitialised"),
    /** The feature is not enabled. */
    NOT_ENABLED(11, "sdrplay_api_NotEnabled"),
    /** The hardware version does not support the request. */
    HW_VER_ERROR(12, "sdrplay_api_HwVerError"),
    /** The API ran out of memory. */
    OUT_OF_MEM_ERROR(13, "sdrplay_api_OutOfMemError"),
    /** The SDRplay API service is not running or not answering. */
    SERVICE_NOT_RESPONDING(14, "sdrplay_api_ServiceNotResponding"),
    /** An RSPduo slave is waiting for its master to start. */
    START_PENDING(15, "sdrplay_api_StartPending"),
    /** An RSPduo master is waiting for its slave to stop. */
    STOP_PENDING(16, "sdrplay_api_StopPending"),
    /** The request is not valid in the current RSPduo mode. */
    INVALID_MODE(17, "sdrplay_api_InvalidMode"),
    /** Device verification failed. */
    FAILED_VERIFICATION_1(18, "sdrplay_api_FailedVerification1"),
    /** Device verification failed. */
    FAILED_VERIFICATION_2(19, "sdrplay_api_FailedVerification2"),
    /** Device verification failed. */
    FAILED_VERIFICATION_3(20, "sdrplay_api_FailedVerification3"),
    /** Device verification failed. */
    FAILED_VERIFICATION_4(21, "sdrplay_api_FailedVerification4"),
    /** Device verification failed. */
    FAILED_VERIFICATION_5(22, "sdrplay_api_FailedVerification5"),
    /** Device verification failed. */
    FAILED_VERIFICATION_6(23, "sdrplay_api_FailedVerification6"),
    /** The installed service does not match the API library. */
    INVALID_SERVICE_VERSION(24, "sdrplay_api_InvalidServiceVersion"),

    /** jsdrplay: the SDRplay API library is not installed or could not be loaded. */
    API_NOT_FOUND(-1, null),
    /** jsdrplay: the installed SDRplay API is a version jsdrplay cannot use safely. */
    UNSUPPORTED_API_VERSION(-2, null),
    /** A code this version of jsdrplay does not know. */
    OTHER(Integer.MIN_VALUE, null);

    private final int code;
    private final String nativeName;

    SDRplayError(int code, String nativeName) {
        this.code = code;
        this.nativeName = nativeName;
    }

    /** Returns the {@code sdrplay_api_ErrT} value, or a negative value for the jsdrplay errors. */
    public int code() {
        return code;
    }

    /** Returns the C name, such as {@code sdrplay_api_Fail}, or null for the jsdrplay errors. */
    public String nativeName() {
        return nativeName;
    }

    /** Returns the error for a native code, or {@link #OTHER} if it is unknown. */
    public static SDRplayError fromCode(int code) {
        for (SDRplayError error : values()) {
            if (error.code == code) {
                return error;
            }
        }
        return OTHER;
    }
}
