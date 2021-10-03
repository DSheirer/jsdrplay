package io.github.dsheirer.sdrplay.parameter.composite;

import io.github.dsheirer.sdrplay.device.DeviceType;
import io.github.dsheirer.sdrplay.parameter.device.Rsp2DeviceParameters;
import io.github.dsheirer.sdrplay.parameter.tuner.Rsp2TunerParameters;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * RSP2 Composite parameters (device and tuner)
 */
public class Rsp2CompositeParameters extends CompositeParameters<Rsp2DeviceParameters, Rsp2TunerParameters>
{
    /**
     * Constructs an instance from the foreign memory segment
     *
     * @param memorySegment for the composite structure in foreign memory
     * @param resourceScope for allocating additional memory segments for the sub-structures.
     */
    public Rsp2CompositeParameters(MemorySegment memorySegment, ResourceScope resourceScope)
    {
        super(DeviceType.RSP2, memorySegment, resourceScope);
    }
}
