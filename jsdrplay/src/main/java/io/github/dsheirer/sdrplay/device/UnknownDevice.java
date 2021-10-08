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
     * @param version of the api
     * @param memorySegment foreign memory for this device
     */
    UnknownDevice(SDRplay sdrPlay, Version version, MemorySegment memorySegment)
    {
        super(sdrPlay, version, memorySegment, DeviceType.UNKNOWN);
    }

    @Override
    public RspTuner getTuner1() throws SDRplayException
    {
        throw new SDRplayException("Unrecognized device type.  Cannot construct tuner");
    }
}
