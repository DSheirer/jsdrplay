package com.github.dsheirer.sdrplay.parameter.composite;

import com.github.dsheirer.sdrplay.device.DeviceType;
import com.github.dsheirer.sdrplay.parameter.device.RspDxDeviceParameters;
import com.github.dsheirer.sdrplay.parameter.tuner.RspDxTunerParameters;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * RSPdx Composite parameters (device and receiver)
 */
public class RspDxCompositeParameters extends CompositeParameters<RspDxDeviceParameters, RspDxTunerParameters>
{
    /**
     * Constructs an instance from the foreign memory segment
     *
     * @param memorySegment for the composite structure in foreign memory
     * @param resourceScope for allocating additional memory segments for the sub-structures.
     */
    public RspDxCompositeParameters(MemorySegment memorySegment, ResourceScope resourceScope)
    {
        super(DeviceType.RSPdx, memorySegment, resourceScope);
    }
}
