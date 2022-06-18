package com.github.dsheirer.sdrplay;


import com.github.dsheirer.sdrplay.device.RspDuoMode;
import com.github.dsheirer.sdrplay.device.TunerSelect;

import java.util.EnumSet;

/**
 * Enumeration of modes that indicate how each SDRplay RSP device can be selected
 */
public enum DeviceSelectionMode
{
    //All RSP devices
    SINGLE_TUNER_1("Single Tuner 1", RspDuoMode.SINGLE_TUNER, TunerSelect.TUNER_1),
    SINGLE_TUNER_2("Single Tuner 2", RspDuoMode.SINGLE_TUNER, TunerSelect.TUNER_2),

    //RSPduo devices only
    //Note: dual independent tuners - the device is first claimed as master/tuner1, then a second api instance is
    //created and the second tuner is claimed as slave/tuner2
    DUAL_INDEPENDENT_TUNERS("Dual Independent Tuners", RspDuoMode.MASTER, TunerSelect.TUNER_1),
    DUAL_SYNCHRONIZED_TUNERS("Dual Synchronized Tuners", RspDuoMode.DUAL_TUNER, TunerSelect.TUNER_BOTH),
    MASTER_TUNER_1("Master - Tuner 1", RspDuoMode.MASTER, TunerSelect.TUNER_1),
    MASTER_TUNER_2("Master - Tuner 2", RspDuoMode.MASTER, TunerSelect.TUNER_2),
    SLAVE_TUNER_1("Slave - Tuner 1", RspDuoMode.SLAVE, TunerSelect.TUNER_1),
    SLAVE_TUNER_2("Slave - Tuner 2", RspDuoMode.SLAVE, TunerSelect.TUNER_2);

    String mDescription;
    RspDuoMode mRspDuoMode;
    TunerSelect mTunerSelect;

    /**
     * Private constructor
     * @param description of the mode
     * @param rspDuoMode that corresponds to the mode
     * @param tunerSelect tuner(s) that correspond to the mode
     */
    DeviceSelectionMode(String description, RspDuoMode rspDuoMode, TunerSelect tunerSelect)
    {
        mDescription = description;
        mRspDuoMode = rspDuoMode;
        mTunerSelect = tunerSelect;
    }

    /**
     * Set of all selection modes available for the RSPduo
     */
    public static final EnumSet<DeviceSelectionMode> ALL_DUO_SELECTION_MODES = EnumSet.range(DUAL_INDEPENDENT_TUNERS, SLAVE_TUNER_2);

    public static final EnumSet<DeviceSelectionMode> MASTER_MODES = EnumSet.of(MASTER_TUNER_1, MASTER_TUNER_2,
            DUAL_INDEPENDENT_TUNERS);
    /**
     * RSPduo mode associated with the selection mode
     */
    public RspDuoMode getRspDuoMode()
    {
        return mRspDuoMode;
    }

    /**
     * Indicates if this mode is designated as a master mode.
     */
    public boolean isMasterMode()
    {
        return MASTER_MODES.contains(this);
    }

    /**
     * Tuner(s) associated with the selection mode
     */
    public TunerSelect getTunerSelect()
    {
        return mTunerSelect;
    }

    @Override
    public String toString()
    {
        return mDescription;
    }
}
