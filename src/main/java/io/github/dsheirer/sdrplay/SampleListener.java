package io.github.dsheirer.sdrplay;

/**
 * Receives samples while a device is streaming.
 *
 * <p>Runs on a thread of the SDRplay API, so it should return quickly: copy the
 * samples and hand them to another thread for slow work. It must not call
 * {@link RspDevice#stop()} or {@link RspDevice#close()}. If it throws, no more
 * samples or events are delivered and the exception is available from
 * {@link RspDevice#listenerFailure()}; the device keeps streaming until stopped.
 *
 * <p>An RSPduo in dual tuner mode calls it from two threads, one per tuner, at
 * the same time.
 */
@FunctionalInterface
public interface SampleListener {
    /** Handles a block of samples. */
    void onSamples(SampleBuffer buffer);
}
