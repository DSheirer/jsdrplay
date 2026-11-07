package io.github.dsheirer.sdrplay;

import static java.lang.foreign.ValueLayout.JAVA_BYTE;

import io.github.dsheirer.sdrplay.internal.Layouts;
import io.github.dsheirer.sdrplay.internal.UpdateReason;

/** An RSP1A or RSP1B. */
public final class Rsp1aDevice extends RspDevice {
    private static final long RF_NOTCH = Layouts.offset(Layouts.DEV_PARAMS, "rsp1aParams", "rfNotchEnable");
    private static final long RF_DAB_NOTCH = Layouts.offset(Layouts.DEV_PARAMS, "rsp1aParams", "rfDabNotchEnable");

    Rsp1aDevice(Selection selection) {
        super(selection);
    }

    /** Returns true when the bias tee is on. */
    public boolean isBiasT() {
        return channel().params().get(JAVA_BYTE, RxChannel.Offsets.RSP1A_BIAS_T) != 0;
    }

    /**
     * Turns the bias tee on or off. This puts DC power on the antenna
     * connector, which can damage equipment not built for it.
     */
    public void setBiasT(boolean enabled) throws SDRplayException {
        updateChannel(channel(), UpdateReason.RSP1A_BIAS_T_CONTROL,
            () -> channel().params().set(JAVA_BYTE, RxChannel.Offsets.RSP1A_BIAS_T, RxChannel.flag(enabled)));
    }

    /** Returns true when the broadcast FM notch filter is on. */
    public boolean isRfNotch() {
        return devParams().get(JAVA_BYTE, RF_NOTCH) != 0;
    }

    /** Turns the broadcast FM notch filter on or off. */
    public void setRfNotch(boolean enabled) throws SDRplayException {
        updateDevice(UpdateReason.RSP1A_RF_NOTCH_CONTROL,
            () -> devParams().set(JAVA_BYTE, RF_NOTCH, RxChannel.flag(enabled)));
    }

    /** Returns true when the DAB notch filter is on. */
    public boolean isRfDabNotch() {
        return devParams().get(JAVA_BYTE, RF_DAB_NOTCH) != 0;
    }

    /** Turns the DAB notch filter on or off. */
    public void setRfDabNotch(boolean enabled) throws SDRplayException {
        updateDevice(UpdateReason.RSP1A_RF_DAB_NOTCH_CONTROL,
            () -> devParams().set(JAVA_BYTE, RF_DAB_NOTCH, RxChannel.flag(enabled)));
    }
}
