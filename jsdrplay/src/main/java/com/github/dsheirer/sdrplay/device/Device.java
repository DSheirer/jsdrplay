package com.github.dsheirer.sdrplay.device;

import com.github.dsheirer.sdrplay.async.AsyncUpdateFuture;
import com.github.dsheirer.sdrplay.async.CompletedAsyncUpdate;
import com.github.dsheirer.sdrplay.SDRplay;
import com.github.dsheirer.sdrplay.SDRplayException;
import com.github.dsheirer.sdrplay.Status;
import com.github.dsheirer.sdrplay.UpdateReason;
import com.github.dsheirer.sdrplay.callback.IDeviceEventListener;
import com.github.dsheirer.sdrplay.callback.IStreamCallbackListener;
import com.github.dsheirer.sdrplay.callback.IStreamListener;
import com.github.dsheirer.sdrplay.callback.StreamCallbackParameters;
import com.github.dsheirer.sdrplay.parameter.composite.CompositeParameters;
import com.github.dsheirer.sdrplay.parameter.tuner.SampleRate;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Abstract device structure (sdrplay_api_DeviceT)
 */
public abstract class Device<T extends CompositeParameters<?,?>, R extends RspTuner<?,?>>
{
    private static final Logger mLog = LoggerFactory.getLogger(Device.class);

    private final SDRplay mSDRplay;
    private final UpdateRequestManager mUpdateRequestManager = new UpdateRequestManager();
    private final IDeviceStruct mDeviceStruct;
    protected boolean mSelected = false;
    protected boolean mInitialized = false;
    private T mCompositeParameters;

    /**
     * Constructs an SDRPlay device from the foreign memory segment
     * @param sdrPlay api instance that created this device
     * @param deviceStruct to parse or access the fields of the device structure
     */
    public Device(SDRplay sdrPlay, IDeviceStruct deviceStruct)
    {
        mSDRplay = sdrPlay;
        mDeviceStruct = deviceStruct;
    }

    /**
     * Version specific device structure parser
     */
    protected IDeviceStruct getDeviceStruct()
    {
        return mDeviceStruct;
    }

    /**
     * API that owns this device
     */
    protected SDRplay getAPI()
    {
        return mSDRplay;
    }

    /**
     * Stream callback listener for parameter change events.
     */
    public IStreamCallbackListener getStreamCallbackListener()
    {
        return mUpdateRequestManager;
    }

    /**
     * Loads the device parameters for this device.  Subsequent calls once the parameters are created are ignored.
     * @throws SDRplayException if the device is not selected or if there is an issue loading the parameters
     */
    private void loadDeviceParameters() throws SDRplayException
    {
        if(selected() && !hasCompositeParameters())
        {
            mCompositeParameters = (T)getAPI().getCompositeParameters(Device.this, getDeviceHandle());
        }
    }

    /**
     * Selects this device for exclusive use and loads the device composite parameters.
     * @throws SDRplayException if unable to select the device or if unable to load the composite parameters for use.
     */
    public void select() throws SDRplayException
    {
        if(!selected())
        {
            getAPI().select(Device.this, getDeviceMemorySegment());
            mSelected = true;
            loadDeviceParameters();
        }
    }

    /**
     * Indicates if this device has been selected via the SDRplay api
     */
    protected boolean selected()
    {
        return mSelected;
    }

    /**
     * Indicates if the device is valid and ready for use
     */
    public boolean isValid()
    {
        return getDeviceStruct().isValid();
    }

    /**
     * Releases this device from exclusive use.
     */
    public void release() throws SDRplayException
    {
        if(selected())
        {
            mSelected = false;
            getAPI().release(Device.this, getDeviceMemorySegment());
        }
    }

    /**
     * Indicates if this device is initialized for use
     */
    public boolean isInitialized()
    {
        return mInitialized;
    }

    /**
     * Initializes this device for use and starts the tuner providing raw signal samples to the stream listener(s) and
     * device events to the event listener.
     *
     * Note: invoke select() to select this device for exclusive use before invoking this method.  If this device has
     * previously been initialized, an exception is thrown.  Use uninit() to uninitialize this device and stop the
     * sample stream and event notifications.
     *
     * @param eventListener to receive device event notifications
     * @param streamListeners one or two stream listeners to receive samples from the tuner where the first listener
     * will receive samples from tuner 1 and the optional second listener will receive samples from tuner 2 (RSPduo only).
     * Note: if only a single stream listener is specified, an empty listener is created for the seconds stream.
     * @throws SDRplayException if there is an error
     */
    public void init(IDeviceEventListener eventListener, IStreamListener ... streamListeners) throws SDRplayException
    {
        if(!isSelected())
        {
            throw new SDRplayException("Device must be selected before it can be initialized");
        }

        if(isInitialized())
        {
            throw new SDRplayException("Device has already been initialized with listeners");
        }

        List<IStreamListener> listeners = Arrays.stream(streamListeners).toList();

        if(listeners.isEmpty() || listeners.size() > 2)
        {
            throw new SDRplayException("Requires 1 or 2 stream listeners");
        }

        IStreamListener streamAListener = listeners.get(0);
        IStreamListener streamBListener = null;

        if(listeners.size() == 2)
        {
            streamBListener = listeners.get(1);
        }

        getAPI().init(Device.this, getDeviceHandle(), eventListener, streamAListener, streamBListener);
        mInitialized = true;
    }



