package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.SDRplay;
import io.github.dsheirer.sdrplay.Version;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_DeviceT;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_h;
import jdk.incubator.foreign.MemorySegment;
import jdk.incubator.foreign.SegmentAllocator;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory methods for creating new Device instances
 */
public class DeviceFactory
{
    /**
     * Creates a foreign memory segment for a DeviceT array, appropriate for the specified version.
     * @param version value
     * @param segmentAllocator to allocate the foreign memory
     * @return devices array
     */
    public static MemorySegment createForeignDeviceArray(Version version, SegmentAllocator segmentAllocator)
    {
        if(version.gte(Version.V3_08))
        {
            return io.github.dsheirer.sdrplay.api.v3_08.sdrplay_api_DeviceT.allocateArray(sdrplay_api_h.SDRPLAY_MAX_DEVICES(), segmentAllocator);
        }
        else if(version == Version.V3_07)
        {
            return sdrplay_api_DeviceT.allocateArray(sdrplay_api_h.SDRPLAY_MAX_DEVICES(), segmentAllocator);
        }

        throw new IllegalArgumentException("Unrecognized version: " + version);
    }

    public static List<Device> parseDevices(Version version, SDRplay sdrplay, MemorySegment devicesArray, int count)
    {
        List<Device> devices = new ArrayList<>();

        if(version.gte(Version.V3_08))
        {
            devicesArray.elements(io.github.dsheirer.sdrplay.api.v3_08.sdrplay_api_DeviceT.$LAYOUT())
                    .limit(count).forEach(memorySegment ->
            {
                devices.add(DeviceFactory.create(sdrplay, version, memorySegment));
            });
        }
        else if(version == Version.V3_07)
        {
            devicesArray.elements(sdrplay_api_DeviceT.$LAYOUT()).limit(count).forEach(memorySegment ->
            {
                devices.add(DeviceFactory.create(sdrplay, version, memorySegment));
            });
        }
        else
        {
            throw new IllegalArgumentException("Unrecognized version: " + version);
        }

        return devices;
    }

    /**
     * Creates an SDRplay device from the foreign memory Device instance.
     * @param sdrPlay for device callback support
     * @param version of the api
     * @param deviceMemorySegment instance for the device
     * @return correctly typed device
     */
    public static Device create(SDRplay sdrPlay, Version version, MemorySegment deviceMemorySegment)
    {
        DeviceType deviceType = getDeviceType(deviceMemorySegment);

        switch(deviceType)
        {
            case RSP1 -> {
                return new Rsp1Device(sdrPlay, version, deviceMemorySegment);
            }
            case RSP1A -> {
                return new Rsp1aDevice(sdrPlay, version, deviceMemorySegment);
            }
            case RSP2 -> {
                return new Rsp2Device(sdrPlay, version, deviceMemorySegment);
            }
            case RSPduo -> {
                return new RspDuoDevice(sdrPlay, version, deviceMemorySegment);
            }
            case RSPdx -> {
                return new RspDxDevice(sdrPlay, version, deviceMemorySegment);
            }
            default -> {
                return new UnknownDevice(sdrPlay, version, deviceMemorySegment);
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
