package io.github.dsheirer.sdrplay.async;

import io.github.dsheirer.sdrplay.UpdateReason;
import io.github.dsheirer.sdrplay.device.TunerSelect;

/**
 * Completed asynchronous update operation
 */
public class CompletedAsyncUpdate
{
    private TunerSelect mTunerSelect;
    private UpdateReason mUpdateReason;

    /**
     * Creates a completed update.
     */
    public CompletedAsyncUpdate(TunerSelect tunerSelect, UpdateReason updateReason)
    {
        mTunerSelect = tunerSelect;
        mUpdateReason = updateReason;
    }

    /**
     * Tuner(s) for the update
     */
    public TunerSelect getTunerSelect()
    {
        return mTunerSelect;
    }

    /**
     * UpdateReason that is being updated
     */
    public UpdateReason getUpdateReason()
    {
        return mUpdateReason;
    }
}
