package com.example.se2_android.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.se2_android.MainActivity;
import com.example.se2_android.Models.Device;
import com.example.se2_android.Models.Household;
import com.example.se2_android.Models.User;
import com.example.se2_android.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebsocketAuth {
    private static final String TAG = "WebsocketAuth";
    private static WebsocketViewModel websocketViewModel;

    private static final int OPCODE_STATUS_DEVICE = 10;
    private static final int OPCODE_STATUS_ALL_DEVICES = 11;
    private static final int OPCODE_CHANGE_DEVICE_STATUS = 20;
    private static final int OPCODE_CHANGE_DEVICE_HOUSEHOLD = 21;

    private static final int OPCODE_CREATE_USER = 23;
    private static final int OPCODE_CREATE_USER_OK = 13;

    private static final int OPCODE_NEW_TOKEN = 12;
    private static final int OPCODE_REQUEST_TOKEN = 22;
    private static final int OPCODE_NEW_TOKEN_NOTOK = 42;

    private static final int OPCODE_CREATE_HOUSEHOLD_OK = 14;
    private static final int OPCODE_CREATE_HOUSEHOLD = 24;

    private static final String AUTH_SERVER_PATH = "ws://192.168.0.100:7071/houseauth"; // Test IP
    //    private static final String AUTH_SERVER_PATH = "ws://85.197.159.131:1337/houseauth";     // Bogge IP
    private static WebSocket webSocket;
    private static Gson gson;
    private static SharedPreferences sharedPref;

    private static View view;
    private static FragmentActivity fragmentActivity;

    public WebsocketAuth(Context context, View view, FragmentActivity activity) {
        websocketViewModel = new ViewModelProvider(activity).get(WebsocketViewModel.class);
        fragmentActivity = activity;
        gson = new Gson();
        WebsocketAuth.view = view;
        sharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        Log.i(TAG, "Connecting to houseauth");
        connectauthWebsocket();
    }

    //Login with user, returns token
    public void login(User user) {
        Log.i(TAG, "login - user: " + user.getUsername());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("opcode", OPCODE_REQUEST_TOKEN);
            jsonObject.put("data", gson.toJson(user));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Sending: " + jsonObject);
        webSocket.send(jsonObject.toString());
    }

    public void sendNewHousehold(Household household) {
        Log.i(TAG, "NewHousehold: " + household.getName());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("opcode", OPCODE_CREATE_HOUSEHOLD);
            jsonObject.put("data", gson.toJson(household));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "NewHousehold: Sending: " + jsonObject);
        webSocket.send(jsonObject.toString());
    }

    public void sendNewUser(User user) {
        Log.i(TAG, "NewUser: " + user.getUsername());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("opcode", OPCODE_CREATE_USER);
            jsonObject.put("data", gson.toJson(user));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "NewUser: Sending: " + jsonObject);
        webSocket.send(jsonObject.toString());
    }

    public void closeConnection() {
        if (webSocket != null) {
            webSocket.close(1000, "finished");
        }
    }

    private static class SocketListenerAuth extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);

            Log.i(TAG, "autHouse: Socket Connection successful");
            websocketViewModel.setConnectionStatusAuth(1);
            Log.i(TAG, "autHouse: Socket Connection successful, after viewmodel set");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            Log.i(TAG, "###authHouse: onMessage - connected to: " + webSocket.request());
            Log.i(TAG, "authHouse: onMessage - Received message: " + text);
            try {
                JSONObject jsonObject = new JSONObject(text);
                int opcode = jsonObject.getInt("opcode");

                switch (opcode) {
                    case OPCODE_NEW_TOKEN_NOTOK:
                        Log.i(TAG, "authHouse: onMessage - No token created");
                        invalidCredentials();
                        break;
                    case OPCODE_NEW_TOKEN:
                        String token = (String) jsonObject.get("data");
                        Log.i(TAG, "authHose: New Token, disconnecting");
                        storeToken(token);
                        Log.i(TAG, "authHouse: onMessage - New token stored: " + token);
                        break;
                    case OPCODE_CREATE_USER_OK:
                        Log.i(TAG, "authHouse: onMessage - New user created: " + jsonObject);
                        createUserOk();
                        break;
                    case OPCODE_CREATE_HOUSEHOLD_OK:
                        createHouseholdOk(jsonObject);
                        break;
                    default:
                        Log.i(TAG, "authHouse: UKNOWN OPCODE: " + opcode);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            webSocket.close(0, "finished");
            Log.i(TAG, "---authHouse: onMessage END---");
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            websocketViewModel.setConnectionStatusAuth(0);
        }
    }
    private static void createHouseholdOk(JSONObject jsonObject) {
        try {
            Household household = gson.fromJson(jsonObject.getString("data"), Household.class);
            fragmentActivity.runOnUiThread(() -> {
                websocketViewModel.setHouseholdId(household.getHouseholdId());
                websocketViewModel.setConnectionStatusAuth(4);
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void createUserOk() {
        fragmentActivity.runOnUiThread(() -> {
            websocketViewModel.setConnectionStatusAuth(5);
        });
    }

    private static void invalidCredentials() {
        fragmentActivity.runOnUiThread(() -> {
            Toast.makeText(fragmentActivity, "Invalid credentials", Toast.LENGTH_SHORT).show();
        });
    }

    private static void storeToken(String token) {
        Log.i(TAG, "1 Stored token: " + token);
        fragmentActivity.runOnUiThread(() -> {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("token", token);
            editor.commit();
            SharedPreferences sharedPref = fragmentActivity.getSharedPreferences("settings", Context.MODE_PRIVATE);
            Log.i(TAG, "2 Stored token: " + sharedPref.getString("token", "none"));
            websocketViewModel.setConnectionStatusAuth(2);
            webSocket.close(1000, "finished");
        });
        Log.i(TAG, "3 Stored token: " + token);
    }

    private void connectauthWebsocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(AUTH_SERVER_PATH).build();
        webSocket = client.newWebSocket(request, new SocketListenerAuth());
    }
}
