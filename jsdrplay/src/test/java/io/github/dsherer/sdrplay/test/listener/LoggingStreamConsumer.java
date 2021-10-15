package io.github.dsherer.sdrplay.test.listener;

import io.github.dsheirer.sdrplay.SDRplayException;
import io.github.dsheirer.sdrplay.callback.IDeviceEventListener;
import io.github.dsheirer.sdrplay.device.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Streaming samples consumer that can initialize a device, process streaming data for 5 seconds, and un-initialize
 * the device.
 */
public class LoggingStreamConsumer
{
    private static final Logger mLog = LoggerFactory.getLogger(LoggingStreamConsumer.class);
    private Device mDevice;
    private LoggingStreamListener mStream1Listener = new LoggingStreamListener("Stream 1");
    private LoggingStreamListener mStream2Listener = new LoggingStreamListener("Stream 2");
    private IDeviceEventListener mDeviceEventListener;

    public LoggingStreamConsumer(Device device)
    {
        mDevice = device;
        mDeviceEventListener = new LoggingDeviceEventListener(device.getDeviceType().toString(), device);
    }

    /**
     * Initializes the device, consumes samples for the specified period and then uninitializes the device.
     * @param durationMS duration of the test in seconds.
     * @throws SDRplayException
     */
    public void process(int durationSeconds) throws SDRplayException
    {
        mLog.info("Initializing device ...");
        mDevice.init(mDeviceEventListener, mStream1Listener, mStream2Listener);

        final long startTimestamp = System.currentTimeMillis();

        CountDownLatch latch = new CountDownLatch(1);

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.schedule(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    mLog.info("Un-initializing the device");
                    mDevice.uninitialize();

                    mStream1Listener.logSampleRate(System.currentTimeMillis() - startTimestamp);
                    mStream2Listener.logSampleRate(System.currentTimeMillis() - startTimestamp);

                    mLog.info("releasing device");
                    mDevice.release();
                }
                catch(SDRplayException se)
                {
                    mLog.error("Error releasing RSPduo device", se);
                }

                latch.countDown();
            }
        }, durationSeconds, TimeUnit.SECONDS);

        try
        {
            latch.await(durationSeconds + 10, TimeUnit.SECONDS);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Logs the spectral (FFT) data captured from stream 1.
     */
    public void logSpectrum1()
    {
        mStream1Listener.logSpectrum();
    }

    /**
     * Logs the spectral (FFT) data captured from stream 2.
     */
    public void logSpectrum2()
    {
        mStream2Listener.logSpectrum();
    }
}
