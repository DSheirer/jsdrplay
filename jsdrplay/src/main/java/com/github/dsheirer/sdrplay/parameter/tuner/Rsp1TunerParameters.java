package com.github.dsheirer.sdrplay.parameter.tuner;

import jdk.incubator.foreign.MemorySegment;

/**
 * RSP1 Tuner Parameters structure
 *
 * Note: the RSP1 doesn't have any additional settings beyond the basic tuner parameters
 */
public class Rsp1TunerParameters extends TunerParameters
{
    /**
     * Constructs an instance from the foreign memory segment.
     */
    public Rsp1TunerParameters(MemorySegment tunerParametersMemorySegment)
    {
        super(tunerParametersMemorySegment);
    }
}