    /**
     * Uninitializes this device from use.  Note: call is ignored if this device hasn't been initialized
     * @throws SDRplayException if there is an error
     */
    public void uninitialize() throws SDRplayException
    {
        if(isInitialized())
        {
            getAPI().uninit(this, getDeviceHandle());
            mInitialized = false;
        }
        else
        {
            throw new SDRplayException("Attempt to uninit a device that has not been initialized previously");
        }
    }

    /**
     * Updates this device after parameter change, only when the device is initialized.  If the device is not yet
     * initialized, the update request is ignored.
     *
     * @throws SDRplayException if unable to apply updates
     */
    protected void update(TunerSelect tunerSelect, UpdateReason ... updateReasons) throws SDRplayException
    {
        if(isInitialized())
        {
            submitUpdate(tunerSelect, updateReasons);
        }
    }

    /**
     * Asynchronous update request.  This method should only be used for Frequency, Gain and Sample Rate updates.
     * @param tunerSelect tuner being updated
     * @param updateReason for the parameter that is being updated
     * @param expectedResponse that is one of Gain, Frequency, or Sample Rate.
     * @return a future that has already been completed, or if initialized a future that will be completed.
     */
    protected AsyncUpdateFuture updateAsync(TunerSelect tunerSelect, UpdateReason updateReason, UpdateReason expectedResponse)
    {
        if(!expectedResponse.isAsyncUpdateResponse())
        {
            throw new IllegalArgumentException("Invalid expected response: " + expectedResponse +
                    ". Valid values are: " + UpdateReason.ASYNC_UPDATE_RESPONSES);
        }

        if(isInitialized())
        {
            return mUpdateRequestManager.update(tunerSelect, updateReason, expectedResponse);
        }

        //If not initialized, return success.
        AsyncUpdateFuture future = new AsyncUpdateFuture(tunerSelect, updateReason, expectedResponse);
        future.setResult(Status.SUCCESS);
        return future;
    }

    /**
     * Submits an update request to the API.  This method is used/managed by the update request manager.
     * @param tunerSelect for the update
     * @param updateReasons to apply
     * @throws SDRplayException if there is an issue.
     */
    private void submitUpdate(TunerSelect tunerSelect, UpdateReason ... updateReasons) throws SDRplayException
    {
        getAPI().update(Device.this, getDeviceHandle(), tunerSelect, updateReasons);
    }

    /**
     * Acknowledge tuner power overload events
     * @param tunerSelect identifying which tuner(s)
     * @throws SDRplayException on error
     */
    public void acknowledgePowerOverload(TunerSelect tunerSelect) throws SDRplayException
    {
        //There's a bug (feature?) in the API ... when you un-initialize the device, it causes a power overload event
        // and if you acknowledge it, you get an error that the device is not initialized.
        if(isInitialized())
        {
            update(tunerSelect, UpdateReason.CONTROL_OVERLOAD_MESSAGE_ACK);
        }
    }

    /**
     * Tuner selection.
     * @return tuner select.  Defaults to TUNER_1 for all but the RSPduo where it is overridden for tuner 2.
     */
    public TunerSelect getTunerSelect()
    {
        return TunerSelect.TUNER_1;
    }

    /**
     * Foreign memory segment representing this device.
     */
    protected MemorySegment getDeviceMemorySegment()
    {
        return getDeviceStruct().getDeviceMemorySegment();
    }

    /**
     * Handle to this device.
     *
     * Note: this device must be selected for exclusive use before you can access this handle.
     *
     * @throws SDRplayException if this method is accessed before the device has been successfully selected
     */
    MemoryAddress getDeviceHandle() throws SDRplayException
    {
        if(!selected())
        {
            throw new SDRplayException("This device must be selected for exclusive use before accessing/using the " +
                    "device handle");
        }

        return getDeviceStruct().getDeviceHandle();
    }

    /**
     * Tuner for this device
     * @return tuner appropriate for the device type
     * @throws SDRplayException for various reasons include device not selected or API unavailable
     */
    public abstract R getTuner() throws SDRplayException;

