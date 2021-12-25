package io.github.dsheirer.sdrplay.async;

import io.github.dsheirer.sdrplay.Status;
import io.github.dsheirer.sdrplay.UpdateReason;
import io.github.dsheirer.sdrplay.device.TunerSelect;

/**
 * Asynchronous future for an SDRplay API update operation.
 */
public class AsyncUpdateFuture extends AsyncFuture<Status>
{
    private TunerSelect mTunerSelect;
    private UpdateReason mUpdateReason;
    private UpdateReason mExpectedResponse;
    private boolean mSubmitted;

    /**
     * Creates a to-be completed future where a successful update with return success or an unsuccessful update will
     * return an exception.
     * @param tunerSelect for the tuner to be updated
     * @param updateReason of what is to be updated
     * @param expectedResponse to be received to indicate the async operation is completed
     */
    public AsyncUpdateFuture(TunerSelect tunerSelect, UpdateReason updateReason, UpdateReason expectedResponse)
    {
        mTunerSelect = tunerSelect;
        mUpdateReason = updateReason;
        mExpectedResponse = expectedResponse;
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

    /**
     * Expected response to this update operation
     */
    public UpdateReason getExpectedResponse()
    {
        return mExpectedResponse;
    }

    /**
     * Flag to indicate if this update has been submitted and is currently awaiting results.
     */
    public boolean isSubmitted()
    {
        return mSubmitted;
    }

    /**
     * Sets flag to indicate that this update has been submitted.
     */
    public void setSubmitted(boolean submitted)
    {
        mSubmitted = submitted;
    }

    /**
     * Indicates if the completed async operation matches this submitted async update future
     * @param completedAsyncUpdate to compare
     * @return true if there is a match
     */
    public boolean matches(CompletedAsyncUpdate completedAsyncUpdate)
    {
        return getTunerSelect().equals(completedAsyncUpdate.getTunerSelect()) &&
                getExpectedResponse().equals(completedAsyncUpdate.getUpdateReason());
    }
}
