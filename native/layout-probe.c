/*
 * Prints the memory layout of the SDRplay API structures as this platform's C
 * compiler sees them, along with the values of the enums and constants that
 * jsdrplay uses, one "key=value" per line:
 *
 *   struct.sdrplay_api_DeviceT=96          size of a structure
 *   field.sdrplay_api_DeviceT.dev=88       offset of a field
 *   const.sdrplay_api_BW_0_200=200         value of an enum constant or macro
 *
 * LayoutProbeTest compares every line with Layouts.java and the Java enums, and
 * also fails when Layouts.java has a structure or field missing here.
 */
#include <stddef.h>
#include <stdio.h>

#include "sdrplay_api.h"

#define STRUCT(type) printf("struct.%s=%u\n", #type, (unsigned) sizeof(type))
#define FIELD(type, field) printf("field.%s.%s=%u\n", #type, #field, (unsigned) offsetof(type, field))
#define CONST(name) printf("const.%s=%lld\n", #name, (long long) (name))

int main(void)
{
    printf("api.version=%.2f\n", (double) SDRPLAY_API_VERSION);

    /* sdrplay_api.h */
    STRUCT(sdrplay_api_DeviceT);
    FIELD(sdrplay_api_DeviceT, SerNo);
    FIELD(sdrplay_api_DeviceT, hwVer);
    FIELD(sdrplay_api_DeviceT, tuner);
    FIELD(sdrplay_api_DeviceT, rspDuoMode);
    FIELD(sdrplay_api_DeviceT, valid);
    FIELD(sdrplay_api_DeviceT, rspDuoSampleFreq);
    FIELD(sdrplay_api_DeviceT, dev);

    STRUCT(sdrplay_api_DeviceParamsT);
    FIELD(sdrplay_api_DeviceParamsT, devParams);
    FIELD(sdrplay_api_DeviceParamsT, rxChannelA);
    FIELD(sdrplay_api_DeviceParamsT, rxChannelB);

    STRUCT(sdrplay_api_ErrorInfoT);
    FIELD(sdrplay_api_ErrorInfoT, file);
    FIELD(sdrplay_api_ErrorInfoT, function);
    FIELD(sdrplay_api_ErrorInfoT, line);
    FIELD(sdrplay_api_ErrorInfoT, message);

    /* sdrplay_api_dev.h */
    STRUCT(sdrplay_api_FsFreqT);
    FIELD(sdrplay_api_FsFreqT, fsHz);
    FIELD(sdrplay_api_FsFreqT, syncUpdate);
    FIELD(sdrplay_api_FsFreqT, reCal);

    STRUCT(sdrplay_api_SyncUpdateT);
    FIELD(sdrplay_api_SyncUpdateT, sampleNum);
    FIELD(sdrplay_api_SyncUpdateT, period);

    STRUCT(sdrplay_api_ResetFlagsT);
    FIELD(sdrplay_api_ResetFlagsT, resetGainUpdate);
    FIELD(sdrplay_api_ResetFlagsT, resetRfUpdate);
    FIELD(sdrplay_api_ResetFlagsT, resetFsUpdate);

    STRUCT(sdrplay_api_DevParamsT);
    FIELD(sdrplay_api_DevParamsT, ppm);
    FIELD(sdrplay_api_DevParamsT, fsFreq);
    FIELD(sdrplay_api_DevParamsT, syncUpdate);
    FIELD(sdrplay_api_DevParamsT, resetFlags);
    FIELD(sdrplay_api_DevParamsT, mode);
    FIELD(sdrplay_api_DevParamsT, samplesPerPkt);
    FIELD(sdrplay_api_DevParamsT, rsp1aParams);
    FIELD(sdrplay_api_DevParamsT, rsp2Params);
    FIELD(sdrplay_api_DevParamsT, rspDuoParams);
    FIELD(sdrplay_api_DevParamsT, rspDxParams);

    /* sdrplay_api_tuner.h */
    STRUCT(sdrplay_api_GainValuesT);
    FIELD(sdrplay_api_GainValuesT, curr);
    FIELD(sdrplay_api_GainValuesT, max);
    FIELD(sdrplay_api_GainValuesT, min);

    STRUCT(sdrplay_api_GainT);
    FIELD(sdrplay_api_GainT, gRdB);
    FIELD(sdrplay_api_GainT, LNAstate);
    FIELD(sdrplay_api_GainT, syncUpdate);
    FIELD(sdrplay_api_GainT, minGr);
    FIELD(sdrplay_api_GainT, gainVals);

    STRUCT(sdrplay_api_RfFreqT);
    FIELD(sdrplay_api_RfFreqT, rfHz);
    FIELD(sdrplay_api_RfFreqT, syncUpdate);

    STRUCT(sdrplay_api_DcOffsetTunerT);
    FIELD(sdrplay_api_DcOffsetTunerT, dcCal);
    FIELD(sdrplay_api_DcOffsetTunerT, speedUp);
    FIELD(sdrplay_api_DcOffsetTunerT, trackTime);
    FIELD(sdrplay_api_DcOffsetTunerT, refreshRateTime);

    STRUCT(sdrplay_api_TunerParamsT);
    FIELD(sdrplay_api_TunerParamsT, bwType);
    FIELD(sdrplay_api_TunerParamsT, ifType);
    FIELD(sdrplay_api_TunerParamsT, loMode);
    FIELD(sdrplay_api_TunerParamsT, gain);
    FIELD(sdrplay_api_TunerParamsT, rfFreq);
    FIELD(sdrplay_api_TunerParamsT, dcOffsetTuner);

    /* sdrplay_api_control.h */
    STRUCT(sdrplay_api_DcOffsetT);
    FIELD(sdrplay_api_DcOffsetT, DCenable);
    FIELD(sdrplay_api_DcOffsetT, IQenable);

    STRUCT(sdrplay_api_DecimationT);
    FIELD(sdrplay_api_DecimationT, enable);
    FIELD(sdrplay_api_DecimationT, decimationFactor);
    FIELD(sdrplay_api_DecimationT, wideBandSignal);

    STRUCT(sdrplay_api_AgcT);
    FIELD(sdrplay_api_AgcT, enable);
    FIELD(sdrplay_api_AgcT, setPoint_dBfs);
    FIELD(sdrplay_api_AgcT, attack_ms);
    FIELD(sdrplay_api_AgcT, decay_ms);
    FIELD(sdrplay_api_AgcT, decay_delay_ms);
    FIELD(sdrplay_api_AgcT, decay_threshold_dB);
    FIELD(sdrplay_api_AgcT, syncUpdate);

    STRUCT(sdrplay_api_ControlParamsT);
    FIELD(sdrplay_api_ControlParamsT, dcOffset);
    FIELD(sdrplay_api_ControlParamsT, decimation);
    FIELD(sdrplay_api_ControlParamsT, agc);
    FIELD(sdrplay_api_ControlParamsT, adsbMode);

    /* sdrplay_api_rsp1a.h, sdrplay_api_rsp2.h, sdrplay_api_rspDuo.h, sdrplay_api_rspDx.h */
    STRUCT(sdrplay_api_Rsp1aParamsT);
    FIELD(sdrplay_api_Rsp1aParamsT, rfNotchEnable);
    FIELD(sdrplay_api_Rsp1aParamsT, rfDabNotchEnable);

    STRUCT(sdrplay_api_Rsp1aTunerParamsT);
    FIELD(sdrplay_api_Rsp1aTunerParamsT, biasTEnable);

    STRUCT(sdrplay_api_Rsp2ParamsT);
    FIELD(sdrplay_api_Rsp2ParamsT, extRefOutputEn);

    STRUCT(sdrplay_api_Rsp2TunerParamsT);
    FIELD(sdrplay_api_Rsp2TunerParamsT, biasTEnable);
    FIELD(sdrplay_api_Rsp2TunerParamsT, amPortSel);
    FIELD(sdrplay_api_Rsp2TunerParamsT, antennaSel);
    FIELD(sdrplay_api_Rsp2TunerParamsT, rfNotchEnable);

    STRUCT(sdrplay_api_RspDuoParamsT);
    FIELD(sdrplay_api_RspDuoParamsT, extRefOutputEn);

    STRUCT(sdrplay_api_RspDuo_ResetSlaveFlagsT);
    FIELD(sdrplay_api_RspDuo_ResetSlaveFlagsT, resetGainUpdate);
    FIELD(sdrplay_api_RspDuo_ResetSlaveFlagsT, resetRfUpdate);

    STRUCT(sdrplay_api_RspDuoTunerParamsT);
    FIELD(sdrplay_api_RspDuoTunerParamsT, biasTEnable);
    FIELD(sdrplay_api_RspDuoTunerParamsT, tuner1AmPortSel);
    FIELD(sdrplay_api_RspDuoTunerParamsT, tuner1AmNotchEnable);
    FIELD(sdrplay_api_RspDuoTunerParamsT, rfNotchEnable);
    FIELD(sdrplay_api_RspDuoTunerParamsT, rfDabNotchEnable);
    FIELD(sdrplay_api_RspDuoTunerParamsT, resetSlaveFlags);

    STRUCT(sdrplay_api_RspDxParamsT);
    FIELD(sdrplay_api_RspDxParamsT, hdrEnable);
    FIELD(sdrplay_api_RspDxParamsT, biasTEnable);
    FIELD(sdrplay_api_RspDxParamsT, antennaSel);
    FIELD(sdrplay_api_RspDxParamsT, rfNotchEnable);
    FIELD(sdrplay_api_RspDxParamsT, rfDabNotchEnable);

    STRUCT(sdrplay_api_RspDxTunerParamsT);
    FIELD(sdrplay_api_RspDxTunerParamsT, hdrBw);

    /* sdrplay_api_rx_channel.h */
    STRUCT(sdrplay_api_RxChannelParamsT);
    FIELD(sdrplay_api_RxChannelParamsT, tunerParams);
    FIELD(sdrplay_api_RxChannelParamsT, ctrlParams);
    FIELD(sdrplay_api_RxChannelParamsT, rsp1aTunerParams);
    FIELD(sdrplay_api_RxChannelParamsT, rsp2TunerParams);
    FIELD(sdrplay_api_RxChannelParamsT, rspDuoTunerParams);
    FIELD(sdrplay_api_RxChannelParamsT, rspDxTunerParams);

    /* sdrplay_api_callback.h */
    STRUCT(sdrplay_api_GainCbParamT);
    FIELD(sdrplay_api_GainCbParamT, gRdB);
    FIELD(sdrplay_api_GainCbParamT, lnaGRdB);
    FIELD(sdrplay_api_GainCbParamT, currGain);

    STRUCT(sdrplay_api_PowerOverloadCbParamT);
    FIELD(sdrplay_api_PowerOverloadCbParamT, powerOverloadChangeType);

    STRUCT(sdrplay_api_RspDuoModeCbParamT);
    FIELD(sdrplay_api_RspDuoModeCbParamT, modeChangeType);

    STRUCT(sdrplay_api_EventParamsT);
    FIELD(sdrplay_api_EventParamsT, gainParams);
    FIELD(sdrplay_api_EventParamsT, powerOverloadParams);
    FIELD(sdrplay_api_EventParamsT, rspDuoModeParams);

    STRUCT(sdrplay_api_StreamCbParamsT);
    FIELD(sdrplay_api_StreamCbParamsT, firstSampleNum);
    FIELD(sdrplay_api_StreamCbParamsT, grChanged);
    FIELD(sdrplay_api_StreamCbParamsT, rfChanged);
    FIELD(sdrplay_api_StreamCbParamsT, fsChanged);
    FIELD(sdrplay_api_StreamCbParamsT, numSamples);

    STRUCT(sdrplay_api_CallbackFnsT);
    FIELD(sdrplay_api_CallbackFnsT, StreamACbFn);
    FIELD(sdrplay_api_CallbackFnsT, StreamBCbFn);
    FIELD(sdrplay_api_CallbackFnsT, EventCbFn);

    /* Constants */
    CONST(SDRPLAY_MAX_DEVICES);
    CONST(SDRPLAY_MAX_TUNERS_PER_DEVICE);
    CONST(SDRPLAY_MAX_SER_NO_LEN);
    CONST(SDRPLAY_MAX_ROOT_NM_LEN);
    CONST(SDRPLAY_RSP1_ID);
    CONST(SDRPLAY_RSP1A_ID);
    CONST(SDRPLAY_RSP2_ID);
    CONST(SDRPLAY_RSPduo_ID);
    CONST(SDRPLAY_RSPdx_ID);
    CONST(SDRPLAY_RSP1B_ID);
    CONST(SDRPLAY_RSPdxR2_ID);
    CONST(MAX_BB_GR);

    /* sdrplay_api_ErrT */
    CONST(sdrplay_api_Success);
    CONST(sdrplay_api_Fail);
    CONST(sdrplay_api_InvalidParam);
    CONST(sdrplay_api_OutOfRange);
    CONST(sdrplay_api_GainUpdateError);
    CONST(sdrplay_api_RfUpdateError);
    CONST(sdrplay_api_FsUpdateError);
    CONST(sdrplay_api_HwError);
    CONST(sdrplay_api_AliasingError);
    CONST(sdrplay_api_AlreadyInitialised);
    CONST(sdrplay_api_NotInitialised);
    CONST(sdrplay_api_NotEnabled);
    CONST(sdrplay_api_HwVerError);
    CONST(sdrplay_api_OutOfMemError);
    CONST(sdrplay_api_ServiceNotResponding);
    CONST(sdrplay_api_StartPending);
    CONST(sdrplay_api_StopPending);
    CONST(sdrplay_api_InvalidMode);
    CONST(sdrplay_api_FailedVerification1);
    CONST(sdrplay_api_FailedVerification2);
    CONST(sdrplay_api_FailedVerification3);
    CONST(sdrplay_api_FailedVerification4);
    CONST(sdrplay_api_FailedVerification5);
    CONST(sdrplay_api_FailedVerification6);
    CONST(sdrplay_api_InvalidServiceVersion);

    /* sdrplay_api_ReasonForUpdateT */
    CONST(sdrplay_api_Update_None);
    CONST(sdrplay_api_Update_Dev_Fs);
    CONST(sdrplay_api_Update_Dev_Ppm);
    CONST(sdrplay_api_Update_Dev_SyncUpdate);
    CONST(sdrplay_api_Update_Dev_ResetFlags);
    CONST(sdrplay_api_Update_Rsp1a_BiasTControl);
    CONST(sdrplay_api_Update_Rsp1a_RfNotchControl);
    CONST(sdrplay_api_Update_Rsp1a_RfDabNotchControl);
    CONST(sdrplay_api_Update_Rsp2_BiasTControl);
    CONST(sdrplay_api_Update_Rsp2_AmPortSelect);
    CONST(sdrplay_api_Update_Rsp2_AntennaControl);
    CONST(sdrplay_api_Update_Rsp2_RfNotchControl);
    CONST(sdrplay_api_Update_Rsp2_ExtRefControl);
    CONST(sdrplay_api_Update_RspDuo_ExtRefControl);
    CONST(sdrplay_api_Update_Tuner_Gr);
    CONST(sdrplay_api_Update_Tuner_GrLimits);
    CONST(sdrplay_api_Update_Tuner_Frf);
    CONST(sdrplay_api_Update_Tuner_BwType);
    CONST(sdrplay_api_Update_Tuner_IfType);
    CONST(sdrplay_api_Update_Tuner_DcOffset);
    CONST(sdrplay_api_Update_Tuner_LoMode);
    CONST(sdrplay_api_Update_Ctrl_DCoffsetIQimbalance);
    CONST(sdrplay_api_Update_Ctrl_Decimation);
    CONST(sdrplay_api_Update_Ctrl_Agc);
    CONST(sdrplay_api_Update_Ctrl_AdsbMode);
    CONST(sdrplay_api_Update_Ctrl_OverloadMsgAck);
    CONST(sdrplay_api_Update_RspDuo_BiasTControl);
    CONST(sdrplay_api_Update_RspDuo_AmPortSelect);
    CONST(sdrplay_api_Update_RspDuo_Tuner1AmNotchControl);
    CONST(sdrplay_api_Update_RspDuo_RfNotchControl);
    CONST(sdrplay_api_Update_RspDuo_RfDabNotchControl);

    /* sdrplay_api_ReasonForUpdateExtension1T */
    CONST(sdrplay_api_Update_Ext1_None);
    CONST(sdrplay_api_Update_RspDx_HdrEnable);
    CONST(sdrplay_api_Update_RspDx_BiasTControl);
    CONST(sdrplay_api_Update_RspDx_AntennaControl);
    CONST(sdrplay_api_Update_RspDx_RfNotchControl);
    CONST(sdrplay_api_Update_RspDx_RfDabNotchControl);
    CONST(sdrplay_api_Update_RspDx_HdrBw);
    CONST(sdrplay_api_Update_RspDuo_ResetSlaveFlags);

    /* sdrplay_api_DbgLvl_t */
    CONST(sdrplay_api_DbgLvl_Disable);
    CONST(sdrplay_api_DbgLvl_Verbose);
    CONST(sdrplay_api_DbgLvl_Warning);
    CONST(sdrplay_api_DbgLvl_Error);
    CONST(sdrplay_api_DbgLvl_Message);

    /* sdrplay_api_TransferModeT */
    CONST(sdrplay_api_ISOCH);
    CONST(sdrplay_api_BULK);

    /* sdrplay_api_Bw_MHzT */
    CONST(sdrplay_api_BW_Undefined);
    CONST(sdrplay_api_BW_0_200);
    CONST(sdrplay_api_BW_0_300);
    CONST(sdrplay_api_BW_0_600);
    CONST(sdrplay_api_BW_1_536);
    CONST(sdrplay_api_BW_5_000);
    CONST(sdrplay_api_BW_6_000);
    CONST(sdrplay_api_BW_7_000);
    CONST(sdrplay_api_BW_8_000);

    /* sdrplay_api_If_kHzT */
    CONST(sdrplay_api_IF_Undefined);
    CONST(sdrplay_api_IF_Zero);
    CONST(sdrplay_api_IF_0_450);
    CONST(sdrplay_api_IF_1_620);
    CONST(sdrplay_api_IF_2_048);

    /* sdrplay_api_LoModeT */
    CONST(sdrplay_api_LO_Undefined);
    CONST(sdrplay_api_LO_Auto);
    CONST(sdrplay_api_LO_120MHz);
    CONST(sdrplay_api_LO_144MHz);
    CONST(sdrplay_api_LO_168MHz);

    /* sdrplay_api_MinGainReductionT */
    CONST(sdrplay_api_EXTENDED_MIN_GR);
    CONST(sdrplay_api_NORMAL_MIN_GR);

    /* sdrplay_api_TunerSelectT */
    CONST(sdrplay_api_Tuner_Neither);
    CONST(sdrplay_api_Tuner_A);
    CONST(sdrplay_api_Tuner_B);
    CONST(sdrplay_api_Tuner_Both);

    /* sdrplay_api_AgcControlT */
    CONST(sdrplay_api_AGC_DISABLE);
    CONST(sdrplay_api_AGC_100HZ);
    CONST(sdrplay_api_AGC_50HZ);
    CONST(sdrplay_api_AGC_5HZ);
    CONST(sdrplay_api_AGC_CTRL_EN);

    /* sdrplay_api_AdsbModeT */
    CONST(sdrplay_api_ADSB_DECIMATION);
    CONST(sdrplay_api_ADSB_NO_DECIMATION_LOWPASS);
    CONST(sdrplay_api_ADSB_NO_DECIMATION_BANDPASS_2MHZ);
    CONST(sdrplay_api_ADSB_NO_DECIMATION_BANDPASS_3MHZ);

    /* sdrplay_api_Rsp2_AntennaSelectT, sdrplay_api_Rsp2_AmPortSelectT */
    CONST(sdrplay_api_Rsp2_ANTENNA_A);
    CONST(sdrplay_api_Rsp2_ANTENNA_B);
    CONST(sdrplay_api_Rsp2_AMPORT_1);
    CONST(sdrplay_api_Rsp2_AMPORT_2);

    /* sdrplay_api_RspDuoModeT, sdrplay_api_RspDuo_AmPortSelectT */
    CONST(sdrplay_api_RspDuoMode_Unknown);
    CONST(sdrplay_api_RspDuoMode_Single_Tuner);
    CONST(sdrplay_api_RspDuoMode_Dual_Tuner);
    CONST(sdrplay_api_RspDuoMode_Master);
    CONST(sdrplay_api_RspDuoMode_Slave);
    CONST(sdrplay_api_RspDuo_AMPORT_1);
    CONST(sdrplay_api_RspDuo_AMPORT_2);

    /* sdrplay_api_RspDx_AntennaSelectT, sdrplay_api_RspDx_HdrModeBwT */
    CONST(sdrplay_api_RspDx_ANTENNA_A);
    CONST(sdrplay_api_RspDx_ANTENNA_B);
    CONST(sdrplay_api_RspDx_ANTENNA_C);
    CONST(sdrplay_api_RspDx_HDRMODE_BW_0_200);
    CONST(sdrplay_api_RspDx_HDRMODE_BW_0_500);
    CONST(sdrplay_api_RspDx_HDRMODE_BW_1_200);
    CONST(sdrplay_api_RspDx_HDRMODE_BW_1_700);

    /* Callback enums */
    CONST(sdrplay_api_Overload_Detected);
    CONST(sdrplay_api_Overload_Corrected);
    CONST(sdrplay_api_MasterInitialised);
    CONST(sdrplay_api_SlaveAttached);
    CONST(sdrplay_api_SlaveDetached);
    CONST(sdrplay_api_SlaveInitialised);
    CONST(sdrplay_api_SlaveUninitialised);
    CONST(sdrplay_api_MasterDllDisappeared);
    CONST(sdrplay_api_SlaveDllDisappeared);
    CONST(sdrplay_api_GainChange);
    CONST(sdrplay_api_PowerOverloadChange);
    CONST(sdrplay_api_DeviceRemoved);
    CONST(sdrplay_api_RspDuoModeChange);
    CONST(sdrplay_api_DeviceFailure);
    return 0;
}
