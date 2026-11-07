package io.github.dsheirer.sdrplay.internal;

import java.lang.foreign.MemorySegment;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Routes the API's stream and event callbacks to the device they belong to.
 *
 * <p>The API passes back the context pointer given to {@code sdrplay_api_Init}.
 * jsdrplay uses a number rather than an address there, so a callback for a
 * device that has been closed finds nothing registered and is dropped.
 *
 * <p>An exception that escapes an upcall terminates the JVM, so none may leave
 * these methods.
 */
public final class Callbacks {
    /** Receives the callbacks of one device. Implementations must not throw. */
    public interface Target {
        /**
         * Handles a block of samples from the stream of tuner A or B.
         *
         * @param streamB true for {@code StreamBCbFn}, false for {@code StreamACbFn}
         */
        void onStream(boolean streamB, MemorySegment xi, MemorySegment xq, MemorySegment params,
            int numSamples, int reset);

        /** Handles a device event. */
        void onEvent(int eventId, int tuner, MemorySegment params);
    }

    private static final Map<Long, Target> TARGETS = new ConcurrentHashMap<>();
    private static final AtomicLong NEXT_ID = new AtomicLong(1);

    private Callbacks() {
    }

    /** Registers a target and returns the context to pass to {@code sdrplay_api_Init}. */
    public static MemorySegment register(Target target) {
        long id = NEXT_ID.getAndIncrement();
        TARGETS.put(id, target);
        return MemorySegment.ofAddress(id);
    }

    /** Stops routing callbacks for a context. */
    public static void unregister(MemorySegment context) {
        TARGETS.remove(context.address());
    }

    static void streamA(MemorySegment xi, MemorySegment xq, MemorySegment params, int numSamples, int reset,
            MemorySegment context) {
        stream(false, xi, xq, params, numSamples, reset, context);
    }

    static void streamB(MemorySegment xi, MemorySegment xq, MemorySegment params, int numSamples, int reset,
            MemorySegment context) {
        stream(true, xi, xq, params, numSamples, reset, context);
    }

    private static void stream(boolean streamB, MemorySegment xi, MemorySegment xq, MemorySegment params,
            int numSamples, int reset, MemorySegment context) {
        try {
            Target target = TARGETS.get(context.address());
            if (target != null) {
                target.onStream(streamB, xi, xq, params, numSamples, reset);
            }
        } catch (Throwable t) {
            // Targets catch their own failures; this only guards the JVM.
        }
    }

    static void event(int eventId, int tuner, MemorySegment params, MemorySegment context) {
        try {
            Target target = TARGETS.get(context.address());
            if (target != null) {
                target.onEvent(eventId, tuner, params);
            }
        } catch (Throwable t) {
            // Targets catch their own failures; this only guards the JVM.
        }
    }
}
