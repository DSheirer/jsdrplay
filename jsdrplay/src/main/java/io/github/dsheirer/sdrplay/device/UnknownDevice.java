package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.SDRplay;
import io.github.dsheirer.sdrplay.SDRplayException;
import io.github.dsheirer.sdrplay.Version;
import io.github.dsheirer.sdrplay.parameter.composite.CompositeParameters;
import jdk.incubator.foreign.MemorySegment;

/**
 * Unknown or Unrecognized SDRplay Device
 */
public class UnknownDevice extends Device
{
    /**
     * Constructs an Unknown SDRPlay device from the foreign memory segment
     *
     * @param sdrPlay api instance that created this device
     * @param deviceStruct parser
     */
    UnknownDevice(SDRplay sdrPlay, IDeviceStruct deviceStruct)
    {
        super(sdrPlay, deviceStruct);
    }

    @Override
    public RspTuner getTuner() throws SDRplayException
    {
        throw new SDRplayException("Unrecognized device type.  Cannot construct tuner");
    }
}
