// Generated by jextract

package io.github.dsheirer.sdrplay.api.v3_07;

import java.lang.invoke.VarHandle;

import jdk.incubator.foreign.*;
import static jdk.incubator.foreign.CLinker.*;
public class sdrplay_api_ResetFlagsT {

    static final MemoryLayout $struct$LAYOUT = MemoryLayout.structLayout(
        C_CHAR.withName("resetGainUpdate"),
        C_CHAR.withName("resetRfUpdate"),
        C_CHAR.withName("resetFsUpdate")
    );
    public static MemoryLayout $LAYOUT() {
        return sdrplay_api_ResetFlagsT.$struct$LAYOUT;
    }
    static final VarHandle resetGainUpdate$VH = $struct$LAYOUT.varHandle(byte.class, MemoryLayout.PathElement.groupElement("resetGainUpdate"));
    public static VarHandle resetGainUpdate$VH() {
        return sdrplay_api_ResetFlagsT.resetGainUpdate$VH;
    }
    public static byte resetGainUpdate$get(MemorySegment seg) {
        return (byte)sdrplay_api_ResetFlagsT.resetGainUpdate$VH.get(seg);
    }
    public static void resetGainUpdate$set( MemorySegment seg, byte x) {
        sdrplay_api_ResetFlagsT.resetGainUpdate$VH.set(seg, x);
    }
    public static byte resetGainUpdate$get(MemorySegment seg, long index) {
        return (byte)sdrplay_api_ResetFlagsT.resetGainUpdate$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void resetGainUpdate$set(MemorySegment seg, long index, byte x) {
        sdrplay_api_ResetFlagsT.resetGainUpdate$VH.set(seg.asSlice(index*sizeof()), x);
    }
    static final VarHandle resetRfUpdate$VH = $struct$LAYOUT.varHandle(byte.class, MemoryLayout.PathElement.groupElement("resetRfUpdate"));
    public static VarHandle resetRfUpdate$VH() {
        return sdrplay_api_ResetFlagsT.resetRfUpdate$VH;
    }
    public static byte resetRfUpdate$get(MemorySegment seg) {
        return (byte)sdrplay_api_ResetFlagsT.resetRfUpdate$VH.get(seg);
    }
    public static void resetRfUpdate$set( MemorySegment seg, byte x) {
        sdrplay_api_ResetFlagsT.resetRfUpdate$VH.set(seg, x);
    }
    public static byte resetRfUpdate$get(MemorySegment seg, long index) {
        return (byte)sdrplay_api_ResetFlagsT.resetRfUpdate$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void resetRfUpdate$set(MemorySegment seg, long index, byte x) {
        sdrplay_api_ResetFlagsT.resetRfUpdate$VH.set(seg.asSlice(index*sizeof()), x);
    }
    static final VarHandle resetFsUpdate$VH = $struct$LAYOUT.varHandle(byte.class, MemoryLayout.PathElement.groupElement("resetFsUpdate"));
    public static VarHandle resetFsUpdate$VH() {
        return sdrplay_api_ResetFlagsT.resetFsUpdate$VH;
    }
    public static byte resetFsUpdate$get(MemorySegment seg) {
        return (byte)sdrplay_api_ResetFlagsT.resetFsUpdate$VH.get(seg);
    }
    public static void resetFsUpdate$set( MemorySegment seg, byte x) {
        sdrplay_api_ResetFlagsT.resetFsUpdate$VH.set(seg, x);
    }
    public static byte resetFsUpdate$get(MemorySegment seg, long index) {
        return (byte)sdrplay_api_ResetFlagsT.resetFsUpdate$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void resetFsUpdate$set(MemorySegment seg, long index, byte x) {
        sdrplay_api_ResetFlagsT.resetFsUpdate$VH.set(seg.asSlice(index*sizeof()), x);
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

