package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.SDRplayException;
import io.github.dsheirer.sdrplay.Version;
import io.github.dsheirer.sdrplay.parameter.composite.Rsp1aCompositeParameters;
import io.github.dsheirer.sdrplay.SDRplay;
import jdk.incubator.foreign.MemorySegment;

/**
 * RSP1A Device
 */
public class Rsp1aDevice extends Device<Rsp1aCompositeParameters, Rsp1aTuner>
{
    private Rsp1aTuner mTuner1;

    /**
     * Constructs an SDRPlay RSP1A device from the foreign memory segment
     *
     * @param sdrPlay api instance that created this device
     * @param version of the api
     * @param memorySegment of foreign memory
     */
    Rsp1aDevice(SDRplay sdrPlay, Version version, MemorySegment memorySegment)
    {
        super(sdrPlay, version, memorySegment, DeviceType.RSP1A);
    }

    @Override
    public Rsp1aTuner getTuner1() throws SDRplayException
    {
        if(!isSelected())
        {
            throw new SDRplayException("Device must be selected before accessing the tuner");
        }

        if(mTuner1 == null)
        {
            mTuner1 = new Rsp1aTuner(Rsp1aDevice.this, getAPI(), getTunerSelect(),
                    getCompositeParameters().getDeviceParameters(), getCompositeParameters().getTunerParameters1(),
                    getCompositeParameters().getControlParameters1());
        }

        return mTuner1;
    }
}