    /**
     * Composite parameters for this device
     */
    //TODO: change back to package private
    public T getCompositeParameters()
    {
        return mCompositeParameters;
    }

    /**
     * Sets the composite parameters
     * @param compositeParameters to apply
     */
    public void setCompositeParameters(T compositeParameters)
    {
        mCompositeParameters = compositeParameters;
    }

    /**
     * Indicates if this device has composite parameters
     */
    boolean hasCompositeParameters()
    {
        return mCompositeParameters != null;
    }

    /**
     * Indicates if this device is available and has been selected for exclusive use.
     */
    public boolean isSelected()
    {
        return mSelected;
    }

    /**
     * Device type
     */
    public DeviceType getDeviceType()
    {
        return getDeviceStruct().getDeviceType();
    }

    /**
     * Serial number
     */
    public String getSerialNumber()
    {
        return getDeviceStruct().getSerialNumber();
    }

    /**
     * Current sample rate
     */
    public double getSampleRate()
    {
        return getCompositeParameters().getDeviceParameters().getSamplingFrequency().getSampleRate();
    }

    /**
     * Sets the specified sample rate
     *
     * @param sampleRate to apply
     * @throws SDRplayException if the device is not selected or available
     */
    public void setSampleRate(SampleRate sampleRate) throws SDRplayException
    {
        getTuner().setBandwidth(sampleRate.getBandwidth());

        getCompositeParameters().getDeviceParameters().getSamplingFrequency().setSampleRate(sampleRate);
        update(getTunerSelect(), UpdateReason.DEVICE_SAMPLE_RATE);

        setDecimation(sampleRate.getDecimation());
    }

    /**
     * Sets the decimation factor for the final sample rate.
     * @param decimation as an integer multiple of two (e.g. 1, 2, 4, 8)
     * @throws SDRplayException if there is an error while setting decimation
     */
    public void setDecimation(int decimation) throws SDRplayException
    {
        if((decimation != 1) && (decimation % 2 != 0))
        {
            throw new IllegalArgumentException("Invalid decimation rate [" + decimation + "] - must be 1 or an integer " +
                    "multiple of 2 (e.g. 1, 2, 4, 8, etc)");
        }

        getCompositeParameters().getControlAParameters().getDecimation().setDecimationFactor(decimation);
        getCompositeParameters().getControlAParameters().getDecimation().setEnabled(decimation != 1);
        update(getTunerSelect(), UpdateReason.CONTROL_DECIMATION);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("SDRPplay Device").append("\n");
        sb.append("\tType: ").append(getDeviceType()).append("\n");
        sb.append("\tSerial Number: ").append(getSerialNumber()).append("\n");
        sb.append("\tSelected: ").append(isSelected());
        if(hasCompositeParameters())
        {
            sb.append("\t").append(getCompositeParameters());
        }

        return sb.toString();
    }

    /**
     * Thread-safe manager for asynchronous update request queue processing for an initialized RSP device.
     *
     * Once a device has been initialized, any changes to frequency, gain or sample rate require submitting an update
     * request to the device to apply the parameter change(s).  However, since the API supports non-blocking operation,
     * the operation executes asynchronously. The API supports only a single update request operation
     * to be in-progress at a time and any overlapping update requests are met with an unsuccessful status code return
     * from the update method.
     */
    class UpdateRequestManager implements IStreamCallbackListener
    {
        private static final long UPDATE_QUEUE_PROCESSING_INTERVAL_MS = 75;
        private final ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();
        private final Queue<AsyncUpdateFuture> mUpdateQueue = new ConcurrentLinkedQueue();
        private final Queue<CompletedAsyncUpdate> mCompletedUpdateQueue = new ConcurrentLinkedQueue();
        private final ReentrantLock mLock = new ReentrantLock();


        /**
         * Submits an update request for the specified tuner and update reason for queued processing.  This is a
         * non-blocking operation and the update is performed by a thread from the thread pool.
         * @param tunerSelect to apply the update
         * @param updateReason to update
         * @return an asynchronous future to monitor the progress of the update request
         */
        public AsyncUpdateFuture update(TunerSelect tunerSelect, UpdateReason updateReason, UpdateReason expectedResponse)
        {
            AsyncUpdateFuture future = new AsyncUpdateFuture(tunerSelect, updateReason, expectedResponse);
            mUpdateQueue.add(future);
            processQueuesImmediately();
            return future;
        }

        /**
         * Schedules a process queues task for immediate execution.  Non-blocking.
         */
        private void processQueuesImmediately()
        {
            processQueuesAfterDelay(0);
        }

