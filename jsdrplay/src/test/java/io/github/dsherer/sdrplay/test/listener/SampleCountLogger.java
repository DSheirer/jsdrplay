package io.github.dsherer.sdrplay.test.listener;

/**
 * Logs the cumulative/running sample count
 */
public class SampleCountLogger implements ISampleCountListener
{
    private final String mLabel;
    private final int mSampleLogInterval;

    public SampleCountLogger(String label, int sampleLoggingInterval)
    {
        mLabel = label;
        mSampleLogInterval = sampleLoggingInterval;
    }

    @Override
    public void sampleCount(long sampleCount)
    {

//        mEmitSampleLogCount++;
//
//        if(mEmitSampleLogCount % mSampleLogInterval == 0)
//        {
//            mEmitSampleLogCount = 0;
//            mLog.info(mLabel + " - Samples Received: " + mSampleCount);
//        }

    }
}
