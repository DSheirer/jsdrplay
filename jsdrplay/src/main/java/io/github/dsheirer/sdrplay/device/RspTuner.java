package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.SDRplay;
import io.github.dsheirer.sdrplay.SDRplayException;
import io.github.dsheirer.sdrplay.UpdateReason;
import io.github.dsheirer.sdrplay.parameter.control.ControlParameters;
import io.github.dsheirer.sdrplay.parameter.device.DeviceParameters;
import io.github.dsheirer.sdrplay.parameter.tuner.GainReduction;
import io.github.dsheirer.sdrplay.parameter.tuner.SampleRate;
import io.github.dsheirer.sdrplay.parameter.tuner.TunerParameters;

/**
 * Abstract RSP device tuner
 * @param <T> RSP device implementation
 */
public abstract class RspTuner<D extends DeviceParameters, T extends TunerParameters>
{
    private final SDRplay mSDRplay;
    private final Device mDevice;
    private final TunerSelect mTunerSelect;
    private final D mDeviceParameters;
    private final T mTunerParameters;
    private final ControlParameters mControlParameters;

    private GainReduction mGainReduction;

    RspTuner(Device device, SDRplay sdrplay, TunerSelect tunerSelect, D deviceParameters, T tunerParameters,
             ControlParameters controlParameters)
    {
        mDevice = device;
        mSDRplay = sdrplay;
        mTunerSelect = tunerSelect;
        mDeviceParameters = deviceParameters;
        mTunerParameters = tunerParameters;
        mControlParameters = controlParameters;
    }

    /**
     * SDRplay API
     */
    private SDRplay getSDRplay()
    {
        return mSDRplay;
    }

    /**
     * Parent device for this tuner
     */
    private Device getDevice()
    {
        return mDevice;
    }

    /**
     * Selected tuner
     */
    public TunerSelect getTunerSelect()
    {
        return mTunerSelect;
    }

    /**
     * Device parameters
     */
    protected D getDeviceParameters()
    {
        return mDeviceParameters;
    }

    /**
     * Tuner parameters for the selected tuner
     */
    protected T getTunerParameters()
    {
        return mTunerParameters;
    }

    /**
     * Control parameters for the selected tuner
     */
    protected ControlParameters getControlParameters()
    {
        return mControlParameters;
    }

    /**
     * Convenience method for notifying the API that parameters for the device have been updated
     * @param updateReasons indicating what has been updated
     * @throws SDRplayException if there is an error
     */
    protected void update(UpdateReason ... updateReasons) throws SDRplayException
    {
        mSDRplay.update(getDevice(), getDevice().getDeviceHandle(), getTunerSelect(), updateReasons);
    }

    /**
     * Current sample rate
     */
    public double getSampleRate()
    {
        return getDeviceParameters().getSamplingFrequency().getSampleRate();
    }

    /**
     * Sets the specified sample rate
     * @param sampleRate to apply
     * @throws SDRplayException if the device is not selected or available
     */
    public void setSampleRate(SampleRate sampleRate) throws SDRplayException
    {
        getDeviceParameters().getSamplingFrequency().setSampleRate(sampleRate);

        getTunerParameters().setBandwidth(sampleRate.getBandwidth());

        if(sampleRate.hasDecimation())
        {
            getControlParameters().getDecimation().setEnabled(true);
            getControlParameters().getDecimation().setWideBandSignal(true);
            getControlParameters().getDecimation().setDecimationFactor(sampleRate.getDecimation());
        }
        else
        {
            getControlParameters().getDecimation().setEnabled(false);
        }

        update(UpdateReason.DEVICE_SAMPLE_RATE, UpdateReason.TUNER_BANDWIDTH_TYPE, UpdateReason.CONTROL_DECIMATION);
    }

    /**
     * Center frequency for the tuner
     */
    public long getFrequency()
    {
        return (long)getTunerParameters().getRfFrequency().getFrequency();
    }

    /**
     * Sets the center frequency for the tuner
     * @param frequency in Hertz
     * @throws SDRplayException if there is an error
     */
    public void setFrequency(long frequency) throws SDRplayException
    {
        getTunerParameters().getRfFrequency().setFrequency(frequency);
        update(UpdateReason.TUNER_FREQUENCY_RF);
        updateGainReduction(frequency);
    }

    /**
     * Gain reduction in use for this device with the current RF frequency
     */
    public GainReduction getGainReduction()
    {
        if(mGainReduction == null)
        {
            updateGainReduction(getFrequency());
        }

        return mGainReduction;
    }

    /**
     * Updates the gain reduction, if necessary, for the specified RF frequency
     * @param frequency value
     */
    private void updateGainReduction(long frequency)
    {
        if(mGainReduction == null || !mGainReduction.isValidFor(frequency))
        {
            mGainReduction = GainReduction.lookup(getDevice().getDeviceType(), frequency);
        }
    }

    /**
     * Selects a gain value index from the current gain reduction values
     * @param index of the gain reduction values to use
     */
    public void setGain(int index) throws SDRplayException
    {
        getTunerParameters().getGain().setGain(getGainReduction(), index);
        update(UpdateReason.TUNER_GAIN_REDUCTION);
    }
}
