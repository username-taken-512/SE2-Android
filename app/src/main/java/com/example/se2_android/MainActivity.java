package com.example.se2_android;

import org.jetbrains.annotations.NotNull;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.se2_android.Models.Device;
import com.example.se2_android.Models.Household;
import com.example.se2_android.Models.User;
import com.example.se2_android.Utils.Constant;
import com.example.se2_android.Utils.WebsocketViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class MainActivity extends AppCompatActivity {
    private static final int OPCODE_STATUS_DEVICE = 10;
    private static final int OPCODE_STATUS_ALL_DEVICES = 11;
    private static final int OPCODE_CHANGE_DEVICE_STATUS = 20;
    private static final int OPCODE_CHANGE_DEVICE_HOUSEHOLD = 21;
    private static final int OPCODE_CHANGE_DEVICE_NOTOK = 40;
    private static final int OPCODE_INVALID_TOKEN = 49;

    private static final String TAG = "MainActivity";

    private static WebSocket webSocket;
    //Mats IP: 85.197.159.131, Mats websocket port: 1337
    private static String SERVER_PATH = "ws://192.168.0.100:7071/house?token=";
//    private static String SERVER_PATH = "ws://85.197.159.150:1337/house?token=";

    private static Gson gson;
    private WebsocketViewModel websocketViewModel;
    private SharedPreferences sharedPref;

    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = this.getSharedPreferences("settings", Context.MODE_PRIVATE);

        //Set up bottom Nav & Nav controller
        BottomNavigationView bottomNavigationView;
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.navHostFragment);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        websocketViewModel = new ViewModelProvider(this).get(WebsocketViewModel.class);
        gson = new Gson();
//        connectWebsocket("token=123");
        ComponentActivity ca = this;
    }

    // --- Websocket ---
    public void connectWebsocket(String token) {
        Log.i(TAG, "connectWebSocket method start");
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(SERVER_PATH + token).build();

        if (websocketViewModel.getWebsocket() == null) {
            Log.i(TAG, "connectWebSocket viewmodel null");
            webSocket = client.newWebSocket(request, new SocketListener());
            websocketViewModel.setWebsocket(webSocket);
        } else {
            Log.i(TAG, "connectWebSocket viewmodel not null");
            webSocket = websocketViewModel.getWebsocket();
        }


        Log.i(TAG, "connectWebSocket method end");
    }

    public void disconnectWebsocket() {
        if ((webSocket = websocketViewModel.getWebsocket()) != null) {
            webSocket.close(1000, "logout");
            websocketViewModel.setWebsocket(null);
            Log.i(TAG, "disconnectWebsocket");
        }
    }

    private class SocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);

            Log.i(TAG, "Connection opened ");
            runOnUiThread(() -> {
                websocketViewModel.setWebsocketConnected(true);
            });
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            Log.i(TAG, "onMessage method start");
            Log.i(TAG, webSocket.request().toString());

            JSONObject jsonObject = null;
            JSONArray jsonArray = null;
            int opcode;

            try {
                //Incoming message is json
                jsonObject = new JSONObject(text);

                //Get opcode to determine action
                opcode = jsonObject.getInt("opcode");
                Log.i(TAG, "onMessage method jsonObject: " + jsonObject.toString());
                switch (opcode) {
                    case OPCODE_STATUS_ALL_DEVICES:
                        statusAllDevices(jsonArray, jsonObject);
                        break;
                    case OPCODE_STATUS_DEVICE:
                        statusDevice(jsonObject);
                        break;
                    case OPCODE_CHANGE_DEVICE_NOTOK:
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Could not connect to device", Toast.LENGTH_SHORT).show();
                        });
                        statusDevice(jsonObject);
                        break;
                    case OPCODE_INVALID_TOKEN:
                        runOnUiThread(() -> {
                            clearToken();
                            websocketViewModel.setConnectionStatus(3);
                        });
                        break;
                    case Constant.OPCODE_CREATE_HOUSEHOLD_OK:
                        householdCreated(jsonObject, true);
                        break;
                    case Constant.OPCODE_CREATE_HOUSEHOLD_NOTOK:
                        householdCreated(jsonObject, false);
                        break;
                    case Constant.OPCODE_USER_CHANGE_HOUSEHOLD_NOTOK:
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Could not join household", Toast.LENGTH_SHORT).show();
                        });
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "onMessage END");
        }

        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosed(webSocket, code, reason);
            Log.i(TAG, "onClosed" + webSocket.request());
            Log.i(TAG, "onClosed " + code + "|" + reason);

            runOnUiThread(() -> {
                websocketViewModel.setWebsocketConnected(false);
            });
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosing(webSocket, code, reason);
            Log.i(TAG, "onClosing " + webSocket.request());
            Log.i(TAG, "onClosing " + code + "|" + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);

            System.out.println("onFailure " + t.getMessage());

            runOnUiThread(() -> {
                websocketViewModel.setWebsocketConnected(false);
            });
