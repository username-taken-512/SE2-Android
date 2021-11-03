package com.example.se2_android.Models;

public class Device {
    int deviceId;
    String name;
    String type;
    int value;
    int householdId;

    public Device() {
    }

    public Device(int deviceId, String name, String type, int value, int householdId) {
        this.deviceId = deviceId;
        this.name = name;
        this.type = type;
        this.value = value;
        this.householdId = householdId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(int householdId) {
        this.householdId = householdId;
    }
}
