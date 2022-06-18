package io.github.dsherer.sdrplay.test;

import com.github.dsheirer.sdrplay.SDRplay;
import com.github.dsheirer.sdrplay.parameter.tuner.GainReduction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SDRplayTest
{
    public static Logger mlog = LoggerFactory.getLogger(SDRplayTest.class);

    public SDRplayTest()
    {
    }

    @Test
    public void loadLibrary()
    {
        mlog.info("Loading SDRplay API");
        SDRplay sdrplay = new SDRplay();
        assertTrue(sdrplay.isAvailable(), "Library is not available");
    }

    @Test
    @DisplayName("Ensure each Gain Reduction entry has the specified number of gain indices")
    void testGainReductionIndices()
    {
        for(GainReduction gainReduction: GainReduction.values())
        {
            if(gainReduction != GainReduction.UNKNOWN)
            {
                for(int x = GainReduction.MIN_GAIN_INDEX; x <= GainReduction.MAX_GAIN_INDEX; x++)
                {
                    int grValue = gainReduction.getGainReduction(x);
                    assertTrue(0 <= grValue, "Gain reduction value greater than 0");
                    assertTrue(59 >= grValue, "Gain reduction value less than 60");

                    int lnaValue = gainReduction.getLnaState(x);
                    assertTrue(0 <= lnaValue, "LNA value greater than 0");
                    assertTrue(27 >= lnaValue, "LNA value less than 60");
                }
            }
        }
    }

}
