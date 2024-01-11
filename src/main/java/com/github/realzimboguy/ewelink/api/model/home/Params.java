
package com.github.realzimboguy.ewelink.api.model.home;

import java.util.List;

import com.github.realzimboguy.ewelink.api.model.DeviceStatus;
import com.google.gson.annotations.SerializedName;


public class Params {
    private BindInfos bindInfos;
    private Integer version;
    private String sledOnline;
    private String fwVersion;
    private String staMac;
    private Integer rssi;
    private Integer init;
    private String alarmType;
    private List<Integer> alarmVValue;
    private List<Integer> alarmCValue;
    private List<Integer> alarmPValue;
    @SerializedName("switch")
    private String _switch;
    private List<OutletSwitch> switches;
    private String startup;
    private String pulse;
    private Integer pulseWidth;
    private String power;
    private String voltage;
    private String current;
    private String oneKwh;
    private Integer uiActive;
    private Integer timeZone;
    private OnlyDevice onlyDevice;
    private String ssid;
    private String bssid;
    private List<Timer> timers;
    private String hundredDaysKwh;
    private Long demNextFetchTime;
    private Integer rstReason;
    private Integer exccause;
    private Integer epc1;
    private Integer epc2;
    private Integer epc3;
    private Integer excvaddr;
    private Integer depc;
    private P2pinfo p2pinfo;
    private Batteryinfo batteryinfo;
    private Record record;
    private String endTime;
    private String startTime;
    public BindInfos getBindInfos() {
        return bindInfos;
    }
    public Integer getVersion() {
        return version;
    }
    public String getSledOnline() {
        return sledOnline;
    }
    public String getFwVersion() {
        return fwVersion;
    }
    public String getStaMac() {
        return staMac;
    }
    public Integer getRssi() {
        return rssi;
    }
    public Integer getInit() {
        return init;
    }
    public String getAlarmType() {
        return alarmType;
    }
    public List<Integer> getAlarmVValue() {
        return alarmVValue;
    }
    public List<Integer> getAlarmCValue() {
        return alarmCValue;
    }
    public List<Integer> getAlarmPValue() {
        return alarmPValue;
    }
    public DeviceStatus getDeviceStatus() {
        return DeviceStatus.fromString(_switch);
    }
    public void setSwitch(String _switch) {
        this._switch = _switch;
    }
    public List<OutletSwitch> getSwitches() {
        return switches;
    }
    public void setSwitches(List<OutletSwitch> switches) {
        this.switches = switches;
    }
    public String getStartup() {
        return startup;
    }
    public String getPulse() {
        return pulse;
    }
    public Integer getPulseWidth() {
        return pulseWidth;
    }
    public String getPower() {
        return power;
    }
    public String getVoltage() {
        return voltage;
    }
    public String getCurrent() {
        return current;
    }
    public String getOneKwh() {
        return oneKwh;
    }
    public Integer getUiActive() {
        return uiActive;
    }
    public Integer getTimeZone() {
        return timeZone;
    }
    public OnlyDevice getOnlyDevice() {
        return onlyDevice;
    }
    public String getSsid() {
        return ssid;
    }
    public String getBssid() {
        return bssid;
    }
    public List<Timer> getTimers() {
        return timers;
    }
    public String getHundredDaysKwh() {
        return hundredDaysKwh;
    }
    public Long getDemNextFetchTime() {
        return demNextFetchTime;
    }
    public Integer getRstReason() {
        return rstReason;
    }
    public Integer getExccause() {
        return exccause;
    }
    public Integer getEpc1() {
        return epc1;
    }
    public Integer getEpc2() {
        return epc2;
    }
    public Integer getEpc3() {
        return epc3;
    }
    public Integer getExcvaddr() {
        return excvaddr;
    }
    public Integer getDepc() {
        return depc;
    }
    public P2pinfo getP2pinfo() {
        return p2pinfo;
    }
    public Batteryinfo getBatteryinfo() {
        return batteryinfo;
    }
    public Record getRecord() {
        return record;
    }
    public String getEndTime() {
        return endTime;
    }
    public String getStartTime() {
        return startTime;
    }
}
