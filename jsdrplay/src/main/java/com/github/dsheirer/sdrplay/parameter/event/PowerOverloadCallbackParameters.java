package com.github.dsheirer.sdrplay.parameter.event;

import com.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_PowerOverloadCbParamT;
import jdk.incubator.foreign.MemorySegment;

/**
 * Power Overload Callback Parameters (sdrplay_api_PowerOverloadCbParamT)
 */
public class PowerOverloadCallbackParameters
{
    private PowerOverloadEventType mPowerOverloadEventType;

    /**
     * Constructs an instance from the foreign memory segment
     */
    public PowerOverloadCallbackParameters(MemorySegment memorySegment)
    {
        mPowerOverloadEventType = PowerOverloadEventType
                .fromValue(sdrplay_api_PowerOverloadCbParamT.powerOverloadChangeType$get(memorySegment));
    }

    /**
     * Event type
     */
    public PowerOverloadEventType getPowerOverloadEvent()
    {
        return mPowerOverloadEventType;
    }
}
