// Generated by jextract

package com.github.dsheirer.sdrplay.api.v3_07;

import jdk.incubator.foreign.*;

public interface sdrplay_api_SwapRspDuoActiveTuner_t {

    int apply(jdk.incubator.foreign.MemoryAddress x0, jdk.incubator.foreign.MemoryAddress x1, int x2);
    static NativeSymbol allocate(sdrplay_api_SwapRspDuoActiveTuner_t fi, ResourceScope scope) {
        return RuntimeHelper.upcallStub(sdrplay_api_SwapRspDuoActiveTuner_t.class, fi, constants$6.sdrplay_api_SwapRspDuoActiveTuner_t$FUNC, "(Ljdk/incubator/foreign/MemoryAddress;Ljdk/incubator/foreign/MemoryAddress;I)I", scope);
    }
    static sdrplay_api_SwapRspDuoActiveTuner_t ofAddress(MemoryAddress addr, ResourceScope scope) {
        NativeSymbol symbol = NativeSymbol.ofAddress("sdrplay_api_SwapRspDuoActiveTuner_t::" + Long.toHexString(addr.toRawLongValue()), addr, scope);
return (jdk.incubator.foreign.MemoryAddress x0, jdk.incubator.foreign.MemoryAddress x1, int x2) -> {
            try {
                return (int)constants$6.sdrplay_api_SwapRspDuoActiveTuner_t$MH.invokeExact(symbol, (jdk.incubator.foreign.Addressable)x0, (jdk.incubator.foreign.Addressable)x1, x2);
            } catch (Throwable ex$) {
                throw new AssertionError("should not reach here", ex$);
            }
        };
    }
}

