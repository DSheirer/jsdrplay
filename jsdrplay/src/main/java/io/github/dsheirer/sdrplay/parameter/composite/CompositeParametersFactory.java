package io.github.dsheirer.sdrplay.parameter.composite;

import io.github.dsheirer.sdrplay.device.DeviceType;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.ResourceScope;

/**
 * Creates a composite parameters instance for a device type
 */
public class CompositeParametersFactory
{
    /**
     * Creates a composite parameters instance for the specified device type
     * @param deviceType to create
     * @param memorySegment of foreign memory structure for the composite parameters
     * @param resourceScope to allocate additional memory structures
     * @return instance
     */
    public static CompositeParameters create(DeviceType deviceType, MemorySegment memorySegment, ResourceScope resourceScope)
    {
        switch(deviceType)
        {
            case RSP1 -> {
                return new Rsp1CompositeParameters(memorySegment, resourceScope);
            }
            case RSP1A -> {
                return new Rsp1aCompositeParameters(memorySegment, resourceScope);
            }
            case RSP2 -> {
                return new Rsp2CompositeParameters(memorySegment, resourceScope);
            }
            case RSPduo -> {
                return new RspDuoCompositeParameters(memorySegment, resourceScope);
            }
            case RSPdx -> {
                return new RspDxCompositeParameters(memorySegment, resourceScope);
            }
        }

        throw new IllegalArgumentException("Unrecognized device type: " + deviceType);
    }
}
