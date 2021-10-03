package io.github.dsheirer.sdrplay.parameter.device;

import jdk.incubator.foreign.MemorySegment;

/**
 * RSP-1 Device Parameters structure
 */
public class Rsp1DeviceParameters extends DeviceParameters
{
    /**
     * Constructs an instance
     * @param memorySegment for this structure
     */
    public Rsp1DeviceParameters(MemorySegment memorySegment)
    {
        super(memorySegment);
    }
}
