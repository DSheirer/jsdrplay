package io.github.dsheirer.sdrplay.device;

import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_DeviceT;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

/**
 * sdrplay_api_DeviceT structure parser for API version 3.07
 */
public class DeviceStruct_v3_07 implements IDeviceStruct
{
    private MemorySegment mDeviceMemorySegment;
    private DeviceType mDeviceType;
    private String mSerialNumber;

    /**
     * Constructs an instance
     * @param deviceMemorySegment of foreign memory
     */
    public DeviceStruct_v3_07(MemorySegment deviceMemorySegment)
    {
        mDeviceMemorySegment = deviceMemorySegment;
        mDeviceType = DeviceType.fromValue(0xFF & sdrplay_api_DeviceT.hwVer$get(mDeviceMemorySegment));
        MemorySegment serialSegment = sdrplay_api_DeviceT.SerNo$slice(mDeviceMemorySegment);
        mSerialNumber = serialSegment.getUtf8String(0);
    }

    @Override public MemorySegment getDeviceMemorySegment()
    {
        return mDeviceMemorySegment;
    }

    @Override public String getSerialNumber()
    {
        return mSerialNumber;
    }

    @Override public DeviceType getDeviceType()
    {
        return mDeviceType;
    }

    @Override public TunerSelect getTunerSelect()
    {
        return TunerSelect.fromValue(sdrplay_api_DeviceT.tuner$get(getDeviceMemorySegment()));
    }

    @Override public RspDuoMode getRspDuoMode()
    {
        return RspDuoMode.fromValue(sdrplay_api_DeviceT.rspDuoMode$get(getDeviceMemorySegment()));
    }

    @Override
    public void setRspDuoMode(RspDuoMode mode)
    {
        sdrplay_api_DeviceT.rspDuoMode$set(getDeviceMemorySegment(), mode.getValue());
    }

    @Override public boolean isValid()
    {
        //Always returns true for version 3.07
        return true;
    }

    @Override public double getRspDuoSampleFrequency()
    {
        return sdrplay_api_DeviceT.rspDuoSampleFreq$get(getDeviceMemorySegment());
    }

    @Override
    public void setRspDuoSampleFrequency(double frequency)
    {
        sdrplay_api_DeviceT.rspDuoSampleFreq$set(getDeviceMemorySegment(), frequency);
    }

    @Override public MemoryAddress getDeviceHandle()
    {
        return sdrplay_api_DeviceT.dev$get(getDeviceMemorySegment());
    }
}
