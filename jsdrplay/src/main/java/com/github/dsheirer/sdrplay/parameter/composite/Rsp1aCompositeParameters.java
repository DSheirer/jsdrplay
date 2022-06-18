package com.github.dsheirer.sdrplay.parameter.composite;

import com.github.dsheirer.sdrplay.device.DeviceType;
import com.github.dsheirer.sdrplay.parameter.device.Rsp1aDeviceParameters;
import com.github.dsheirer.sdrplay.parameter.tuner.Rsp1aTunerParameters;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * RSP1A Composite parameters (device and tuner)
 */
public class Rsp1aCompositeParameters extends CompositeParameters<Rsp1aDeviceParameters, Rsp1aTunerParameters>
{
    /**
     * Constructs an instance from the foreign memory segment
     *
     * @param memorySegment for the composite structure in foreign memory
     * @param resourceScope for allocating additional memory segments for the sub-structures.
     */
    public Rsp1aCompositeParameters(MemorySegment memorySegment, ResourceScope resourceScope)
    {
        super(DeviceType.RSP1A, memorySegment, resourceScope);
    }
}
