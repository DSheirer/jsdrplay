// Generated by jextract

package io.github.dsheirer.sdrplay.api.v3_07;

import java.lang.invoke.VarHandle;

import jdk.incubator.foreign.*;
import static jdk.incubator.foreign.CLinker.*;
public class sdrplay_api_TunerParamsT {

    static final MemoryLayout $struct$LAYOUT = MemoryLayout.structLayout(
        C_INT.withName("bwType"),
        C_INT.withName("ifType"),
        C_INT.withName("loMode"),
        MemoryLayout.structLayout(
            C_INT.withName("gRdB"),
            C_CHAR.withName("LNAstate"),
            C_CHAR.withName("syncUpdate"),
            MemoryLayout.paddingLayout(16),
            C_INT.withName("minGr"),
            MemoryLayout.structLayout(
                C_FLOAT.withName("curr"),
                C_FLOAT.withName("max"),
                C_FLOAT.withName("min")
            ).withName("gainVals")
        ).withName("gain"),
        MemoryLayout.paddingLayout(32),
        MemoryLayout.structLayout(
            C_DOUBLE.withName("rfHz"),
            C_CHAR.withName("syncUpdate"),
            MemoryLayout.paddingLayout(56)
        ).withName("rfFreq"),
        MemoryLayout.structLayout(
            C_CHAR.withName("dcCal"),
            C_CHAR.withName("speedUp"),
            MemoryLayout.paddingLayout(16),
            C_INT.withName("trackTime"),
            C_INT.withName("refreshRateTime")
        ).withName("dcOffsetTuner"),
        MemoryLayout.paddingLayout(32)
    );
    public static MemoryLayout $LAYOUT() {
        return sdrplay_api_TunerParamsT.$struct$LAYOUT;
    }
    static final VarHandle bwType$VH = $struct$LAYOUT.varHandle(int.class, MemoryLayout.PathElement.groupElement("bwType"));
    public static VarHandle bwType$VH() {
        return sdrplay_api_TunerParamsT.bwType$VH;
    }
    public static int bwType$get(MemorySegment seg) {
        return (int)sdrplay_api_TunerParamsT.bwType$VH.get(seg);
    }
    public static void bwType$set( MemorySegment seg, int x) {
        sdrplay_api_TunerParamsT.bwType$VH.set(seg, x);
    }
    public static int bwType$get(MemorySegment seg, long index) {
        return (int)sdrplay_api_TunerParamsT.bwType$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void bwType$set(MemorySegment seg, long index, int x) {
        sdrplay_api_TunerParamsT.bwType$VH.set(seg.asSlice(index*sizeof()), x);
    }
    static final VarHandle ifType$VH = $struct$LAYOUT.varHandle(int.class, MemoryLayout.PathElement.groupElement("ifType"));
    public static VarHandle ifType$VH() {
        return sdrplay_api_TunerParamsT.ifType$VH;
    }
    public static int ifType$get(MemorySegment seg) {
        return (int)sdrplay_api_TunerParamsT.ifType$VH.get(seg);
    }
    public static void ifType$set( MemorySegment seg, int x) {
        sdrplay_api_TunerParamsT.ifType$VH.set(seg, x);
    }
    public static int ifType$get(MemorySegment seg, long index) {
        return (int)sdrplay_api_TunerParamsT.ifType$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void ifType$set(MemorySegment seg, long index, int x) {
        sdrplay_api_TunerParamsT.ifType$VH.set(seg.asSlice(index*sizeof()), x);
    }
    static final VarHandle loMode$VH = $struct$LAYOUT.varHandle(int.class, MemoryLayout.PathElement.groupElement("loMode"));
    public static VarHandle loMode$VH() {
        return sdrplay_api_TunerParamsT.loMode$VH;
    }
    public static int loMode$get(MemorySegment seg) {
        return (int)sdrplay_api_TunerParamsT.loMode$VH.get(seg);
    }
    public static void loMode$set( MemorySegment seg, int x) {
        sdrplay_api_TunerParamsT.loMode$VH.set(seg, x);
    }
    public static int loMode$get(MemorySegment seg, long index) {
        return (int)sdrplay_api_TunerParamsT.loMode$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void loMode$set(MemorySegment seg, long index, int x) {
        sdrplay_api_TunerParamsT.loMode$VH.set(seg.asSlice(index*sizeof()), x);
    }
    public static MemorySegment gain$slice(MemorySegment seg) {
        return seg.asSlice(12, 24);
    }
    public static MemorySegment rfFreq$slice(MemorySegment seg) {
        return seg.asSlice(40, 16);
    }
    public static MemorySegment dcOffsetTuner$slice(MemorySegment seg) {
        return seg.asSlice(56, 12);
    }
    public static long sizeof() { return $LAYOUT().byteSize(); }
    public static MemorySegment allocate(SegmentAllocator allocator) { return allocator.allocate($LAYOUT()); }
    public static MemorySegment allocate(ResourceScope scope) { return allocate(SegmentAllocator.ofScope(scope)); }
    public static MemorySegment allocateArray(int len, SegmentAllocator allocator) {
        return allocator.allocate(MemoryLayout.sequenceLayout(len, $LAYOUT()));
    }
    public static MemorySegment allocateArray(int len, ResourceScope scope) {
        return allocateArray(len, SegmentAllocator.ofScope(scope));
    }
    public static MemorySegment ofAddress(MemoryAddress addr, ResourceScope scope) { return RuntimeHelper.asArray(addr, $LAYOUT(), 1, scope); }
}

