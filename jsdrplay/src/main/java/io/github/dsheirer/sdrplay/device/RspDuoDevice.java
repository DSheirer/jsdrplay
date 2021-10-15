package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.SDRplayException;
import io.github.dsheirer.sdrplay.UpdateReason;
import io.github.dsheirer.sdrplay.parameter.composite.RspDuoCompositeParameters;
import io.github.dsheirer.sdrplay.SDRplay;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_DeviceT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RSPduo Device
 */
public class RspDuoDevice extends Device<RspDuoCompositeParameters, RspDuoTuner>
{
    private static final Logger mLog = LoggerFactory.getLogger(RspDuoDevice.class);

    private RspDuoTuner mTuner;

    /**
     * Constructs an SDRPlay RSPduo device from the foreign memory segment
     *
     * @param sdrPlay api instance that created this device
     * @param deviceStruct parser
     */
    RspDuoDevice(SDRplay sdrPlay, IDeviceStruct deviceStruct)
    {
        super(sdrPlay, deviceStruct);
    }

    /**
     * Tuner
     * @return tuner
     * @throws SDRplayException if there is an error
     */
    @Override
    public RspDuoTuner getTuner() throws SDRplayException
    {
        if(!isSelected())
        {
            throw new SDRplayException("Device must be selected before accessing the tuner");
        }

        if(mTuner == null)
        {
            if(getTunerSelect().equals(TunerSelect.TUNER_1))
            {
                mTuner = new RspDuoTuner1(RspDuoDevice.this, getAPI(),
                        getCompositeParameters().getDeviceParameters(), getCompositeParameters().getTunerAParameters(),
                        getCompositeParameters().getControlAParameters());
            }
            else
            {
                mTuner = new RspDuoTuner2(RspDuoDevice.this, getAPI(),
                        getCompositeParameters().getDeviceParameters(), getCompositeParameters().getTunerBParameters(),
                        getCompositeParameters().getControlBParameters());
            }
        }

        return mTuner;
    }

    /**
     * Selected tuner(s).
     */
    @Override
    public TunerSelect getTunerSelect()
    {
        return TunerSelect.fromValue(sdrplay_api_DeviceT.tuner$get(getDeviceMemorySegment()));
    }

    /**
     * Sets the selected tuner(s)
     */
    public void setTunerSelect(TunerSelect tunerSelect)
    {
        sdrplay_api_DeviceT.tuner$set(getDeviceMemorySegment(), tunerSelect.getValue());
    }

    /**
     * RSPduo mode
     */
    public RspDuoMode getRspDuoMode()
    {
        return getDeviceStruct().getRspDuoMode();
    }

    /**
     * Sets RSPduo mode.  Note this must be set before selecting the device for use.
     * @param mode to set
     * @throws SDRplayException if the device has already been selected
     */
    public void setRspDuoMode(RspDuoMode mode) throws SDRplayException
    {
        if(isSelected())
        {
            throw new SDRplayException("RSPduo device is already selected.  Mode can only be set/changed before the " +
                    "device is selected for use.");
        }

        getDeviceStruct().setRspDuoMode(mode);
    }

    /**
     * Sample rate when in master/slave mode
     */
    public double getRspDuoSampleFrequency()
    {
        return getDeviceStruct().getRspDuoSampleFrequency();
    }

    /**
     * Sets the sample rate when the device is configured for master mode.
     * @param frequency
     */
    public void setRspDuoSampleFrequency(double frequency) throws SDRplayException
    {
        if(!getRspDuoMode().equals(RspDuoMode.MASTER))
        {
            throw new SDRplayException("This method can only be used to set the overall sample rate when the RSPduo " +
                    "device is configured for master mode.");
        }

        getDeviceStruct().setRspDuoSampleFrequency(frequency);
    }

    /**
     * Sets the decimation factor for the final sample rate.
     * @param decimation as an integer multiple of two (e.g. 1, 2, 4, 8)
     * @throws SDRplayException if there is an error while setting decimation
     */
    @Override
    public void setDecimation(int decimation) throws SDRplayException
    {
        if((decimation < 1) || ((decimation != 1) && (decimation % 2 != 0)))
        {
            throw new IllegalArgumentException("Invalid decimation rate [" + decimation + "] - must be a positive integer multiple of 2.");
        }

        if(getTunerSelect() == TunerSelect.TUNER_1)
        {
            getCompositeParameters().getControlAParameters().getDecimation().setDecimationFactor(decimation);
            getCompositeParameters().getControlAParameters().getDecimation().setEnabled(decimation != 1);
            update(getTunerSelect(), UpdateReason.CONTROL_DECIMATION);
        }
        else if(getTunerSelect() == TunerSelect.TUNER_2)
        {
            getCompositeParameters().getControlBParameters().getDecimation().setDecimationFactor(decimation);
            getCompositeParameters().getControlBParameters().getDecimation().setEnabled(decimation != 1);
            update(getTunerSelect(), UpdateReason.CONTROL_DECIMATION);
        }
        else if(getTunerSelect() == TunerSelect.TUNER_BOTH)
        {
            //Dual-synchronized tuner mode ... let the parent Device class set the value
            super.setDecimation(decimation);
        }
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("SDRPplay Device").append("\n");
        sb.append("\tType: " + getDeviceType()).append("\n");
        sb.append("\tSerial Number: " + getSerialNumber()).append("\n");
        sb.append("\tTuner: " + getTunerSelect()).append("\n");
        sb.append("\tRSP Duo: " + getRspDuoMode()).append("\n");
        sb.append("\tRSP Duo Sample Rate: " + getRspDuoSampleFrequency()).append("\n");
        sb.append("\tSelected: " + isSelected());
        if(hasCompositeParameters())
        {
            sb.append("\t").append(getCompositeParameters());
        }

        return sb.toString();
    }
}
