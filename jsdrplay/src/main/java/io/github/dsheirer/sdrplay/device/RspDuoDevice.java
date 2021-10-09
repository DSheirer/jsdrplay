package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.SDRplayException;
import io.github.dsheirer.sdrplay.Version;
import io.github.dsheirer.sdrplay.parameter.composite.RspDuoCompositeParameters;
import io.github.dsheirer.sdrplay.SDRplay;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_DeviceT;
import jdk.incubator.foreign.MemorySegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RSPduo Device
 */
public class RspDuoDevice extends Device<RspDuoCompositeParameters, RspDuoTuner1>
{
    private static final Logger mLog = LoggerFactory.getLogger(RspDuoDevice.class);

    private RspDuoTuner1 mTuner1;
    private RspDuoTuner2 mTuner2;

    /**
     * Constructs an SDRPlay RSPduo device from the foreign memory segment
     *
     * @param sdrPlay api instance that created this device
     * @param version of the api
     * @param deviceStruct parser
     */
    RspDuoDevice(SDRplay sdrPlay, Version version, IDeviceStruct deviceStruct)
    {
        super(sdrPlay, version, deviceStruct);

        //Auto-set the tuner to dual tuner mode.
        if(getRspDuoMode() == RspDuoMode.UNKNOWN)
        {
            setRspDuoMode(RspDuoMode.DUAL_TUNER);
            setTunerSelect(TunerSelect.TUNER_1);
        }
    }

    /**
     * Tuner 1
     * @return tuner 1
     * @throws SDRplayException if there is an error
     */
    @Override
    public RspDuoTuner1 getTuner1() throws SDRplayException
    {
        if(!isSelected())
        {
            throw new SDRplayException("Device must be selected before accessing the tuner");
        }

        if(mTuner1 == null)
        {
            mTuner1 = new RspDuoTuner1(RspDuoDevice.this, getAPI(),
                    getCompositeParameters().getDeviceParameters(), getCompositeParameters().getTunerParameters1(),
                    getCompositeParameters().getControlParameters1());
        }

        return mTuner1;
    }

    /**
     * Tuner 2
     * @return tuner 2
     * @throws SDRplayException if there is an error or if RSPduo mode is not set to RspDuoMode.DUAL_TUNER
     */
    public RspDuoTuner2 getTuner2() throws SDRplayException
    {
        if(!isSelected())
        {
            throw new SDRplayException("Device must be selected before accessing the tuner");
        }

        if(getRspDuoMode() != RspDuoMode.DUAL_TUNER)
        {
            throw new SDRplayException("Set RSPduo mode to RspDuoMode.DUAL_TUNER before accessing this second tuner");
        }

        if(mTuner2 == null)
        {
            mTuner2 = new RspDuoTuner2(RspDuoDevice.this, getAPI(),
                    getCompositeParameters().getDeviceParameters(), getCompositeParameters().getTunerParameters2(),
                    getCompositeParameters().getControlParameters2());
        }

        return mTuner2;
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
     * Sets RSPduo mode.  Note this must be set before accessing tuner 1 or 2.  Attempts to change the mode after any
     * tuner has been accessed will be ignored.
     */
    public void setRspDuoMode(RspDuoMode mode)
    {
        if(mTuner1 != null || mTuner2 != null)
        {
            mLog.warn("Attempt to change RSPduo mode [" + mode + "] ignored.  Tuner 1 or 2 has already been accessed");
        }
        else
        {
            getDeviceStruct().setRspDuoMode(mode);
        }
    }

    /**
     * Sample rate when in master/slave mode
     */
    public double getRspDuoSampleFrequency()
    {
        return sdrplay_api_DeviceT.rspDuoSampleFreq$get(getDeviceMemorySegment());
    }

    /**
     * Sets the sample rate for master/slave mode
     */
    public void setRspDuoSampleFrequency(double frequency)
    {
        getDeviceStruct().setRspDuoSampleFrequency(frequency);
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