//            Log.i(TAG, "onFailure - Trying to reconnect... " + websocketViewModel.isWebsocketConnected());
            //TODO: Reconnect feature - connectWebsocket();
        }
    }

    public void clearToken() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", "");
        editor.commit();
    }

    // --- Websocket incoming message methods ---
    public void statusAllDevices(JSONArray jsonArray, JSONObject jsonObject) throws JSONException {
        websocketViewModel.resetDeviceList();
        Log.i(TAG, "onMessage SEND ALL DEVICES");
        jsonArray = jsonObject.getJSONArray("data");
        Household household = gson.fromJson(String.valueOf(jsonObject.get("household")), Household.class);
        websocketViewModel.setHouseholdId(household.getHouseholdId());
        websocketViewModel.setHouseholdName(household.getName());
        websocketViewModel.setNameOfUser(jsonObject.getString("nameOfUser"));

        for (int i = 0; i < jsonArray.length(); i++) {
            Log.i(TAG, "onMessage for loop: " + jsonArray.getString(i));
            Device device = gson.fromJson(jsonArray.getString(i), Device.class);

            websocketViewModel.addToDeviceList(gson.fromJson(jsonArray.getString(i), Device.class));
            Log.i(TAG, "onMessage for-loop list size: " + websocketViewModel.getDeviceList().size());
        }
        Log.i(TAG, "onMessage for-loop left loop");
        runOnUiThread(() -> {
            websocketViewModel.addDeviceListVersion();
        });
    }

    public void statusDevice(JSONObject jsonObject) throws JSONException {
        Log.i(TAG, "onMessage SEND ONE DEVICE");
        Device device = gson.fromJson(jsonObject.getString("data"), Device.class);

        ArrayList<Device> currentList = websocketViewModel.getDeviceList();

        boolean existingDevice = false;
        for (Device d : currentList) {
            if (device.getDeviceId() == d.getDeviceId()) {
                if (d.getHouseholdId() != device.getHouseholdId()) {
                    currentList.remove(d);
                }
                d.setValue(device.getValue());
                d.setType(device.getType());
                d.setName(device.getName());
                d.setTimer(device.getTimer());
                existingDevice = true;

                //Notify if alarm
                if (d.getType().equals("alarm") && d.getValue() == 2) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, d.getName() + " triggered!", Toast.LENGTH_SHORT).show();
                    });
                }
                break;
            }
        }

        if (!existingDevice) {
            currentList.add(device);
        }
        runOnUiThread(() -> {
            Log.i(TAG, "onMessage VM set deviceList");
            websocketViewModel.setDeviceList(currentList);
            Log.i(TAG, "onMessage VM addVersion");
            websocketViewModel.addDeviceListVersion();
        });
    }

    private void householdCreated(JSONObject jsonObject, boolean responseOk) {
        if (responseOk) {
            try {
                Household household = gson.fromJson(jsonObject.getString("data"), Household.class);
                changeHousehold(household.getHouseholdId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            runOnUiThread(() -> {
                Toast.makeText(this, "Could not create household", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void changedHousehold(JSONObject jsonObject) {

    }


    // --- Websocket outgoing message methods ---

    //Called from DevicesFragment
    public void changeDevice(Device device, int opcode) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("opcode", opcode);
            jsonObject.put("data", gson.toJson(device));
            System.out.println("Sending: " + jsonObject);
            webSocket.send(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Called from ConfigFragment
    public void sendNewHousehold(Household household) {
        Log.i(TAG, "NewHousehold: " + household.getName());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("opcode", Constant.OPCODE_CREATE_HOUSEHOLD);
            jsonObject.put("data", gson.toJson(household));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "NewHousehold: Sending: " + jsonObject);
        webSocket.send(jsonObject.toString());
    }

    public void changeHousehold(int newId) {
        //Clear devicelist
        websocketViewModel.resetDeviceList();

        //Send household
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("opcode", Constant.OPCODE_USER_CHANGE_HOUSEHOLD);
            jsonObject.put("data", newId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "changeHousehold: Sending: " + jsonObject);
        webSocket.send(jsonObject.toString());
    }
}