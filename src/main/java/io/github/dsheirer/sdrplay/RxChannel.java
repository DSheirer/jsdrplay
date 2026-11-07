package io.github.dsheirer.sdrplay;

import static java.lang.foreign.ValueLayout.JAVA_BYTE;
import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;
import static java.lang.foreign.ValueLayout.JAVA_FLOAT;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.lang.foreign.ValueLayout.JAVA_SHORT;

import io.github.dsheirer.sdrplay.internal.Layouts;
import io.github.dsheirer.sdrplay.internal.UpdateReason;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemorySegment;
import java.util.Objects;

/**
 * The settings of one tuner: {@code sdrplay_api_RxChannelParamsT}. Every
 * model has one, from {@link RspDevice#channel()}; an RSPduo in dual tuner
 * mode has one per tuner.
 *
 * <p>As with {@link RspDevice}, a setter changes the stored setting and, while
 * the device is streaming, sends it to the device.
 */
public final class RxChannel {
    private static final int[] DECIMATION_FACTORS = {1, 2, 4, 8, 16, 32};

    private final RspDevice device;
    private final Tuner tuner;
    private final int index;

    RxChannel(RspDevice device, Tuner tuner, int index) {
        this.device = device;
        this.tuner = tuner;
        this.index = index;
    }

    /** Returns the tuner these settings belong to. */
    public Tuner tuner() {
        return tuner;
    }

    int index() {
        return index;
    }

    MemorySegment params() {
        return device.rxChannelParams(index);
    }

    private void update(UpdateReason reason, Runnable write) throws SDRplayException {
        device.updateChannel(this, reason, write);
    }

    // ---- Frequency ----

    /** Returns the tuned frequency in Hz. */
    public double frequency() {
        return params().get(JAVA_DOUBLE, Offsets.RF_HZ);
    }

    /** Tunes to a frequency in Hz. Each model's range is in the SDRplay API specification. */
    public void setFrequency(double hertz) throws SDRplayException {
        RspDevice.requirePositive(hertz, "Frequency");
        update(UpdateReason.TUNER_FRF, () -> params().set(JAVA_DOUBLE, Offsets.RF_HZ, hertz));
    }

    // ---- Gain ----

    /** Returns the baseband gain reduction in dB. */
    public int gainReduction() {
        return params().get(JAVA_INT, Offsets.GR_DB);
    }

    /**
     * Sets the baseband gain reduction in dB, from the minimum of
     * {@link #setMinGainReduction} (20 dB by default) to
     * {@value io.github.dsheirer.sdrplay.internal.Layouts#MAX_BB_GR}. It
     * applies when the AGC is disabled.
     */
    public void setGainReduction(int decibels) throws SDRplayException {
        if (decibels < 0 || decibels > Layouts.MAX_BB_GR) {
            throw new IllegalArgumentException("Gain reduction must be 0 to " + Layouts.MAX_BB_GR + ": " + decibels);
        }
        update(UpdateReason.TUNER_GR, () -> params().set(JAVA_INT, Offsets.GR_DB, decibels));
    }

    /** Returns the LNA state: 0 is the most gain. */
    public int lnaState() {
        return Byte.toUnsignedInt(params().get(JAVA_BYTE, Offsets.LNA_STATE));
    }

    /**
     * Sets the LNA state, where 0 is the most gain. The number of states
     * depends on the model and frequency band, from 5 to 28; see the SDRplay
     * API specification.
     */
    public void setLnaState(int state) throws SDRplayException {
        byte value = u8(state, "LNA state");
        update(UpdateReason.TUNER_GR, () -> params().set(JAVA_BYTE, Offsets.LNA_STATE, value));
    }

    /** Sets the baseband gain reduction and LNA state in one update. */
    public void setGain(int gainReductionDb, int lnaState) throws SDRplayException {
        if (gainReductionDb < 0 || gainReductionDb > Layouts.MAX_BB_GR) {
            throw new IllegalArgumentException(
                "Gain reduction must be 0 to " + Layouts.MAX_BB_GR + ": " + gainReductionDb);
        }
        byte state = u8(lnaState, "LNA state");
        update(UpdateReason.TUNER_GR, () -> {
            MemorySegment params = params();
            params.set(JAVA_INT, Offsets.GR_DB, gainReductionDb);
            params.set(JAVA_BYTE, Offsets.LNA_STATE, state);
        });
    }

    /** Returns the lower limit of the gain reduction. */
    public MinGainReduction minGainReduction() {
        return MinGainReduction.fromCode(params().get(JAVA_INT, Offsets.MIN_GR));
    }

