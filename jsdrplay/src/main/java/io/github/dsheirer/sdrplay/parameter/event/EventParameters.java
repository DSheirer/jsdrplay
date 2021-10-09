package io.github.dsheirer.sdrplay.parameter.event;

import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_EventParamsT;
import jdk.incubator.foreign.MemorySegment;

/**
 * Event Parameters structure (sdrplay_api_EventParamsT)
 */
public class EventParameters
{
    private GainCallbackParameters mGainCallbackParameters;
    private PowerOverloadCallbackParameters mPowerOverloadCallbackParameters;
    private RspDuoModeCallbackParameters mRspDuoModeCallbackParameters;

    /**
     * Constructs an instance from the foreign memory segment
     */
    public EventParameters(MemorySegment memorySegment)
    {
        mGainCallbackParameters = new GainCallbackParameters(sdrplay_api_EventParamsT.gainParams$slice(memorySegment));
        mPowerOverloadCallbackParameters = new PowerOverloadCallbackParameters(sdrplay_api_EventParamsT.powerOverloadParams$slice(memorySegment));
        mRspDuoModeCallbackParameters = new RspDuoModeCallbackParameters(sdrplay_api_EventParamsT.rspDuoModeParams$slice(memorySegment));
    }

    /**
     * Gain event callback parameters
     */
    public GainCallbackParameters getGainCallbackParameters()
    {
        return mGainCallbackParameters;
    }

    /**
     * Power overload event callback parameters
     */
    public PowerOverloadCallbackParameters getPowerOverloadCallbackParameters()
    {
        return mPowerOverloadCallbackParameters;
    }

    /**
     * RSPduo mode event callback parameters
     */
    public RspDuoModeCallbackParameters getRspDuoModeCallbackParameters()
    {
        return mRspDuoModeCallbackParameters;
    }
}
