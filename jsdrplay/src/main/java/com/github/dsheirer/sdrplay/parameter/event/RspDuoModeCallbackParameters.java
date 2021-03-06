package com.github.dsheirer.sdrplay.parameter.event;

import com.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_RspDuoModeCbParamT;
import jdk.incubator.foreign.MemorySegment;

/**
 * RSP-Duo Mode Callback Parameters (sdrplay_api_RspDuoModeCbParamT)
 */
public class RspDuoModeCallbackParameters
{
    private RspDuoModeEventType mRspDuoModeEventType;

    /**
     * Constructs an instance from the foreign memory segment
     */
    public RspDuoModeCallbackParameters(MemorySegment memorySegment)
    {
        mRspDuoModeEventType = RspDuoModeEventType.fromValue(sdrplay_api_RspDuoModeCbParamT.modeChangeType$get(memorySegment));
    }

    /**
     * Event type
     */
    public RspDuoModeEventType getRspDuoModeEvent()
    {
        return mRspDuoModeEventType;
    }
}
