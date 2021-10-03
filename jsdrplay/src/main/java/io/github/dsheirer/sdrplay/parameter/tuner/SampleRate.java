package io.github.dsheirer.sdrplay.parameter.tuner;

/**
 * Sample Rate, Bandwidth and Decimation enumeration
 *
 * Note: final effective sample rate must be greater than IF bandwidth setting to avoid aliasing.  The available IF
 * bandwidth values effectively drive the available sample rates
 */
public enum SampleRate
{
    RATE_0_250(2_000_000, Bandwidth.BW_0_200, 3, "0.250 MHz"),
    RATE_0_400(3_200_000, Bandwidth.BW_0_300, 3, "0.400 MHz"),
    RATE_0_800(3_200_000, Bandwidth.BW_0_600, 2,  "0.800 MHz"),
    RATE_1_800(3_600_000, Bandwidth.BW_1_536, 2, "1.800 MHz"),
    RATE_2_000(2_000_000, Bandwidth.BW_1_536, 1, "2.000 MHz"),
    RATE_6_000(6_000_000, Bandwidth.BW_5_000, 1, "6.000 MHz"),
    RATE_7_000(7_000_000, Bandwidth.BW_6_000, 1, "7.000 MHz"),
    RATE_8_000(8_000_000, Bandwidth.BW_7_000, 1, "8.000 MHz"),
    RATE_10_000(10_000_000, Bandwidth.BW_8_000, 1, "10.000 MHz"),
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
        return getDecimation() > 1;
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
