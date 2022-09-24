package com.github.dsheirer.sdrplay.parameter.tuner;

import java.util.EnumSet;

/**
 * Sample Rate, Bandwidth and Decimation enumeration
 *
 * Note: final effective sample rate must be greater than IF bandwidth setting to avoid aliasing.  The available IF
 * bandwidth values effectively drive the available sample rates
 */
public enum SampleRate
{
    RATE_0_250(2_000_000, Bandwidth.BW_0_200, 8, "0.250 MHz"),
    RATE_0_300(2_400_000, Bandwidth.BW_0_300, 8, "0.300 MHz"),
    RATE_0_600(2_400_000, Bandwidth.BW_0_600, 4,  "0.600 MHz"),
    RATE_1_600(3_200_000, Bandwidth.BW_1_536, 2, "1.600 MHz"),
    RATE_6_000(6_000_000, Bandwidth.BW_5_000, 1, "6.000 MHz"),
    RATE_7_000(7_000_000, Bandwidth.BW_6_000, 1, "7.000 MHz"),
    RATE_8_000(8_000_000, Bandwidth.BW_7_000, 1, "8.000 MHz"),
    RATE_9_000(9_000_000, Bandwidth.BW_8_000, 1, "9.000 MHz"),
    RATE_10_000(10_000_000, Bandwidth.BW_8_000, 1, "10.000 MHz"),

    /**
     * Note: in dual tuner mode, available sample rates are 6 or 8 MHz with an effective output rate of
     * 2 MHz.  Decimation is applied against the final 2 MHz rate, not the original 6/8 sampling rate.
     */
    DUO_RATE_0_500(8_000_000, Bandwidth.BW_1_536, 2, "0.500 MHz"),
    DUO_RATE_1_000(8_000_000, Bandwidth.BW_1_536, 1, "1.000 MHz"),
    DUO_RATE_2_000(8_000_000, Bandwidth.BW_1_536, 0, "2.000 MHz"),

    UNDEFINED(0, Bandwidth.UNDEFINED, 0, "UNDEFINED");

    private long mSampleRate;
    private Bandwidth mBandwidth;
    private int mDecimation;
    private String mDescription;

    SampleRate(long sampleRate, Bandwidth bandwidth, int decimation, String description)
    {
        mSampleRate = sampleRate;
        mBandwidth = bandwidth;
        mDecimation = decimation;
        mDescription = description;
    }

    /**
     * RSPduo dual-tuner mode sample rates
     */
    public static EnumSet<SampleRate> RSP_DUO_DUAL_TUNER_SAMPLE_RATES = EnumSet.range(DUO_RATE_0_500, DUO_RATE_2_000);

    public static EnumSet<SampleRate> VALID_VALUES = EnumSet.range(RATE_0_250, RATE_10_000);

    /**
     * Sample Rate
     * @return sample rate (Hz)
     */
    public long getSampleRate()
    {
        return mSampleRate;
    }

    /**
     * Decimation
     * @return decimation value
     */
    public int getDecimation()
    {
        return mDecimation;
    }

    /**
     * Indicates if this sample rate specifies decimation
     */
    public boolean hasDecimation()
    {
        return getDecimation() > 0;
    }

    /**
     * Effective Sample Rate (sample rate / decimation)
     * @return sample rate (Hz)
     */
    public long getEffectiveSampleRate()
    {
        return getSampleRate() / getDecimation();
    }

    /**
     * Bandwidth entry
     */
    public Bandwidth getBandwidth()
    {
        return mBandwidth;
    }

    @Override
    public String toString()
    {
        return mDescription;
    }
}