    /** Sets the lower limit of the gain reduction. */
    public void setMinGainReduction(MinGainReduction limit) throws SDRplayException {
        Objects.requireNonNull(limit, "limit");
        update(UpdateReason.TUNER_GR_LIMITS, () -> params().set(JAVA_INT, Offsets.MIN_GR, limit.code()));
    }

    /** Returns the gain the API calculated for the current settings, which it updates while streaming. */
    public GainValues gainValues() {
        MemorySegment params = params();
        return new GainValues(params.get(JAVA_FLOAT, Offsets.GAIN_CURR), params.get(JAVA_FLOAT, Offsets.GAIN_MAX),
            params.get(JAVA_FLOAT, Offsets.GAIN_MIN));
    }

    // ---- Tuner ----

    /** Returns the IF filter bandwidth. */
    public Bandwidth bandwidth() {
        return Bandwidth.fromCode(params().get(JAVA_INT, Offsets.BW_TYPE));
    }

    /** Sets the IF filter bandwidth. */
    public void setBandwidth(Bandwidth bandwidth) throws SDRplayException {
        Objects.requireNonNull(bandwidth, "bandwidth");
        update(UpdateReason.TUNER_BW_TYPE, () -> params().set(JAVA_INT, Offsets.BW_TYPE, bandwidth.code()));
    }

    /** Returns the intermediate frequency. */
    public IfMode ifMode() {
        return IfMode.fromCode(params().get(JAVA_INT, Offsets.IF_TYPE));
    }

    /** Sets the intermediate frequency. Low IF modes need particular sample rates; see the API specification. */
    public void setIfMode(IfMode mode) throws SDRplayException {
        Objects.requireNonNull(mode, "mode");
        update(UpdateReason.TUNER_IF_TYPE, () -> params().set(JAVA_INT, Offsets.IF_TYPE, mode.code()));
    }

    /** Returns the local oscillator mode. */
    public LoMode loMode() {
        return LoMode.fromCode(params().get(JAVA_INT, Offsets.LO_MODE));
    }

    /** Sets the local oscillator mode. */
    public void setLoMode(LoMode mode) throws SDRplayException {
        Objects.requireNonNull(mode, "mode");
        update(UpdateReason.TUNER_LO_MODE, () -> params().set(JAVA_INT, Offsets.LO_MODE, mode.code()));
    }

    /**
     * Configures the tuner's DC offset calibration.
     *
     * @param dcCal calibration mode; 3, periodic, is the default
     * @param speedUp true to speed up the calibration
     * @param trackTime tracking time, in units of 9 microseconds; default 1
     * @param refreshRateTime refresh period, in units of 9 microseconds; default 2048
     */
    public void setDcOffsetTuner(int dcCal, boolean speedUp, int trackTime, int refreshRateTime)
            throws SDRplayException {
        byte cal = u8(dcCal, "DC calibration mode");
        update(UpdateReason.TUNER_DC_OFFSET, () -> {
            MemorySegment params = params();
            params.set(JAVA_BYTE, Offsets.DC_CAL, cal);
            params.set(JAVA_BYTE, Offsets.SPEED_UP, flag(speedUp));
            params.set(JAVA_INT, Offsets.TRACK_TIME, trackTime);
            params.set(JAVA_INT, Offsets.REFRESH_RATE_TIME, refreshRateTime);
        });
    }

    // ---- Control ----

    /** Returns true when DC offset correction is enabled, the default. */
    public boolean isDcOffsetCorrection() {
        return params().get(JAVA_BYTE, Offsets.DC_ENABLE) != 0;
    }

    /** Enables or disables DC offset correction. */
    public void setDcOffsetCorrection(boolean enabled) throws SDRplayException {
        update(UpdateReason.CTRL_DC_OFFSET_IQ_IMBALANCE,
            () -> params().set(JAVA_BYTE, Offsets.DC_ENABLE, flag(enabled)));
    }

    /** Returns true when I/Q imbalance correction is enabled, the default. */
    public boolean isIqCorrection() {
        return params().get(JAVA_BYTE, Offsets.IQ_ENABLE) != 0;
    }

    /** Enables or disables I/Q imbalance correction. */
    public void setIqCorrection(boolean enabled) throws SDRplayException {
        update(UpdateReason.CTRL_DC_OFFSET_IQ_IMBALANCE,
            () -> params().set(JAVA_BYTE, Offsets.IQ_ENABLE, flag(enabled)));
    }

    /** Returns the decimation factor, 1 when decimation is off. */
    public int decimationFactor() {
        MemorySegment params = params();
        return params.get(JAVA_BYTE, Offsets.DECIMATION_ENABLE) == 0
            ? 1 : Byte.toUnsignedInt(params.get(JAVA_BYTE, Offsets.DECIMATION_FACTOR));
    }

