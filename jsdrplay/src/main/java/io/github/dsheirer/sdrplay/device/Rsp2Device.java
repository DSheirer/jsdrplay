package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.SDRplayException;
import io.github.dsheirer.sdrplay.parameter.composite.Rsp2CompositeParameters;
import io.github.dsheirer.sdrplay.SDRplay;
import jdk.incubator.foreign.MemorySegment;

/**
 * RSP2 Device
 */
public class Rsp2Device extends Device<Rsp2CompositeParameters, Rsp2Tuner>
{
    private Rsp2Tuner mTuner1;

    /**
     * Constructs an SDRPlay RSP2 device from the foreign memory segment
     *
     * @param sdrPlay api instance that created this device
     * @param memorySegment of foreign memory
     */
    Rsp2Device(SDRplay sdrPlay, MemorySegment memorySegment)
    {
        super(sdrPlay, memorySegment, DeviceType.RSP2);
    }

    @Override
    public Rsp2Tuner getTuner1() throws SDRplayException
    {
        if(!isSelected())
        {
            throw new SDRplayException("Device must be selected before accessing the tuner");
        }

        if(mTuner1 == null)
        {
            mTuner1 = new Rsp2Tuner(Rsp2Device.this, getAPI(), getTunerSelect(),
                    getCompositeParameters().getDeviceParameters(), getCompositeParameters().getTunerParameters1(),
                    getCompositeParameters().getControlParameters1());
        }

        return mTuner1;
    }
}
