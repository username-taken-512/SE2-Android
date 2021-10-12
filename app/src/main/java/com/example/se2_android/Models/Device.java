package com.example.se2_android.Models;

public class Device {
    int deviceId;
    boolean on;
    String deviceType;
    String deviceName;

    public Device() {
    }

    public Device(int deviceId, boolean on, String deviceType, String deviceName) {
        this.deviceId = deviceId;
        this.on = on;
        this.deviceType = deviceType;
        this.deviceName = deviceName;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
