// Generated by jextract

package com.github.dsheirer.sdrplay.api.v3_07;

import java.lang.invoke.VarHandle;
import jdk.incubator.foreign.*;

public class sdrplay_api_StreamCbParamsT {

    static final  GroupLayout $struct$LAYOUT = MemoryLayout.structLayout(
        Constants$root.C_INT$LAYOUT.withName("firstSampleNum"),
        Constants$root.C_INT$LAYOUT.withName("grChanged"),
        Constants$root.C_INT$LAYOUT.withName("rfChanged"),
        Constants$root.C_INT$LAYOUT.withName("fsChanged"),
        Constants$root.C_INT$LAYOUT.withName("numSamples")
    );
    public static MemoryLayout $LAYOUT() {
        return sdrplay_api_StreamCbParamsT.$struct$LAYOUT;
    }
    static final VarHandle firstSampleNum$VH = $struct$LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("firstSampleNum"));
    public static VarHandle firstSampleNum$VH() {
        return sdrplay_api_StreamCbParamsT.firstSampleNum$VH;
    }
    public static int firstSampleNum$get(MemorySegment seg) {
        return (int)sdrplay_api_StreamCbParamsT.firstSampleNum$VH.get(seg);
    }
    public static void firstSampleNum$set( MemorySegment seg, int x) {
        sdrplay_api_StreamCbParamsT.firstSampleNum$VH.set(seg, x);
    }
    public static int firstSampleNum$get(MemorySegment seg, long index) {
        return (int)sdrplay_api_StreamCbParamsT.firstSampleNum$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void firstSampleNum$set(MemorySegment seg, long index, int x) {
        sdrplay_api_StreamCbParamsT.firstSampleNum$VH.set(seg.asSlice(index*sizeof()), x);
    }
    static final VarHandle grChanged$VH = $struct$LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("grChanged"));
    public static VarHandle grChanged$VH() {
        return sdrplay_api_StreamCbParamsT.grChanged$VH;
    }
    public static int grChanged$get(MemorySegment seg) {
        return (int)sdrplay_api_StreamCbParamsT.grChanged$VH.get(seg);
    }
    public static void grChanged$set( MemorySegment seg, int x) {
        sdrplay_api_StreamCbParamsT.grChanged$VH.set(seg, x);
    }
    public static int grChanged$get(MemorySegment seg, long index) {
        return (int)sdrplay_api_StreamCbParamsT.grChanged$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void grChanged$set(MemorySegment seg, long index, int x) {
        sdrplay_api_StreamCbParamsT.grChanged$VH.set(seg.asSlice(index*sizeof()), x);
    }
    static final VarHandle rfChanged$VH = $struct$LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("rfChanged"));
    public static VarHandle rfChanged$VH() {
        return sdrplay_api_StreamCbParamsT.rfChanged$VH;
    }
    public static int rfChanged$get(MemorySegment seg) {
        return (int)sdrplay_api_StreamCbParamsT.rfChanged$VH.get(seg);
    }
    public static void rfChanged$set( MemorySegment seg, int x) {
        sdrplay_api_StreamCbParamsT.rfChanged$VH.set(seg, x);
    }
    public static int rfChanged$get(MemorySegment seg, long index) {
        return (int)sdrplay_api_StreamCbParamsT.rfChanged$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void rfChanged$set(MemorySegment seg, long index, int x) {
        sdrplay_api_StreamCbParamsT.rfChanged$VH.set(seg.asSlice(index*sizeof()), x);
    }
    static final VarHandle fsChanged$VH = $struct$LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("fsChanged"));
    public static VarHandle fsChanged$VH() {
        return sdrplay_api_StreamCbParamsT.fsChanged$VH;
    }
    public static int fsChanged$get(MemorySegment seg) {
        return (int)sdrplay_api_StreamCbParamsT.fsChanged$VH.get(seg);
    }
    public static void fsChanged$set( MemorySegment seg, int x) {
        sdrplay_api_StreamCbParamsT.fsChanged$VH.set(seg, x);
    }
    public static int fsChanged$get(MemorySegment seg, long index) {
        return (int)sdrplay_api_StreamCbParamsT.fsChanged$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void fsChanged$set(MemorySegment seg, long index, int x) {
        sdrplay_api_StreamCbParamsT.fsChanged$VH.set(seg.asSlice(index*sizeof()), x);
    }
    static final VarHandle numSamples$VH = $struct$LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("numSamples"));
    public static VarHandle numSamples$VH() {
        return sdrplay_api_StreamCbParamsT.numSamples$VH;
    }
    public static int numSamples$get(MemorySegment seg) {
        return (int)sdrplay_api_StreamCbParamsT.numSamples$VH.get(seg);
    }
    public static void numSamples$set( MemorySegment seg, int x) {
        sdrplay_api_StreamCbParamsT.numSamples$VH.set(seg, x);
    }
    public static int numSamples$get(MemorySegment seg, long index) {
        return (int)sdrplay_api_StreamCbParamsT.numSamples$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void numSamples$set(MemorySegment seg, long index, int x) {
        sdrplay_api_StreamCbParamsT.numSamples$VH.set(seg.asSlice(index*sizeof()), x);
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


