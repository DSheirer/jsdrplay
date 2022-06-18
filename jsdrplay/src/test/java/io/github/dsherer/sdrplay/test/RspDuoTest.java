package io.github.dsherer.sdrplay.test;

import com.github.dsheirer.sdrplay.DeviceDescriptor;
import com.github.dsheirer.sdrplay.DeviceSelectionMode;
import com.github.dsheirer.sdrplay.SDRplay;
import com.github.dsheirer.sdrplay.SDRplayException;
import com.github.dsheirer.sdrplay.device.Device;
import com.github.dsheirer.sdrplay.device.DeviceType;
import com.github.dsheirer.sdrplay.device.RspDuoDevice;
import com.github.dsheirer.sdrplay.device.RspDuoDualIndependentTunerDevice;
import com.github.dsheirer.sdrplay.parameter.tuner.IfMode;
import com.github.dsheirer.sdrplay.parameter.tuner.SampleRate;
import io.github.dsherer.sdrplay.test.listener.LoggingStreamConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit testing for an RSPduo device
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class RspDuoTest
{
    private static Logger mLog = LoggerFactory.getLogger(RspDuoTest.class);

    /**
     * Tests an RSPduo configured for dual independent tuners, each controlled by a separate SDRplay API instance.
     */
    @Test
    @DisplayName("Test an RSPduo device using dual independent tuners")
    public void testDualIndependentTuners()
    {
        testDuo(DeviceSelectionMode.DUAL_INDEPENDENT_TUNERS);
    }

    /**
     * Tests an RSPduo configured for dual synchronized tuners (ie diversity mode).
     */
    @Test
    @DisplayName("Test an RSPduo device using dual synchronized tuners")
    public void testDualSynchronizedTuners()
    {
        testDuo(DeviceSelectionMode.DUAL_SYNCHRONIZED_TUNERS);
    }

    /**
     * Tests an RSPduo configured for single tuner 1.
     */
    @Test
    @DisplayName("Test an RSPduo device using single tuner mode with tuner 1")
    public void testSingleTuner1()
    {
        testDuo(DeviceSelectionMode.SINGLE_TUNER_1);
    }

    /**
     * Tests an RSPduo configured for single tuner 2.
     */
    @Test
    @DisplayName("Test an RSPduo device using single tuner mode with tuner 2")
    public void testSingleTuner2()
    {
        testDuo(DeviceSelectionMode.SINGLE_TUNER_2);
    }

    /**
     * Tests an RSPduo configured for master with tuner 1.
     */
    @Test
    @DisplayName("Test an RSPduo device using master tuner mode with tuner 1")
    public void testMasterTuner1()
    {
        testDuo(DeviceSelectionMode.MASTER_TUNER_1);
    }

    /**
     * Tests an RSPduo configured for master with tuner 2.
     */
    @Test
    @DisplayName("Test an RSPduo device using master tuner mode with tuner 2")
    public void testMasterTuner2()
    {
        testDuo(DeviceSelectionMode.MASTER_TUNER_2);
    }

    /**
     * Tests the RSPduo with the specified device selection mode
     * @param deviceSelectionMode
     */
    private void testDuo(DeviceSelectionMode deviceSelectionMode)
    {
        SDRplay api = new SDRplay();

        DeviceDescriptor deviceDescriptor = api.getDevice(DeviceType.RSPduo);

        if(deviceDescriptor != null)
        {
            mLog.info("Testing: " + deviceDescriptor + " With Device Selection Mode: " + deviceSelectionMode);

            if(deviceDescriptor.getDeviceSelectionModes().contains(deviceSelectionMode))
            {
                try
                {
                    Device device = api.select(deviceDescriptor, deviceSelectionMode);

                    if(device instanceof RspDuoDevice)
                    {
                        mLog.info("Selected: " + device.getClass());

                        if(deviceSelectionMode.isMasterMode())
                        {
                            mLog.info("Setting IF Mode");
                            device.getTuner().setIfMode(IfMode.IF_2048);

                            mLog.info("Setting Sample Rate");
                            device.setSampleRate(SampleRate.DUO_RATE_0_500);
                        }
                        else if(deviceSelectionMode.equals(DeviceSelectionMode.DUAL_SYNCHRONIZED_TUNERS))
                        {
                            mLog.info("Setting Sample Rate");
                            device.setSampleRate(SampleRate.RATE_0_600);
                        }
                        else
                        {
                            mLog.info("Setting Sample Rate");
                            device.setSampleRate(SampleRate.RATE_10_000);
                        }

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

                        if(deviceSelectionMode.equals(DeviceSelectionMode.DUAL_SYNCHRONIZED_TUNERS))
                        {
                            loggingStreamConsumer.logSpectrum2();
                        }
                    }
                    else if(device instanceof RspDuoDualIndependentTunerDevice dual)
                    {
                        mLog.info("Selected: " + device.getClass());

                        //IF Mode 2048 is the correct mode for master/slave configurations with 2.0 MHz sample rate
                        device.getTuner().setIfMode(IfMode.IF_2048);

                        mLog.info("Setting Sample Rate");
                        device.setSampleRate(SampleRate.DUO_RATE_2_000);

                        mLog.info("Setting Frequencies");
                        device.getTuner().setFrequency(460_250_000);
                        dual.getTuner2().setFrequency(453_250_000);

                        mLog.info("Capturing Samples ...");
                        LoggingStreamConsumer loggingStreamConsumer = new LoggingStreamConsumer(device);
                        loggingStreamConsumer.process(5);
                        loggingStreamConsumer.logSpectrum1();
                        loggingStreamConsumer.logSpectrum2();
                    }
                    else
                    {
                        mLog.error("Unrecognized Device Type: " + device.getClass());
                    }

                    device.release();
                    mLog.info("Released");
                }
                catch(SDRplayException se)
                {
                    mLog.error("Error releasing device", se);
                }
            }
        }
        else
        {
            mLog.info("Unable to obtain RSPduo device to test");
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
