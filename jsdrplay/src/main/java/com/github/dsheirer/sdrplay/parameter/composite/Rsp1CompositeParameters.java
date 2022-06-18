package com.github.dsheirer.sdrplay.parameter.composite;

import com.github.dsheirer.sdrplay.device.DeviceType;
import com.github.dsheirer.sdrplay.parameter.device.Rsp1DeviceParameters;
import com.github.dsheirer.sdrplay.parameter.tuner.Rsp1TunerParameters;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * RSP1 Composite parameters (device and tuner)
 */
public class Rsp1CompositeParameters extends CompositeParameters<Rsp1DeviceParameters, Rsp1TunerParameters>
{
    /**
     * Constructs an instance from the foreign memory segment
     *
     * @param memorySegment for the composite structure in foreign memory
     * @param resourceScope for allocating additional memory segments for the sub-structures.
     */
    public Rsp1CompositeParameters(MemorySegment memorySegment, ResourceScope resourceScope)
    {
        super(DeviceType.RSP1, memorySegment, resourceScope);
    }
}
