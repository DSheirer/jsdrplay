package io.github.dsheirer.sdrplay;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/**
 * Samples delivered to a {@link SampleListener}: separate arrays of 16-bit I
 * and Q values, as the API provides them.
 *
 * <p>{@link #i()} and {@link #q()} point at memory owned by the API. They may be
 * used only on the callback's thread and only until the callback returns;
 * other access throws {@link IllegalStateException}, or
 * {@link WrongThreadException} from another thread, rather than reading
 * memory the API may have reused. Use one of the copying methods to keep the
 * data.
 *
 * @param tuner the tuner the samples are from
 * @param i the in-phase samples, {@code 2 * sampleCount} bytes of native-order {@code short}
 * @param q the quadrature samples, {@code 2 * sampleCount} bytes of native-order {@code short}
 * @param sampleCount the number of I/Q pairs
 * @param firstSampleNumber the API's sample counter at the first sample, which wraps at 2^32
 * @param reset true when the API restarted the stream, for example after a sample rate
 *     change, so this block does not follow on from the previous one
 * @param gainChanged true when a gain update took effect at the first sample of this block
 * @param frequencyChanged true when a frequency update took effect at the first sample
 * @param sampleRateChanged true when a sample rate update took effect at the first sample
 */
public record SampleBuffer(Tuner tuner, MemorySegment i, MemorySegment q, int sampleCount, long firstSampleNumber,
        boolean reset, boolean gainChanged, boolean frequencyChanged, boolean sampleRateChanged) {

    /** Scale that maps the full range of a 16-bit sample onto -1 to 1. */
    private static final float SCALE = 1.0f / 32768.0f;

    /** Copies the in-phase samples to a new array. */
    public short[] copyI() {
        return i.toArray(ValueLayout.JAVA_SHORT);
    }

    /** Copies the quadrature samples to a new array. */
    public short[] copyQ() {
        return q.toArray(ValueLayout.JAVA_SHORT);
    }

    /**
     * Copies the samples into an array as interleaved pairs: I, Q, I, Q, ...
     * The array must hold at least {@code 2 * sampleCount} values.
     */
    public void copyInterleaved(short[] destination) {
        checkLength(destination.length);
        for (int n = 0; n < sampleCount; n++) {
            destination[2 * n] = i.getAtIndex(ValueLayout.JAVA_SHORT, n);
            destination[2 * n + 1] = q.getAtIndex(ValueLayout.JAVA_SHORT, n);
        }
    }

    /** Copies the samples to a new array of interleaved pairs: I, Q, I, Q, ... */
    public short[] copyInterleaved() {
        short[] result = new short[sampleCount * 2];
        copyInterleaved(result);
        return result;
    }

    /**
     * Copies the samples into an array as interleaved floats, I, Q, I, Q, ...,
     * scaled so that the 16-bit range maps onto -1 to 1. The array must hold at
     * least {@code 2 * sampleCount} values.
     */
    public void copyInterleavedFloats(float[] destination) {
        checkLength(destination.length);
        for (int n = 0; n < sampleCount; n++) {
            destination[2 * n] = i.getAtIndex(ValueLayout.JAVA_SHORT, n) * SCALE;
            destination[2 * n + 1] = q.getAtIndex(ValueLayout.JAVA_SHORT, n) * SCALE;
        }
    }

    /** Copies the samples to a new array of interleaved, scaled floats. See {@link #copyInterleavedFloats(float[])}. */
    public float[] copyInterleavedFloats() {
        float[] result = new float[sampleCount * 2];
        copyInterleavedFloats(result);
        return result;
    }

    private void checkLength(int length) {
        if (length < sampleCount * 2) {
            throw new IndexOutOfBoundsException("Array of " + length + " is shorter than " + sampleCount * 2);
        }
    }
}
