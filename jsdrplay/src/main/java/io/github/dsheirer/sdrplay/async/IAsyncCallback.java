package io.github.dsheirer.sdrplay.async;

/**
 * Callback interface to be notified when an async future has completed or has resulted in an error
 */
public interface IAsyncCallback
{
    /**
     * Callback method once the future is completed or produces an error.
     * @param future that is being monitored
     */
    void complete(AsyncFuture future);
}
