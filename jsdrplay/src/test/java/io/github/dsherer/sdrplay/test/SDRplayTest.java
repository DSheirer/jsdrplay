package io.github.dsherer.sdrplay.test;

import io.github.dsheirer.sdrplay.SDRplay;
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
        mlog.info("Test: loadLibrary");
        SDRplay sdRplay = new SDRplay();
        assertTrue(sdRplay.isAvailable(), "Library is not available");
        mlog.info("Test: loadLibrary complete");
    }
}
