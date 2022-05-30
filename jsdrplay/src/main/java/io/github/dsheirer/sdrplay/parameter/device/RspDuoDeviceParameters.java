package io.github.dsheirer.sdrplay.parameter.device;

import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_DevParamsT;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_RspDuoParamsT;
import io.github.dsheirer.sdrplay.util.Flag;
import jdk.incubator.foreign.MemorySegment;

/**
 * RSPduo Device Parameters structure
 */
public class RspDuoDeviceParameters extends DeviceParameters
{
    private MemorySegment mRspDuoMemorySegment;

    /**
     * Constructs an instance
     * @param memorySegment for this structure
     */
    public RspDuoDeviceParameters(MemorySegment memorySegment)
    {
        super(memorySegment);
        mRspDuoMemorySegment = sdrplay_api_DevParamsT.rspDuoParams$slice(memorySegment);
    }

    /**
     * Foreign memory segment representing this structure
     */
    private MemorySegment getRspDuoMemorySegment()
    {
        return mRspDuoMemorySegment;
    }

    /**
     * Indicates if the external reference output is enabled
     */
    public boolean isExternalReferenceOutput()
    {
        return Flag.evaluate(sdrplay_api_RspDuoParamsT.extRefOutputEn$get(getRspDuoMemorySegment()));
    }

    /**
     * Enables or disables the external reference output
     */
    public void setExternalReferenceOutput(boolean enable)
    {
        sdrplay_api_RspDuoParamsT.extRefOutputEn$set(getRspDuoMemorySegment(), Flag.of(enable));
    }
}