    /**
     * Sets the decimation applied after the ADC.
     *
     * @param factor 1 (off), 2, 4, 8, 16 or 32
     * @param wideBandSignal true to use the half-band filters, suited to wide
     *     signals, rather than the narrower default filters
     */
    public void setDecimation(int factor, boolean wideBandSignal) throws SDRplayException {
        boolean valid = false;
        for (int candidate : DECIMATION_FACTORS) {
            valid |= candidate == factor;
        }
        if (!valid) {
            throw new IllegalArgumentException("Decimation factor must be 1, 2, 4, 8, 16 or 32: " + factor);
        }
        update(UpdateReason.CTRL_DECIMATION, () -> {
            MemorySegment params = params();
            params.set(JAVA_BYTE, Offsets.DECIMATION_ENABLE, flag(factor > 1));
            params.set(JAVA_BYTE, Offsets.DECIMATION_FACTOR, (byte) factor);
            params.set(JAVA_BYTE, Offsets.WIDE_BAND_SIGNAL, flag(wideBandSignal));
        });
    }

    /** Returns the AGC mode. */
    public AgcMode agcMode() {
        return AgcMode.fromCode(params().get(JAVA_INT, Offsets.AGC_ENABLE));
    }

    /** Returns the AGC set point in dBFS. */
    public int agcSetPoint() {
        return params().get(JAVA_INT, Offsets.AGC_SET_POINT);
    }

    /** Sets the AGC mode, keeping the set point. */
    public void setAgc(AgcMode mode) throws SDRplayException {
        Objects.requireNonNull(mode, "mode");
        update(UpdateReason.CTRL_AGC, () -> params().set(JAVA_INT, Offsets.AGC_ENABLE, mode.code()));
    }

    /** Sets the AGC mode and set point, in dBFS; the API default is -60. */
    public void setAgc(AgcMode mode, int setPointDbfs) throws SDRplayException {
        Objects.requireNonNull(mode, "mode");
        update(UpdateReason.CTRL_AGC, () -> {
            MemorySegment params = params();
            params.set(JAVA_INT, Offsets.AGC_ENABLE, mode.code());
            params.set(JAVA_INT, Offsets.AGC_SET_POINT, setPointDbfs);
        });
    }

    /**
     * Sets every AGC parameter. The attack, decay and threshold values are used
     * with {@link AgcMode#CONTROL_ENABLED}.
     */
    public void setAgc(AgcMode mode, int setPointDbfs, int attackMs, int decayMs, int decayDelayMs,
            int decayThresholdDb) throws SDRplayException {
        Objects.requireNonNull(mode, "mode");
        short attack = u16(attackMs, "Attack");
        short decay = u16(decayMs, "Decay");
        short delay = u16(decayDelayMs, "Decay delay");
        short threshold = u16(decayThresholdDb, "Decay threshold");
        update(UpdateReason.CTRL_AGC, () -> {
            MemorySegment params = params();
            params.set(JAVA_INT, Offsets.AGC_ENABLE, mode.code());
            params.set(JAVA_INT, Offsets.AGC_SET_POINT, setPointDbfs);
            params.set(JAVA_SHORT, Offsets.AGC_ATTACK, attack);
            params.set(JAVA_SHORT, Offsets.AGC_DECAY, decay);
            params.set(JAVA_SHORT, Offsets.AGC_DECAY_DELAY, delay);
            params.set(JAVA_SHORT, Offsets.AGC_DECAY_THRESHOLD, threshold);
        });
    }

    /** Returns the ADS-B mode. */
    public AdsbMode adsbMode() {
        return AdsbMode.fromCode(params().get(JAVA_INT, Offsets.ADSB_MODE));
    }

    /** Sets the ADS-B mode. */
    public void setAdsbMode(AdsbMode mode) throws SDRplayException {
        Objects.requireNonNull(mode, "mode");
        update(UpdateReason.CTRL_ADSB_MODE, () -> params().set(JAVA_INT, Offsets.ADSB_MODE, mode.code()));
    }

    @Override
    public String toString() {
        return "RxChannel " + tuner + " of " + device;
    }

    static byte flag(boolean value) {
        return (byte) (value ? 1 : 0);
    }

    static byte u8(int value, String what) {
        if (value < 0 || value > 0xFF) {
            throw new IllegalArgumentException(what + " out of 8-bit range: " + value);
        }
        return (byte) value;
    }

    private static short u16(int value, String what) {
        if (value < 0 || value > 0xFFFF) {
            throw new IllegalArgumentException(what + " out of 16-bit range: " + value);
        }
        return (short) value;
    }

