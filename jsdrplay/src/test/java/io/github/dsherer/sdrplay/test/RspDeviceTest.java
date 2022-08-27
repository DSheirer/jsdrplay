package io.github.dsherer.sdrplay.test;

import com.github.dsheirer.sdrplay.DeviceDescriptor;
import com.github.dsheirer.sdrplay.DeviceSelectionMode;
import com.github.dsheirer.sdrplay.SDRplay;
import com.github.dsheirer.sdrplay.SDRplayException;
import com.github.dsheirer.sdrplay.device.Device;
import com.github.dsheirer.sdrplay.device.DeviceType;
import com.github.dsheirer.sdrplay.parameter.tuner.SampleRate;
import io.github.dsherer.sdrplay.test.listener.LoggingStreamConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit testing for an RSP1A device
 */
public class RspDeviceTest
{
    public static final Logger mLog = LoggerFactory.getLogger(RspDeviceTest.class);

    @Test
    @DisplayName("List available RSP devices")
    public void listDevices()
    {
        SDRplay api = new SDRplay();
        for(DeviceDescriptor deviceDescriptor: api.getDevices())
        {
            mLog.info(deviceDescriptor.toString());
        }

        try
        {
            api.close();
        }
        catch(Exception se)
        {
            mLog.error("Error closing API", se);
        }
    }

    /**
     * Tests an RSP1A.
     */
    @Test
    @DisplayName("Test an RSP1A device")
    public void testRSP1A()
    {
        testDevice(DeviceType.RSP1A, DeviceSelectionMode.SINGLE_TUNER_1);
    }

    /**
     * Tests an RSP1.
     */
    @Test
    @DisplayName("Test an RSP1 device")
    public void testRSP1()
    {
        testDevice(DeviceType.RSP1, DeviceSelectionMode.SINGLE_TUNER_1);
    }

    /**
     * Tests the RSPduo with the specified device selection mode
     * @param deviceSelectionMode
     */
    private void testDevice(DeviceType deviceType, DeviceSelectionMode deviceSelectionMode)
    {
        SDRplay api = new SDRplay();
        mLog.info("Version: " + api.getVersion());

        DeviceDescriptor deviceDescriptor = api.getDevice(deviceType);

        if(deviceDescriptor != null)
        {
            mLog.info("Testing: " + deviceDescriptor + " With Device Selection Mode: " + deviceSelectionMode);
            mLog.info("Available Selection Modes: " + deviceDescriptor.getDeviceSelectionModes());
            if(deviceDescriptor.getDeviceSelectionModes().contains(deviceSelectionMode))
            {
                try
                {
                    Device device = api.select(deviceDescriptor, deviceSelectionMode);

                    mLog.info("Device: " + device.toString());

                    mLog.info("Selected: " + device.getClass());

                    mLog.info("Setting Sample Rate");
                    device.setSampleRate(SampleRate.RATE_10_000);

                    mLog.info("Setting Frequencies");
                    device.getTuner().setFrequency(460_450_000);

                    mLog.info("Gain Reduction: " + device.getTuner().getGainReduction());
                    mLog.info("AGC Mode: " + device.getTuner().getAGC());
                    mLog.info("Gain: " + device.getTuner().getGain());
                    mLog.info("LO Mode:" + device.getTuner().getLoMode());
                    mLog.info("IF Mode:" + device.getTuner().getIfMode());

                    mLog.info("Capturing Samples ...");
                    LoggingStreamConsumer loggingStreamConsumer = new LoggingStreamConsumer(device);
                    loggingStreamConsumer.process(5);
                    loggingStreamConsumer.logSpectrum1();

                    device.release();
                    mLog.info("Released");
                }
                catch(SDRplayException se)
                {
                    mLog.error("Error testing device", se);
                }
            }
        }
        else
        {
            mLog.info("Unable to obtain RSP device to test");
        }

        try
        {
            api.close();
        }
        catch(Exception se)
        {
            mLog.error("Error closing api");
        }
    }

}
