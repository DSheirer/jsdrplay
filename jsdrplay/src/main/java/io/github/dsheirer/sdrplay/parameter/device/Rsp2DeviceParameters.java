package io.github.dsheirer.sdrplay.parameter.device;

import io.github.dsheirer.sdrplay.util.Flag;
import io.github.dsheirer.sdrplay.api.sdrplay_api_DevParamsT;
import io.github.dsheirer.sdrplay.api.sdrplay_api_Rsp2ParamsT;
import jdk.incubator.foreign.MemorySegment;

/**
 * RSP-2 Device Parameters structure
 */
public class Rsp2DeviceParameters extends DeviceParameters
{
    private MemorySegment mRsp2MemorySegment;

    /**
     * Constructs an instance
     * @param memorySegment for this structure
     */
    public Rsp2DeviceParameters(MemorySegment memorySegment)
    {
        super(memorySegment);
        mRsp2MemorySegment = sdrplay_api_DevParamsT.rsp2Params$slice(memorySegment);
    }

    /**
     * Foreign memory segment representing this structure
     */
    private MemorySegment getRsp2MemorySegment()
    {
        return mRsp2MemorySegment;
    }

    /**
     * Indicates if the external reference output is enabled
     */
    public boolean isExternalReferenceOutput()
    {
        return Flag.evaluate(sdrplay_api_Rsp2ParamsT.extRefOutputEn$get(getRsp2MemorySegment()));
    }

    /**
     * Enables or disables the external reference output
     */
    public void setExternalReferenceOutput(boolean enable)
    {
        sdrplay_api_Rsp2ParamsT.extRefOutputEn$set(getRsp2MemorySegment(), Flag.of(enable));
    }
}
