// Generated by jextract

package com.github.dsheirer.sdrplay.api.v3_07;

import jdk.incubator.foreign.*;

public interface sdrplay_api_StreamCallback_t {

    void apply(jdk.incubator.foreign.MemoryAddress x0, jdk.incubator.foreign.MemoryAddress x1, jdk.incubator.foreign.MemoryAddress x2, int x3, int x4, jdk.incubator.foreign.MemoryAddress x5);
    static NativeSymbol allocate(sdrplay_api_StreamCallback_t fi, ResourceScope scope) {
        return RuntimeHelper.upcallStub(sdrplay_api_StreamCallback_t.class, fi, constants$0.sdrplay_api_StreamCallback_t$FUNC, "(Ljdk/incubator/foreign/MemoryAddress;Ljdk/incubator/foreign/MemoryAddress;Ljdk/incubator/foreign/MemoryAddress;IILjdk/incubator/foreign/MemoryAddress;)V", scope);
    }
    static sdrplay_api_StreamCallback_t ofAddress(MemoryAddress addr, ResourceScope scope) {
        NativeSymbol symbol = NativeSymbol.ofAddress("sdrplay_api_StreamCallback_t::" + Long.toHexString(addr.toRawLongValue()), addr, scope);
return (jdk.incubator.foreign.MemoryAddress x0, jdk.incubator.foreign.MemoryAddress x1, jdk.incubator.foreign.MemoryAddress x2, int x3, int x4, jdk.incubator.foreign.MemoryAddress x5) -> {
            try {
                constants$0.sdrplay_api_StreamCallback_t$MH.invokeExact(symbol, (jdk.incubator.foreign.Addressable)x0, (jdk.incubator.foreign.Addressable)x1, (jdk.incubator.foreign.Addressable)x2, x3, x4, (jdk.incubator.foreign.Addressable)x5);
            } catch (Throwable ex$) {
                throw new AssertionError("should not reach here", ex$);
            }
        };
    }
}


