package io.github.dsheirer.sdrplay;

/**
 * SDRplay checked exception.
 */
public class SDRplayException extends Exception
{
    private Status mStatus = Status.UNKNOWN;

    /**
     * Creates an exception
     * @param message for the exception
     */
    public SDRplayException(String message)
    {
        super(message);
    }

    /**
     * Creates an operation exception
     * @param message for the exception
     * @param status of the operation
     */
    public SDRplayException(String message, Status status)
    {
        super(message + " Status:" + status);
        mStatus = status;
    }

    /**
     * Creates an exception
     * @param message for the exception
     * @param throwable nested exception
     */
    public SDRplayException(String message, Throwable throwable)
    {
        super(message, throwable);
    }

    public Status getStatus()
    {
        return mStatus;
    }
}
