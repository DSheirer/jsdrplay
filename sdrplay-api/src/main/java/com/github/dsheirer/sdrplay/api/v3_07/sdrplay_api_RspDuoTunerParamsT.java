// Generated by jextract

package com.github.dsheirer.sdrplay.api.v3_07;

import java.lang.invoke.VarHandle;
import jdk.incubator.foreign.*;

public class sdrplay_api_RspDuoTunerParamsT {

    static final  GroupLayout $struct$LAYOUT = MemoryLayout.structLayout(
        Constants$root.C_CHAR$LAYOUT.withName("biasTEnable"),
        MemoryLayout.paddingLayout(24),
        Constants$root.C_INT$LAYOUT.withName("tuner1AmPortSel"),
        Constants$root.C_CHAR$LAYOUT.withName("tuner1AmNotchEnable"),
        Constants$root.C_CHAR$LAYOUT.withName("rfNotchEnable"),
        Constants$root.C_CHAR$LAYOUT.withName("rfDabNotchEnable"),
        MemoryLayout.paddingLayout(8)
    );
    public static MemoryLayout $LAYOUT() {
        return sdrplay_api_RspDuoTunerParamsT.$struct$LAYOUT;
    }
    static final VarHandle biasTEnable$VH = $struct$LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("biasTEnable"));
    public static VarHandle biasTEnable$VH() {
        return sdrplay_api_RspDuoTunerParamsT.biasTEnable$VH;
    }
    public static byte biasTEnable$get(MemorySegment seg) {
        return (byte)sdrplay_api_RspDuoTunerParamsT.biasTEnable$VH.get(seg);
    }
    public static void biasTEnable$set( MemorySegment seg, byte x) {
        sdrplay_api_RspDuoTunerParamsT.biasTEnable$VH.set(seg, x);
    }
    public static byte biasTEnable$get(MemorySegment seg, long index) {
        return (byte)sdrplay_api_RspDuoTunerParamsT.biasTEnable$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void biasTEnable$set(MemorySegment seg, long index, byte x) {
        sdrplay_api_RspDuoTunerParamsT.biasTEnable$VH.set(seg.asSlice(index*sizeof()), x);
    }
    static final VarHandle tuner1AmPortSel$VH = $struct$LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("tuner1AmPortSel"));
    public static VarHandle tuner1AmPortSel$VH() {
        return sdrplay_api_RspDuoTunerParamsT.tuner1AmPortSel$VH;
    }
    public static int tuner1AmPortSel$get(MemorySegment seg) {
        return (int)sdrplay_api_RspDuoTunerParamsT.tuner1AmPortSel$VH.get(seg);
    }
    public static void tuner1AmPortSel$set( MemorySegment seg, int x) {
        sdrplay_api_RspDuoTunerParamsT.tuner1AmPortSel$VH.set(seg, x);
    }
    public static int tuner1AmPortSel$get(MemorySegment seg, long index) {
        return (int)sdrplay_api_RspDuoTunerParamsT.tuner1AmPortSel$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void tuner1AmPortSel$set(MemorySegment seg, long index, int x) {
        sdrplay_api_RspDuoTunerParamsT.tuner1AmPortSel$VH.set(seg.asSlice(index*sizeof()), x);
    }
    static final VarHandle tuner1AmNotchEnable$VH = $struct$LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("tuner1AmNotchEnable"));
    public static VarHandle tuner1AmNotchEnable$VH() {
        return sdrplay_api_RspDuoTunerParamsT.tuner1AmNotchEnable$VH;
    }
    public static byte tuner1AmNotchEnable$get(MemorySegment seg) {
        return (byte)sdrplay_api_RspDuoTunerParamsT.tuner1AmNotchEnable$VH.get(seg);
    }
    public static void tuner1AmNotchEnable$set( MemorySegment seg, byte x) {
        sdrplay_api_RspDuoTunerParamsT.tuner1AmNotchEnable$VH.set(seg, x);
    }
    public static byte tuner1AmNotchEnable$get(MemorySegment seg, long index) {
        return (byte)sdrplay_api_RspDuoTunerParamsT.tuner1AmNotchEnable$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void tuner1AmNotchEnable$set(MemorySegment seg, long index, byte x) {
        sdrplay_api_RspDuoTunerParamsT.tuner1AmNotchEnable$VH.set(seg.asSlice(index*sizeof()), x);
    }
    static final VarHandle rfNotchEnable$VH = $struct$LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("rfNotchEnable"));
    public static VarHandle rfNotchEnable$VH() {
        return sdrplay_api_RspDuoTunerParamsT.rfNotchEnable$VH;
    }
    public static byte rfNotchEnable$get(MemorySegment seg) {
        return (byte)sdrplay_api_RspDuoTunerParamsT.rfNotchEnable$VH.get(seg);
    }
    public static void rfNotchEnable$set( MemorySegment seg, byte x) {
        sdrplay_api_RspDuoTunerParamsT.rfNotchEnable$VH.set(seg, x);
    }
    public static byte rfNotchEnable$get(MemorySegment seg, long index) {
        return (byte)sdrplay_api_RspDuoTunerParamsT.rfNotchEnable$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void rfNotchEnable$set(MemorySegment seg, long index, byte x) {
        sdrplay_api_RspDuoTunerParamsT.rfNotchEnable$VH.set(seg.asSlice(index*sizeof()), x);
    }
    static final VarHandle rfDabNotchEnable$VH = $struct$LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("rfDabNotchEnable"));
    public static VarHandle rfDabNotchEnable$VH() {
        return sdrplay_api_RspDuoTunerParamsT.rfDabNotchEnable$VH;
    }
    public static byte rfDabNotchEnable$get(MemorySegment seg) {
        return (byte)sdrplay_api_RspDuoTunerParamsT.rfDabNotchEnable$VH.get(seg);
    }
    public static void rfDabNotchEnable$set( MemorySegment seg, byte x) {
        sdrplay_api_RspDuoTunerParamsT.rfDabNotchEnable$VH.set(seg, x);
    }
    public static byte rfDabNotchEnable$get(MemorySegment seg, long index) {
        return (byte)sdrplay_api_RspDuoTunerParamsT.rfDabNotchEnable$VH.get(seg.asSlice(index*sizeof()));
    }
    public static void rfDabNotchEnable$set(MemorySegment seg, long index, byte x) {
        sdrplay_api_RspDuoTunerParamsT.rfDabNotchEnable$VH.set(seg.asSlice(index*sizeof()), x);
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


