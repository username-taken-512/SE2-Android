package com.example.se2_android;

import org.jetbrains.annotations.NotNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.se2_android.Models.Device;
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

    private static final String TAG = "MainActivity";

    private static WebSocket webSocket;
    //Mats IP: 85.197.159.131, Mats websocket port: 1337
    private static String SERVER_PATH = "ws://85.197.159.131:1337/house?token=123";
    private static Gson gson;
    private WebsocketViewModel websocketViewModel;

    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        connectWebsocket();
    }

    // --- Websocket ---
    private void connectWebsocket() {
        Log.i(TAG, "connectWebSocket method start");
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(SERVER_PATH).build();

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

    private class SocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);

            Log.i(TAG, "Connection opened");
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
                        Toast.makeText(MainActivity.this, "Could not connect to device", Toast.LENGTH_SHORT).show();
                        statusDevice(jsonObject);
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
            System.out.println(webSocket.request());
            System.out.println("closed " + code + "|" + reason);

            runOnUiThread(() -> {
                websocketViewModel.setWebsocketConnected(false);
            });
            Log.i(TAG, "onClosed - Trying to reconnect... " + websocketViewModel.isWebsocketConnected());
            connectWebsocket();
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosing(webSocket, code, reason);
            System.out.println(webSocket.request());
            System.out.println("closing " + code + "|" + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);

            System.out.println("onFailure " + t);

            runOnUiThread(() -> {
                websocketViewModel.setWebsocketConnected(false);
            });
            Log.i(TAG, "onFailure - Trying to reconnect... " + websocketViewModel.isWebsocketConnected());
            connectWebsocket();
        }
    }


    // --- Websocket incoming message methods ---
    public void statusAllDevices(JSONArray jsonArray, JSONObject jsonObject) throws JSONException {
        websocketViewModel.resetDeviceList();
        Log.i(TAG, "onMessage SEND ALL DEVICES");
        jsonArray = jsonObject.getJSONArray("data");

        for (int i = 0; i < jsonArray.length(); i++) {
            Log.i(TAG, "onMessage for loop: " + jsonArray.getString(i));

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

        for (Device d : currentList) {
            Log.i(TAG, "onMessage SEND ONE DEVICE FOR");
            if (device.getDeviceId() == d.getDeviceId()) {
                Log.i(TAG, "onMessage SEND ONE DEVICE IF");
                d.setValue(device.getValue());
                break;
            }
        }
        runOnUiThread(() -> {
            Log.i(TAG, "onMessage VM set deviceList");
            websocketViewModel.setDeviceList(currentList);
            Log.i(TAG, "onMessage VM addVersion");
            websocketViewModel.addDeviceListVersion();
        });
    }

    // --- Websocket outgoing message methods ---

    //Called from DevicesFragment
    public void changeDevice(Device device) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("opcode", OPCODE_CHANGE_DEVICE_STATUS);
            jsonObject.put("data", gson.toJson(device));
            System.out.println("Sending: " + jsonObject);
            webSocket.send(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}