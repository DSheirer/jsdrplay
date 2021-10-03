package io.github.dsheirer.sdrplay.parameter.device;

import io.github.dsheirer.sdrplay.device.DeviceType;
import jdk.incubator.foreign.MemorySegment;

/**
 * Creates device parameters instance from a foreign memory segment
 */
public class DeviceParametersFactory
{
    /**
     * Create a device parameters instance
     * @param deviceType for the device
     * @param memorySegment of foreign memory representing the device type
     * @return instance
     */
    public static DeviceParameters create(DeviceType deviceType, MemorySegment memorySegment)
    {
        switch(deviceType)
        {
            case RSP1 -> {
                return new Rsp1DeviceParameters(memorySegment);
            }
            case RSP1A -> {
                return new Rsp1aDeviceParameters(memorySegment);
            }
            case RSP2 -> {
                return new Rsp2DeviceParameters(memorySegment);
            }
            case RSPduo -> {
                return new RspDuoDeviceParameters(memorySegment);
            }
            case RSPdx -> {
                return new RspDxDeviceParameters(memorySegment);
            }
        }

        throw new IllegalArgumentException("Unrecognized Device Type: " + deviceType);
    }
}
