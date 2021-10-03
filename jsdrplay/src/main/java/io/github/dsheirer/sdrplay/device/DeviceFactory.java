package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.SDRplay;
import io.github.dsheirer.sdrplay.api.sdrplay_api_DeviceT;
import jdk.incubator.foreign.MemorySegment;

/**
 * Factory methods for creating new Device instances
 */
public class DeviceFactory
{
    /**
     * Creates an SDRplay device from the foreign memory Device instance.
     * @param sdrPlay for device callback support
     * @param deviceMemorySegment instance for the device
     * @return correctly typed device
     */
    public static Device create(SDRplay sdrPlay, MemorySegment deviceMemorySegment)
    {
        DeviceType deviceType = getDeviceType(deviceMemorySegment);

        switch(deviceType)
        {
            case RSP1 -> {
                return new Rsp1Device(sdrPlay, deviceMemorySegment);
            }
            case RSP1A -> {
                return new Rsp1aDevice(sdrPlay, deviceMemorySegment);
            }
            case RSP2 -> {
                return new Rsp2Device(sdrPlay, deviceMemorySegment);
            }
            case RSPduo -> {
                return new RspDuoDevice(sdrPlay, deviceMemorySegment);
            }
            case RSPdx -> {
                return new RspDxDevice(sdrPlay, deviceMemorySegment);
            }
            default -> {
                return new UnknownDevice(sdrPlay, deviceMemorySegment);
            }
        }
    }

    /**
     * Identifies the sdrplay device type
     *
     * @param deviceMemorySegment for the device foreign memory segment
     */
    private static DeviceType getDeviceType(MemorySegment deviceMemorySegment)
    {
        //Mask the byte value with 0xFF to get an unsigned byte value
        return DeviceType.fromValue(0xFF & sdrplay_api_DeviceT.hwVer$get(deviceMemorySegment));
    }
}
