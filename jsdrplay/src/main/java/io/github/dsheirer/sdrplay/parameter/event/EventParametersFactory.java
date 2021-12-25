package io.github.dsheirer.sdrplay.parameter.event;

import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_EventParamsT;
import jdk.incubator.foreign.MemorySegment;

/**
 * Event Parameters structure (sdrplay_api_EventParamsT)
 */
public class EventParametersFactory
{
    private GainCallbackParameters mGainCallbackParameters;
    private PowerOverloadCallbackParameters mPowerOverloadCallbackParameters;
    private RspDuoModeCallbackParameters mRspDuoModeCallbackParameters;

    /**
     * Gain event callback parameters
     */
    public static GainCallbackParameters createGainCallbackParameters(MemorySegment memorySegment)
    {
        return new GainCallbackParameters(sdrplay_api_EventParamsT.gainParams$slice(memorySegment));
    }

    /**
     * Power overload event callback parameters
     */
    public static PowerOverloadCallbackParameters createPowerOverloadCallbackParameters(MemorySegment memorySegment)
    {
        return new PowerOverloadCallbackParameters(sdrplay_api_EventParamsT.powerOverloadParams$slice(memorySegment));
    }

    /**
     * RSPduo mode event callback parameters
     */
    public static RspDuoModeCallbackParameters createRspDuoModeCallbackParameters(MemorySegment memorySegment)
    {
        return new RspDuoModeCallbackParameters(sdrplay_api_EventParamsT.rspDuoModeParams$slice(memorySegment));
    }
}
