// Generated by jextract

package com.github.dsheirer.sdrplay.api.v3_07;

import java.lang.invoke.VarHandle;
import jdk.incubator.foreign.*;

public class sdrplay_api_GainValuesT {

    static final  GroupLayout $struct$LAYOUT = MemoryLayout.structLayout(
        Constants$root.C_FLOAT$LAYOUT.withName("curr"),
        Constants$root.C_FLOAT$LAYOUT.withName("max"),
        Constants$root.C_FLOAT$LAYOUT.withName("min")
    );
    public static MemoryLayout $LAYOUT() {
        return sdrplay_api_GainValuesT.$struct$LAYOUT;
    }
    static final VarHandle curr$VH = $struct$LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("curr"));
    public static VarHandle curr$VH() {
        return sdrplay_api_GainValuesT.curr$VH;
    }
    public static float curr$get(MemorySegment seg) {
        return (float)sdrplay_api_GainValuesT.curr$VH.get(seg);
    }
    public static void curr$set( MemorySegment seg, float x) {
        sdrplay_api_GainValuesT.curr$VH.set(seg, x);
    }
    public static float curr$get(MemorySegment seg, long index) {
        return (float)sdrplay_api_GainValuesT.curr$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void curr$set(MemorySegment seg, long index, float x) {
        sdrplay_api_GainValuesT.curr$VH.set(seg.asSlice(index*sizeof()), x);
    }
    static final VarHandle max$VH = $struct$LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("max"));
    public static VarHandle max$VH() {
        return sdrplay_api_GainValuesT.max$VH;
    }
    public static float max$get(MemorySegment seg) {
        return (float)sdrplay_api_GainValuesT.max$VH.get(seg);
    }
    public static void max$set( MemorySegment seg, float x) {
        sdrplay_api_GainValuesT.max$VH.set(seg, x);
    }
    public static float max$get(MemorySegment seg, long index) {
        return (float)sdrplay_api_GainValuesT.max$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void max$set(MemorySegment seg, long index, float x) {
        sdrplay_api_GainValuesT.max$VH.set(seg.asSlice(index*sizeof()), x);
    }
    static final VarHandle min$VH = $struct$LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("min"));
    public static VarHandle min$VH() {
        return sdrplay_api_GainValuesT.min$VH;
    }
    public static float min$get(MemorySegment seg) {
        return (float)sdrplay_api_GainValuesT.min$VH.get(seg);
    }
    public static void min$set( MemorySegment seg, float x) {
        sdrplay_api_GainValuesT.min$VH.set(seg, x);
    }
    public static float min$get(MemorySegment seg, long index) {
        return (float)sdrplay_api_GainValuesT.min$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void min$set(MemorySegment seg, long index, float x) {
        sdrplay_api_GainValuesT.min$VH.set(seg.asSlice(index*sizeof()), x);
    }
    public static long sizeof() { return $LAYOUT().byteSize(); }
    public static MemorySegment allocate(SegmentAllocator allocator) { return allocator.allocate($LAYOUT()); }
    public static MemorySegment allocateArray(int len, SegmentAllocator allocator) {
        return allocator.allocate(MemoryLayout.sequenceLayout(len, $LAYOUT()));
    }
    public static MemorySegment allocate(ResourceScope scope) { return allocate(SegmentAllocator.nativeAllocator(scope)); }
    public static MemorySegment allocateArray(int len, ResourceScope scope) {
        return allocateArray(len, SegmentAllocator.nativeAllocator(scope));
    }
    public static MemorySegment ofAddress(MemoryAddress addr, ResourceScope scope) { return RuntimeHelper.asArray(addr, $LAYOUT(), 1, scope); }
}