    /** Offsets in {@code sdrplay_api_RxChannelParamsT}. */
    static final class Offsets {
        private static final GroupLayout RX = Layouts.RX_CHANNEL_PARAMS;

        static final long BW_TYPE = Layouts.offset(RX, "tunerParams", "bwType");
        static final long IF_TYPE = Layouts.offset(RX, "tunerParams", "ifType");
        static final long LO_MODE = Layouts.offset(RX, "tunerParams", "loMode");
        static final long GR_DB = Layouts.offset(RX, "tunerParams", "gain", "gRdB");
        static final long LNA_STATE = Layouts.offset(RX, "tunerParams", "gain", "LNAstate");
        static final long MIN_GR = Layouts.offset(RX, "tunerParams", "gain", "minGr");
        static final long GAIN_CURR = Layouts.offset(RX, "tunerParams", "gain", "gainVals", "curr");
        static final long GAIN_MAX = Layouts.offset(RX, "tunerParams", "gain", "gainVals", "max");
        static final long GAIN_MIN = Layouts.offset(RX, "tunerParams", "gain", "gainVals", "min");
        static final long RF_HZ = Layouts.offset(RX, "tunerParams", "rfFreq", "rfHz");
        static final long DC_CAL = Layouts.offset(RX, "tunerParams", "dcOffsetTuner", "dcCal");
        static final long SPEED_UP = Layouts.offset(RX, "tunerParams", "dcOffsetTuner", "speedUp");
        static final long TRACK_TIME = Layouts.offset(RX, "tunerParams", "dcOffsetTuner", "trackTime");
        static final long REFRESH_RATE_TIME = Layouts.offset(RX, "tunerParams", "dcOffsetTuner", "refreshRateTime");

        static final long DC_ENABLE = Layouts.offset(RX, "ctrlParams", "dcOffset", "DCenable");
        static final long IQ_ENABLE = Layouts.offset(RX, "ctrlParams", "dcOffset", "IQenable");
        static final long DECIMATION_ENABLE = Layouts.offset(RX, "ctrlParams", "decimation", "enable");
        static final long DECIMATION_FACTOR = Layouts.offset(RX, "ctrlParams", "decimation", "decimationFactor");
        static final long WIDE_BAND_SIGNAL = Layouts.offset(RX, "ctrlParams", "decimation", "wideBandSignal");
        static final long AGC_ENABLE = Layouts.offset(RX, "ctrlParams", "agc", "enable");
        static final long AGC_SET_POINT = Layouts.offset(RX, "ctrlParams", "agc", "setPoint_dBfs");
        static final long AGC_ATTACK = Layouts.offset(RX, "ctrlParams", "agc", "attack_ms");
        static final long AGC_DECAY = Layouts.offset(RX, "ctrlParams", "agc", "decay_ms");
        static final long AGC_DECAY_DELAY = Layouts.offset(RX, "ctrlParams", "agc", "decay_delay_ms");
        static final long AGC_DECAY_THRESHOLD = Layouts.offset(RX, "ctrlParams", "agc", "decay_threshold_dB");
        static final long ADSB_MODE = Layouts.offset(RX, "ctrlParams", "adsbMode");

        static final long RSP1A_BIAS_T = Layouts.offset(RX, "rsp1aTunerParams", "biasTEnable");
        static final long RSP2_BIAS_T = Layouts.offset(RX, "rsp2TunerParams", "biasTEnable");
        static final long RSP2_AM_PORT = Layouts.offset(RX, "rsp2TunerParams", "amPortSel");
        static final long RSP2_ANTENNA = Layouts.offset(RX, "rsp2TunerParams", "antennaSel");
        static final long RSP2_RF_NOTCH = Layouts.offset(RX, "rsp2TunerParams", "rfNotchEnable");
        static final long RSPDUO_BIAS_T = Layouts.offset(RX, "rspDuoTunerParams", "biasTEnable");
        static final long RSPDUO_AM_PORT = Layouts.offset(RX, "rspDuoTunerParams", "tuner1AmPortSel");
        static final long RSPDUO_AM_NOTCH = Layouts.offset(RX, "rspDuoTunerParams", "tuner1AmNotchEnable");
        static final long RSPDUO_RF_NOTCH = Layouts.offset(RX, "rspDuoTunerParams", "rfNotchEnable");
        static final long RSPDUO_RF_DAB_NOTCH = Layouts.offset(RX, "rspDuoTunerParams", "rfDabNotchEnable");
        static final long RSPDX_HDR_BW = Layouts.offset(RX, "rspDxTunerParams", "hdrBw");

        private Offsets() {
        }
    }
}
