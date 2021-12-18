package com.example.se2_android.Utils;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.se2_android.Models.Device;
import com.example.se2_android.Models.Household;
import com.example.se2_android.Models.User;

import java.util.ArrayList;

import okhttp3.WebSocket;

public class WebsocketViewModel extends ViewModel {
    private WebSocket websocket;
    private boolean websocketConnected;
    private int householdId;
    private ArrayList<Device> deviceList = new ArrayList<>();
    private MutableLiveData<Integer> deviceListVersion;
    private MutableLiveData<Integer> connectionStatus;
    private MutableLiveData<Integer> connectionStatusAuth;

    public int getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(int householdId) {
        this.householdId = householdId;
    }

    public WebSocket getWebsocket() {
        return websocket;
    }

    public void setWebsocket(WebSocket websocket) {
        this.websocket = websocket;
    }

    public void addToDeviceList(Device device) {
        deviceList.add(device);
    }

    public void resetDeviceList() {
        deviceList.clear();
    }

    public ArrayList<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(ArrayList<Device> deviceList) {
        this.deviceList = deviceList;
    }

    public MutableLiveData<Integer> getDeviceListVersion() {
        if (deviceListVersion == null) {
            deviceListVersion = new MutableLiveData<>();
            deviceListVersion.setValue(0);
            Log.i("VIEWMODEL", "getDevListVersion -> if");
        }
        return deviceListVersion;
    }

    public void addDeviceListVersion() {
        Log.i("VIEWMODEL", "addDeviceListVersion");
        if (deviceListVersion != null) {
            Log.i("VIEWMODEL", "addDeviceListVersion if 2");
            deviceListVersion.setValue(deviceListVersion.getValue() + 1);
            if (deviceListVersion.getValue() == 32000) {
                deviceListVersion.setValue(0);
            }
        }
    }

    public boolean isWebsocketConnected() {
        return websocketConnected;
    }

    public void setWebsocketConnected(boolean websocketConnected) {
        this.websocketConnected = websocketConnected;
    }

    //House websocket
    public void setConnectionStatus(int status) {
        if (connectionStatus == null) {
            connectionStatus = new MutableLiveData<>();
        }
        Log.i("VIEWMODEL", "connectionStatus set: " + status);
        connectionStatus.setValue(status);
    }

    public MutableLiveData<Integer> getConnectionStatus() {
        if (connectionStatus == null) {
            connectionStatus = new MutableLiveData<>();
            connectionStatus.setValue(0);
        }
        Log.i("VIEWMODEL", "connectionStatus get");
        return connectionStatus;
    }

    //Houseauth websocket
    public void setConnectionStatusAuth(int status) {
        if (connectionStatusAuth == null) {
            connectionStatusAuth = new MutableLiveData<>();
        }
        Log.i("VIEWMODEL", "connectionStatusAuth set: " + status);
        connectionStatusAuth.postValue(status);
    }

    public MutableLiveData<Integer> getConnectionStatusAuth() {
        if (connectionStatusAuth == null) {
            connectionStatusAuth = new MutableLiveData<>();
            connectionStatusAuth.postValue(0);
        }
        Log.i("VIEWMODEL", "connectionStatusAuth get");
        return connectionStatusAuth;
    }
}
