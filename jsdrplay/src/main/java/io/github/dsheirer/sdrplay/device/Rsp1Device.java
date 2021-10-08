package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.SDRplay;
import io.github.dsheirer.sdrplay.SDRplayException;
import io.github.dsheirer.sdrplay.Version;
import io.github.dsheirer.sdrplay.parameter.composite.Rsp1CompositeParameters;
import jdk.incubator.foreign.MemorySegment;

/**
 * RSP1 Device
 */
public class Rsp1Device extends Device<Rsp1CompositeParameters, Rsp1Tuner>
{
    private Rsp1Tuner mTuner1;

    /**
     * Constructs an SDRPlay RSP1 device from the foreign memory segment
     *
     * @param sdrPlay api instance that created this device
     * @param version of the api
     * @param memorySegment of foreign memory
     */
    Rsp1Device(SDRplay sdrPlay, Version version, MemorySegment memorySegment)
    {
        super(sdrPlay, version, memorySegment, DeviceType.RSP1);
    }

    @Override
    public Rsp1Tuner getTuner1() throws SDRplayException
    {
        if(!isSelected())
        {
            throw new SDRplayException("Device must be selected before accessing the tuner");
        }

        if(mTuner1 == null)
        {
            mTuner1 = new Rsp1Tuner(Rsp1Device.this, getAPI(), getTunerSelect(),
                    getCompositeParameters().getDeviceParameters(), getCompositeParameters().getTunerParameters1(),
                    getCompositeParameters().getControlParameters1());
        }

        return mTuner1;
    }
}
