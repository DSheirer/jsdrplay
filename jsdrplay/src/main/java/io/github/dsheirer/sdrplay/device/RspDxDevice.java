package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.SDRplayException;
import io.github.dsheirer.sdrplay.parameter.composite.RspDxCompositeParameters;
import io.github.dsheirer.sdrplay.SDRplay;
import jdk.incubator.foreign.MemorySegment;

/**
 * RSPdx Device
 */
public class RspDxDevice extends Device<RspDxCompositeParameters, RspDxTuner>
{
    private RspDxTuner mTuner1;

    /**
     * Constructs an SDRPlay RSPdx device from the foreign memory segment
     *
     * @param sdrPlay api instance that created this device
     * @param memorySegment of foreign memory
     */
    RspDxDevice(SDRplay sdrPlay, MemorySegment memorySegment)
    {
        super(sdrPlay, memorySegment, DeviceType.RSPdx);
    }


    @Override
    public RspDxTuner getTuner1() throws SDRplayException
    {
        if(!isSelected())
        {
            throw new SDRplayException("Device must be selected before accessing the tuner");
        }

        if(mTuner1 == null)
        {
            mTuner1 = new RspDxTuner(RspDxDevice.this, getAPI(), getTunerSelect(),
                    getCompositeParameters().getDeviceParameters(), getCompositeParameters().getTunerParameters1(),
                    getCompositeParameters().getControlParameters1());
        }

        return mTuner1;
    }
}
