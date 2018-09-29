package com.example.dell.ycjktest;

/**
 * Created by dell on 2018/8/16.
 */

public class ReadData {
    public boolean isTestStatus() {
        return DaqStatus;
    }

    public boolean isFaultStatus() {
        return FaultStatus;
    }

    public float getCurrent_Float() {
        return Current_Float;
    }

    public float getCapacityByCurrent_Float() {
        return CapacityByCurrent_Float;
    }

    public float getRTSOC_Float() {
        return RTSOC_Float;
    }

    public float getInitSOC_Float() {
        return InitSOC_Float;
    }

    public float getNominalCapacity_Float() {
        return NominalCapacity_Float;
    }

    public float getRTCapacityDiff() {
        return RTCapacityDiff_Float;
    }

    public float getDevBattaryPower() {
        return DevBattaryPower_Float;
    }

    public float getHardDiskCapacity() {
        return HardDiskCapacity_Float;
    }

    public float getTesTime() {
        return TesTime_Float;
    }

    public int getLiveCounter() {
        return LiveCounter_U32;
    }

    public int getLastResult() {
        return LastResult_U32;
    }

    public void setTestStatus(boolean testStatus) {
        DaqStatus = testStatus;
    }

    public void setFaultStatus(boolean faultStatus) {
        FaultStatus = faultStatus;
    }

    public void setCurrent_Float(float current_Float) {
        Current_Float = current_Float;
    }

    public void setCapacityByCurrent_Float(float capacityByCurrent_Float) {
        CapacityByCurrent_Float = capacityByCurrent_Float;
    }

    public void setRTSOC_Float(float RTSOC_Float) {
        this.RTSOC_Float = RTSOC_Float;
    }

    public void setInitSOC_Float(float initSOC_Float) {
        InitSOC_Float = initSOC_Float;
    }

    public void setNominalCapacity_Float(float nominalCapacity_Float) {
        NominalCapacity_Float = nominalCapacity_Float;
    }

    public void setRTCapacityDiff(float RTCapacityDiff) {
        this.RTCapacityDiff_Float = RTCapacityDiff;
    }

    public void setDevBattaryPower(float devBattaryPower) {
        DevBattaryPower_Float = devBattaryPower;
    }

    public void setHardDiskCapacity(float hardDiskCapacity) {
        HardDiskCapacity_Float = hardDiskCapacity;
    }

    public void setTesTime(float tesTime) {
        TesTime_Float = tesTime;
    }

    public void setLiveCounter(int liveCounter) {
        LiveCounter_U32 = liveCounter;
    }

    public void setLastResult(int lastResult) {
        LastResult_U32 = lastResult;
    }

    private boolean DaqStatus;
    private boolean FaultStatus;


    public boolean isCANCardStatus() {
        return CANCardStatus;
    }

    public boolean isBattaryStatus() {
        return BattaryStatus;
    }

    public boolean isChargeOrNot() {
        return ChargeOrNot;
    }

    public boolean isCfgFileMissedOrNot() {
        return CfgFileMissedOrNot;
    }

    public void setCANCardStatus(boolean CANCardStatus) {
        this.CANCardStatus = CANCardStatus;
    }

    public void setBattaryStatus(boolean battaryStatus) {
        BattaryStatus = battaryStatus;
    }

    public void setChargeOrNot(boolean chargeOrNot) {
        ChargeOrNot = chargeOrNot;
    }

    public void setCfgFileMissedOrNot(boolean cfgFileMissedOrNot) {
        CfgFileMissedOrNot = cfgFileMissedOrNot;
    }

    private boolean CANCardStatus;
    private boolean BattaryStatus;
    private boolean ChargeOrNot;
    private boolean CfgFileMissedOrNot;
    private float Current_Float;
    private float CapacityByCurrent_Float;
    private float RTSOC_Float;
    private float InitSOC_Float;
    private float NominalCapacity_Float;
    private float RTCapacityDiff_Float;
    private float DevBattaryPower_Float;
    private float HardDiskCapacity_Float;
    private float TesTime_Float;
    private int LiveCounter_U32;
    private int LastResult_U32;
}
