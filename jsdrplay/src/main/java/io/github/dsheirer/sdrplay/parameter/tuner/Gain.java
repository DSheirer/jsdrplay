package io.github.dsheirer.sdrplay.parameter.tuner;

import io.github.dsheirer.sdrplay.util.Flag;
import io.github.dsheirer.sdrplay.api.v3_07.sdrplay_api_GainT;
import jdk.incubator.foreign.MemorySegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gain structure (sdrplay_api_GainT)
 */
public class Gain
{
    private static final Logger mLog = LoggerFactory.getLogger(Gain.class);

    private MemorySegment mMemorySegment;
    private GainValues mGainValues;

    /**
     * Constructs an instance from the foreign memory segment
     */
    public Gain(MemorySegment memorySegment)
    {
        mMemorySegment = memorySegment;
        mGainValues = new GainValues(sdrplay_api_GainT.gainVals$slice(memorySegment));
    }

    /**
     * Foreign memory segment for this structure
     */
    private MemorySegment getMemorySegment()
    {
        return mMemorySegment;
    }

    /**
     * Sets the LNA state and gain reduction from the set of gain reduction values using the specified index
     * @param gainReduction for the current frequency band
     * @param index into the gain values to use for LNA state and gain reduction
     */
    public void setGain(GainReduction gainReduction, int index)
    {

        mLog.info("Setting Gain - LNA:" + gainReduction.getLnaState(index) +
                " Gain Reduction DB:" + gainReduction.getGainReduction(index) +
                " Current LNA:" + getLNA() + " Current GRDB:" + getGainReductionDb());
        mLog.info("Gain Values: " + getGainValues());
//        setLNA(gainReduction.getLnaState(index));
//        setGainReductionDb(gainReduction.getGainReduction(index));

        //TODO: remove after testing
        setLNA(5);
        setGainReductionDb(59);

        setSynchronousUpdate(true);
    }

    /**
     * Current gain reduction
     * @return gain dB
     */
    public int getGainReductionDb()
    {
        return sdrplay_api_GainT.gRdB$get(getMemorySegment());
    }

    /**
     * Sets the gain reduction value
     * @param gainReductionDb
     */
    public void setGainReductionDb(int gainReductionDb)
    {
        sdrplay_api_GainT.gRdB$set(getMemorySegment(), gainReductionDb);
    }

    /**
     * Low Noise Amplifier (LNA) state/value
     */
    public int getLNA()
    {
        return sdrplay_api_GainT.LNAstate$get(getMemorySegment());
    }

    /**
     * Sets the Low Noise Amplifier (LNA) state
     */
    public void setLNA(int lna)
    {
        sdrplay_api_GainT.LNAstate$set(getMemorySegment(), (byte)lna);
    }

    /**
     * Specifies if LNA and gain reduction changes should be applied synchronously
     */
    public void setSynchronousUpdate(boolean syncUpdate)
    {
        sdrplay_api_GainT.syncUpdate$set(getMemorySegment(), Flag.of(syncUpdate));
    }

    /**
     * Minimum gain reduction mode
     */
    public MinimumGainReductionMode getMinimumGainReductionMode()
    {
        return MinimumGainReductionMode.fromValue(sdrplay_api_GainT.minGr$get(getMemorySegment()));
    }

    /**
     * Sets the minimum gain reduction mode
     */
    public void setMinimumGainReductionMode(MinimumGainReductionMode minimumGainReductionMode)
    {
        sdrplay_api_GainT.minGr$set(getMemorySegment(), minimumGainReductionMode.getValue());
    }

    /**
     * Gain values structure
     */
    public GainValues getGainValues()
    {
        return mGainValues;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Reduction:").append(getGainReductionDb()).append("dB");
        sb.append(" LNA:").append(getLNA());
        sb.append(" Min Gain Reduction Mode:").append(getMinimumGainReductionMode());
        sb.append(" Gain Values:").append(getGainValues());
        return sb.toString();
    }
}
