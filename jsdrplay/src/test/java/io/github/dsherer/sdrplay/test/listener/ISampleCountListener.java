package io.github.dsherer.sdrplay.test.listener;

/**
 * Listener interface to be notified of cumulative/increasing sample count
 */
public interface ISampleCountListener
{
    /**
     * Notification of the current sample count
     */
    void sampleCount(long sampleCount);
}