        /**
         * Schedules a process queues task after the delay.  Non-blocking
         * @param delay in milliseconds, or zero for immediate.
         */
        private void processQueuesAfterDelay(long delay)
        {
            mExecutorService.schedule(() -> processQueues(), delay, TimeUnit.MILLISECONDS);
        }

        /**
         * Processes the pending and completed update operation queues.  Submits new update requests one at a time and
         * awaits a stream callback notification that the matching update reason has been applied/updated.  Ensures
         * that only a single update operation is in-progress at any given time.
         */
        private synchronized void processQueues()
        {
            mLock.lock();

            try
            {
                if(mUpdateQueue.isEmpty())
                {
                    //If we have no pending updates, we don't care about any completed update results
                    mCompletedUpdateQueue.clear();
                }
                else
                {
                    mLog.info("Processing Update Queue - " + mUpdateQueue.size() + " elements");
                    boolean processing = true;

                    while(processing)
                    {
                        processing = false;

                        AsyncUpdateFuture futureUpdate = mUpdateQueue.peek();

                        if(futureUpdate != null)
                        {
                            mLog.info("Update Queue Head Element: " + futureUpdate.getTunerSelect() + " " +
                                    futureUpdate.getUpdateReason() + " Submitted:" + futureUpdate.isSubmitted());

                            if(futureUpdate.isSubmitted())
                            {
                                //Process the completion queue
                                while(!mCompletedUpdateQueue.isEmpty())
                                {
                                    CompletedAsyncUpdate completedUpdate = mCompletedUpdateQueue.poll();

                                    if(completedUpdate != null)
                                    {
                                        mLog.info("Completed Queue Processing - " + completedUpdate.getUpdateReason());
                                        if(futureUpdate.matches(completedUpdate))
                                        {
                                            mLog.info("Completed match found - clearing queue");
                                            //Clear the remaining completed updates
                                            mCompletedUpdateQueue.clear();

                                            //Remove and (successfully) complete the current future
                                            mUpdateQueue.remove();
                                            futureUpdate.setResult(Status.SUCCESS);

                                            //Signal to immediately reprocess the queue
                                            processing = true;

                                            //Break out of the completed update queue processing
                                            break;
                                        }
                                    }
                                }
                            }
                            else
                            {
                                //Clear the completed queue and submit the update
                                mCompletedUpdateQueue.clear();

                                try
                                {
                                    submitUpdate(futureUpdate.getTunerSelect(), futureUpdate.getUpdateReason());
                                    futureUpdate.setSubmitted(true);
                                }
                                catch(SDRplayException se)
                                {
                                    futureUpdate = mUpdateQueue.poll();
                                    futureUpdate.setError(se);
                                    //Set continuous to true to immediately reprocess the next update
                                    processing = true;
                                }
                            }
                        }
                    }
                }
            }
            finally
            {
                mLock.unlock();
            }

            if(!mUpdateQueue.isEmpty())
            {
                processQueuesAfterDelay(UPDATE_QUEUE_PROCESSING_INTERVAL_MS);
            }
        }

        /**
         * Receives an update completion event.  This is a non-blocking operation since this method will be invoked
         * by the stream callback thread, and we don't want to impact the delivery of streaming samples or events.
         *
         * @param tunerSelect tuner that was updated
         * @param updateReason for what was updated
         */
        public void completed(TunerSelect tunerSelect, UpdateReason updateReason)
        {
            mCompletedUpdateQueue.add(new CompletedAsyncUpdate(tunerSelect, updateReason));
            mExecutorService.schedule(this::processQueues, 0, TimeUnit.MILLISECONDS);
        }

        /**
         * Resets the update queue.
         */
        public void reset()
        {
            mLock.lock();

            try
            {
                while(!mUpdateQueue.isEmpty())
                {
                    AsyncUpdateFuture future = mUpdateQueue.poll();
                    future.setResult(Status.UNKNOWN);
                }
            }
            finally
            {
                mLock.unlock();
            }
        }

        /**
         * Implements the IStreamCallbackListener interface to receive change notifications from update requests.
         * @param parameters to process
         * @param reset value with flags
         */
        @Override
        public void process(TunerSelect tunerSelect, StreamCallbackParameters parameters, int reset)
        {
            if(parameters.isSampleRateChanged())
            {
                mLog.info("Completing sample rate change");
                completed(tunerSelect, UpdateReason.DEVICE_SAMPLE_RATE);
            }
            if(parameters.isRfFrequencyChanged())
            {
                mLog.info("Completing frequency change");
                completed(tunerSelect, UpdateReason.TUNER_FREQUENCY_RF);
            }
            if(parameters.isGainReductionChanged())
            {
                mLog.info("Completing gain reduction change");
                completed(tunerSelect, UpdateReason.TUNER_GAIN_REDUCTION);
            }
        }
    }
}
