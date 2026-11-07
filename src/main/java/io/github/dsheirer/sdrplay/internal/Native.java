package io.github.dsheirer.sdrplay.internal;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_DOUBLE;
import static java.lang.foreign.ValueLayout.JAVA_INT;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * Downcall handles for the functions of the SDRplay API, and the upcall stubs
 * it calls back.
 *
 * <p>The library is loaded on the first call to {@link #get()}. If it cannot
 * be loaded, every call throws the same {@link UnsatisfiedLinkError}, so a
 * missing installation is reported clearly rather than as a class
 * initialization failure.
 *
 * <p>{@code native/check-bindings.sh} compares the function names here with
 * {@code sdrplay_api.h}.
 */
public final class Native {
    private static final Linker LINKER = Linker.nativeLinker();
    private static Native instance;
    private static UnsatisfiedLinkError failure;

    /** Where the library was loaded from. */
    public final String location;

    public final MethodHandle open;
    public final MethodHandle close;
    public final MethodHandle apiVersion;
    public final MethodHandle lockDeviceApi;
    public final MethodHandle unlockDeviceApi;
    public final MethodHandle getDevices;
    public final MethodHandle selectDevice;
    public final MethodHandle releaseDevice;
    public final MethodHandle getErrorString;
    public final MethodHandle getLastError;
    public final MethodHandle getLastErrorByType;
    public final MethodHandle disableHeartbeat;

    public final MethodHandle debugEnable;
    public final MethodHandle getDeviceParams;
    public final MethodHandle init;
    public final MethodHandle uninit;
    public final MethodHandle update;
    public final MethodHandle swapRspDuoActiveTuner;
    public final MethodHandle swapRspDuoDualTunerModeSampleRate;
    public final MethodHandle swapRspDuoMode;

    /**
     * A {@code sdrplay_api_CallbackFnsT} whose functions forward to
     * {@link Callbacks}. It lives as long as the JVM, so a callback that arrives
     * late never calls freed code.
     */
    public final MemorySegment callbackFunctions;

    private Native(NativeLibraryLoader.Loaded loaded) {
        SymbolLookup lookup = loaded.lookup();
        location = loaded.location();

        open = call(lookup, "sdrplay_api_Open");
        close = call(lookup, "sdrplay_api_Close");
        apiVersion = call(lookup, "sdrplay_api_ApiVersion", ADDRESS);
        lockDeviceApi = call(lookup, "sdrplay_api_LockDeviceApi");
        unlockDeviceApi = call(lookup, "sdrplay_api_UnlockDeviceApi");
        getDevices = call(lookup, "sdrplay_api_GetDevices", ADDRESS, ADDRESS, JAVA_INT);
        selectDevice = call(lookup, "sdrplay_api_SelectDevice", ADDRESS);
        releaseDevice = call(lookup, "sdrplay_api_ReleaseDevice", ADDRESS);
        getErrorString = pointerCall(lookup, "sdrplay_api_GetErrorString", JAVA_INT);
        getLastError = pointerCall(lookup, "sdrplay_api_GetLastError", ADDRESS);
        getLastErrorByType = pointerCall(lookup, "sdrplay_api_GetLastErrorByType", ADDRESS, JAVA_INT, ADDRESS);
        disableHeartbeat = call(lookup, "sdrplay_api_DisableHeartbeat");

        debugEnable = call(lookup, "sdrplay_api_DebugEnable", ADDRESS, JAVA_INT);
        getDeviceParams = call(lookup, "sdrplay_api_GetDeviceParams", ADDRESS, ADDRESS);
        init = call(lookup, "sdrplay_api_Init", ADDRESS, ADDRESS, ADDRESS);
        uninit = call(lookup, "sdrplay_api_Uninit", ADDRESS);
        update = call(lookup, "sdrplay_api_Update", ADDRESS, JAVA_INT, JAVA_INT, JAVA_INT);
        swapRspDuoActiveTuner = call(lookup, "sdrplay_api_SwapRspDuoActiveTuner", ADDRESS, ADDRESS, JAVA_INT);
        // The function takes the device handle, unlike its typedef in sdrplay_api.h.
        swapRspDuoDualTunerModeSampleRate =
            call(lookup, "sdrplay_api_SwapRspDuoDualTunerModeSampleRate", ADDRESS, ADDRESS, JAVA_DOUBLE);
        swapRspDuoMode = call(lookup, "sdrplay_api_SwapRspDuoMode", ADDRESS, ADDRESS, JAVA_INT, JAVA_DOUBLE,
            JAVA_INT, JAVA_INT, JAVA_INT, JAVA_INT);

        callbackFunctions = createCallbackFunctions();
    }

    /**
     * Returns the bindings, loading the library on first use.
     *
     * @throws UnsatisfiedLinkError if the SDRplay API is not installed
     */
    public static synchronized Native get() {
        if (instance == null) {
            if (failure != null) {
                throw failure;
            }
            try {
                instance = new Native(NativeLibraryLoader.load());
            } catch (UnsatisfiedLinkError e) {
                failure = e;
                throw e;
            }
        }
        return instance;
    }

    private static MethodHandle call(SymbolLookup lookup, String name, MemoryLayout... arguments) {
        return LINKER.downcallHandle(symbol(lookup, name), FunctionDescriptor.of(JAVA_INT, arguments));
    }

    private static MethodHandle pointerCall(SymbolLookup lookup, String name, MemoryLayout... arguments) {
        return LINKER.downcallHandle(symbol(lookup, name), FunctionDescriptor.of(ADDRESS, arguments));
    }

    private static MemorySegment symbol(SymbolLookup lookup, String name) {
        return lookup.find(name).orElseThrow(() ->
            new UnsatisfiedLinkError("The SDRplay API library has no function " + name
                + "; jsdrplay needs SDRplay API 3.15 or later"));
    }

    private static MemorySegment createCallbackFunctions() {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            MethodType streamType = MethodType.methodType(void.class, MemorySegment.class, MemorySegment.class,
                MemorySegment.class, int.class, int.class, MemorySegment.class);
            FunctionDescriptor streamDescriptor =
                FunctionDescriptor.ofVoid(ADDRESS, ADDRESS, ADDRESS, JAVA_INT, JAVA_INT, ADDRESS);
            MemorySegment streamA = LINKER.upcallStub(
                lookup.findStatic(Callbacks.class, "streamA", streamType), streamDescriptor, Arena.global());
            MemorySegment streamB = LINKER.upcallStub(
                lookup.findStatic(Callbacks.class, "streamB", streamType), streamDescriptor, Arena.global());
            MemorySegment event = LINKER.upcallStub(
                lookup.findStatic(Callbacks.class, "event", MethodType.methodType(void.class, int.class, int.class,
                    MemorySegment.class, MemorySegment.class)),
                FunctionDescriptor.ofVoid(JAVA_INT, JAVA_INT, ADDRESS, ADDRESS), Arena.global());

            MemorySegment functions = Arena.global().allocate(Layouts.CALLBACK_FNS);
            functions.set(ADDRESS, Layouts.offset(Layouts.CALLBACK_FNS, "StreamACbFn"), streamA);
            functions.set(ADDRESS, Layouts.offset(Layouts.CALLBACK_FNS, "StreamBCbFn"), streamB);
            functions.set(ADDRESS, Layouts.offset(Layouts.CALLBACK_FNS, "EventCbFn"), event);
            return functions;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Calls a native function returning {@code sdrplay_api_ErrT}. This boxes
     * its arguments, which is fine for the control paths using it; samples
     * arrive by upcall.
     */
    public static int invoke(MethodHandle function, Object... arguments) {
        try {
            return (int) function.invokeWithArguments(arguments);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            throw new IllegalStateException("Native call failed", t);
        }
    }

    /** Calls a native function returning a pointer. */
    public static MemorySegment invokePointer(MethodHandle function, Object... arguments) {
        try {
            return (MemorySegment) function.invokeWithArguments(arguments);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            throw new IllegalStateException("Native call failed", t);
        }
    }

    /** Returns the API's description of an error code, or null if it has none. */
    public String errorString(int code) {
        MemorySegment text = invokePointer(getErrorString, code);
        return text.equals(MemorySegment.NULL) ? null : text.reinterpret(Integer.MAX_VALUE).getString(0);
    }
}
