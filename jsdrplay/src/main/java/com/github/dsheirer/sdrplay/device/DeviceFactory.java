package com.github.dsheirer.sdrplay.device;

import com.github.dsheirer.sdrplay.SDRplay;
import com.github.dsheirer.sdrplay.Version;
import com.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_DeviceT;
import com.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_h;
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
//            return io.github.dsheirer.sdrplay.api.v3_08.sdrplay_api_DeviceT.allocateArray(sdrplay_api_h.SDRPLAY_MAX_DEVICES(), segmentAllocator);
        }
        else if(version == Version.V3_07)
        {
            return sdrplay_api_DeviceT.allocateArray(sdrplay_api_h.SDRPLAY_MAX_DEVICES(), segmentAllocator);
        }

        throw new IllegalArgumentException("Unrecognized version: " + version);
    }

    /**
     * Parses individual device memory segments from the devices array and creates Device instances for each
     * RSP model detected.
     * @param version of the API being used
     * @param sdrplay instance
     * @param devicesArray foreign memory segment
     * @param count of devices contained in the foreign memory segment devices array
     * @return zero or more RSP devices
     */
    public static List<Device> parseDevices(Version version, SDRplay sdrplay, MemorySegment devicesArray, int count)
    {
        List<Device> devices = new ArrayList<>();

        if(version.gte(Version.V3_08))
        {
//            devicesArray.elements(io.github.dsheirer.sdrplay.api.v3_08.sdrplay_api_DeviceT.$LAYOUT())
//                    .limit(count).forEach(memorySegment ->
//            {
//                devices.add(DeviceFactory.createDevice(sdrplay, memorySegment));
//            });
        }
        else if(version == Version.V3_07)
        {
            devicesArray.elements(sdrplay_api_DeviceT.$LAYOUT()).limit(count).forEach(memorySegment ->
            {
                devices.add(DeviceFactory.createDevice(sdrplay, memorySegment));
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
     * @param deviceMemorySegment instance for the device
     * @return correctly typed device
     */
    public static Device createDevice(SDRplay sdrPlay, MemorySegment deviceMemorySegment)
    {
        IDeviceStruct deviceStruct = createDeviceStruct(sdrPlay.getVersion(), deviceMemorySegment);

        switch(deviceStruct.getDeviceType())
        {
            case RSP1 -> {
                return new Rsp1Device(sdrPlay, deviceStruct);
            }
            case RSP1A -> {
                return new Rsp1aDevice(sdrPlay, deviceStruct);
            }
            case RSP2 -> {
                return new Rsp2Device(sdrPlay, deviceStruct);
            }
            case RSPduo -> {
                return new RspDuoDevice(sdrPlay, deviceStruct);
            }
            case RSPdx -> {
                return new RspDxDevice(sdrPlay, deviceStruct);
            }
            default -> {
                return new UnknownDevice(sdrPlay, deviceStruct);
            }
        }
    }

    /**
     * Creates a device structure parser for the specified API version
     * @param version to create
     * @param deviceMemorySegment
     * @return
     */
    private static IDeviceStruct createDeviceStruct(Version version, MemorySegment deviceMemorySegment)
    {
        if(version == Version.V3_07)
        {
            return new DeviceStruct_v3_07(deviceMemorySegment);
        }
//        else if(version.gte(Version.V3_08))
//        {
//            return new DeviceStruct_v3_08(deviceMemorySegment);
//        }
        else
        {
            throw new IllegalArgumentException("Unsupported version: " + version);
        }
    }
}
