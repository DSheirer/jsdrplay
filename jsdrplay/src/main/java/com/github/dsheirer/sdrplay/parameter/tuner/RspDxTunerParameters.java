package com.github.dsheirer.sdrplay.parameter.tuner;

import com.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_RspDxTunerParamsT;
import jdk.incubator.foreign.MemorySegment;

/**
 * RSP-Dx Tuner Parameters structure
 */
public class RspDxTunerParameters extends TunerParameters
{
    private MemorySegment mRspDxMemorySegment;

    /**
     * Constructs an instance from the foreign memory segment.
     */
    public RspDxTunerParameters(MemorySegment tunerParametersMemorySegment, MemorySegment rspDxMemorySegment)
    {
        super(tunerParametersMemorySegment);
        mRspDxMemorySegment = rspDxMemorySegment;
    }

    /**
     * Foreign memory segment for this structure
     */
    private MemorySegment getRspDxMemorySegment()
    {
        return mRspDxMemorySegment;
    }

    /**
     * Current bandwidth setting for High Dynamic Range (HDR) mode
     */
    public HdrModeBandwidth getHdrModeBandwidth()
    {
        return HdrModeBandwidth.fromValue(sdrplay_api_RspDxTunerParamsT.hdrBw$get(getRspDxMemorySegment()));
    }

    /**
     * Sets bandwidth for High Dynamic Range (HDR) mode
     */
    public void setHdrModeBandwidth(HdrModeBandwidth bandwidth)
    {
        sdrplay_api_RspDxTunerParamsT.hdrBw$set(getRspDxMemorySegment(), bandwidth.getValue());
    }
}
