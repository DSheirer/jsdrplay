package com.github.dsheirer.sdrplay;

import java.util.ArrayList;
import java.util.List;

/**
 * SDRplay failed update request checked exception.
 */
public class SDRplayUpdateException extends SDRplayException
{
    private List<UpdateReason> mUpdateReasons = new ArrayList<>();

    /**
     * Creates an operation exception
     * @param status of the operation
     * @param updateReasons that were submitted
     */
    public SDRplayUpdateException(Status status, List<UpdateReason> updateReasons)
    {
        super("Unable to update device parameters: " + updateReasons, status);
    }

    /**
     * List of update reasons that failed.
     */
    public List<UpdateReason> getUpdateReasons()
    {
        return mUpdateReasons;
    }
}
