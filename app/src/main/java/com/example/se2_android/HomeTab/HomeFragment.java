package com.example.se2_android.HomeTab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.se2_android.MainActivity;
import com.example.se2_android.Models.Device;
import com.example.se2_android.R;
import com.example.se2_android.Utils.Constant;
import com.example.se2_android.Utils.WebsocketViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private static final String SPEECH_LANGUAGE = "en-US";
    private Context context;
    private View view;
    private SharedPreferences sharedPref;
    private static WebsocketViewModel websocketViewModel;
    private Observer<Integer> connectionObserver, houseObserver;

    private TextView houseTextView, welcomeTextView, thermometerTextView, alarmTextView, powerTextView;
    private FloatingActionButton speechButton;
    private TextToSpeech textToSpeech;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        websocketViewModel = new ViewModelProvider(getActivity()).get(WebsocketViewModel.class);

        houseTextView = view.findViewById(R.id.houseTextView);
        thermometerTextView = view.findViewById(R.id.thermometerTextView);
        alarmTextView = view.findViewById(R.id.alarmsTextView);
        welcomeTextView = view.findViewById(R.id.welcomeTextView);
        speechButton = view.findViewById(R.id.voiceButton);
        powerTextView = view.findViewById(R.id.powerTextView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        begin();
        voiceIntent();
    }

    private void begin() {
        //If sharedPref == null, go to login
        //Get token
        sharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        welcomeTextView.setText("Welcome, " + token);

        //If no saved token, go to login
        if (token.equals("")) {
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_loginFragment);
        }
        Log.i(TAG, "Trying to connect with [" + token + "]");

        //Set up listener to manage connection
        connectionObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Log.i(TAG, "Livedata onChanged: " + integer);
                if (integer == 0) {         // Connection not open, should open
                    Log.i(TAG, "Livedata trying to connect if ws is null: " + integer);
                    ((MainActivity) context).connectWebsocket(token);
                } else if (integer == 1) {  // Connected

                } else if (integer == 3) {  // Token invalid. Clear token and return to login
                    Log.i(TAG, "Livedata clearing token and going login: " + integer);
                    clearToken();
                    Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_loginFragment);
                }
            }
        };
        websocketViewModel.getConnectionStatus().observe(getActivity(), connectionObserver);

        //Set up listener to fill textViews
        houseObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

                Boolean allAlarmsOn = true;
                String alarmText = "No alarms in the house";
                String thermometerText = "";
                String houseText = websocketViewModel.getHouseholdName() +
                        "\nid: " + websocketViewModel.getHouseholdId();
                String powerText = "";
                for (Device d : websocketViewModel.getDeviceList()) {
                    if (d.getType().equals("thermometer")) {
                        double temp = ((double) d.getValue() / 10);
                        thermometerText += d.getName() + ": " + temp + " C\n";
                    }
                    if (d.getType().equals("alarm")) {
                        if (d.getValue() == 1 && allAlarmsOn) {
                            alarmText = "All alarms are ON";
                        } else if (d.getValue() == 0) {
                            allAlarmsOn = false;
                            alarmText = "All alarms are NOT on";
                        }
                    }
                    if (d.getType().equals("powersensor")) {
                        double power = ((double) d.getValue()) / 10;
                        powerText = power + " W";
                    }
                }
                houseTextView.setText(houseText);
                thermometerTextView.setText(thermometerText);
                alarmTextView.setText(alarmText);
                powerTextView.setText(powerText);
                welcomeTextView.setText("Welcome, " + websocketViewModel.getNameOfUser());
            }
        };
        websocketViewModel.getDeviceListVersion().observe(getActivity(), houseObserver);
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        websocketViewModel.getConnectionStatus().removeObserver(connectionObserver);
        websocketViewModel.getDeviceListVersion().removeObserver(houseObserver);
        super.onDestroy();
    }

    private void getToken() {

    }

    private void clearToken() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", "");
        editor.commit();
    }

    // --- Speech to Device ---
    private void voiceIntent() {
        textToSpeech = new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.i(TAG, "tts init " + status);
                if (status != TextToSpeech.ERROR) {
                    Log.i(TAG, "tts init no error");
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {

                            // There are no request codes
                            Intent data = result.getData();
                            ArrayList<String> voiceText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                            Log.i(TAG, "Voice collected: " + voiceText.get(0));
                            for (String s : voiceText) {
                                Log.i(TAG, "-Voice collected: " + s);
                            }
                            talkToDevice(voiceText.get(0));

                        }
                    }
                });

        speechButton.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, SPEECH_LANGUAGE);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say 'help' for instructions");
            someActivityResultLauncher.launch(intent);
        });
    }

    private void talkToDevice(String string) {
        String[] splitString = string.split(" ");
        ArrayList<String> splitArray = new ArrayList<>(Arrays.asList(splitString));
        String command = "";
        String devicename = "";
        String message = "";

        if (string.startsWith("help")) {
            message = "Say 'show', 'start' or 'stop' followed by a device name";
            sayMessage(message);
            return;
        }

        if (splitArray.size() < 2) {
            message = "Sorry, I didn't understand";
        }
        command = splitArray.get(0);
        splitArray.remove(0);

        // All words after command -> device name
        for (String s : splitArray) {
            devicename += s + " ";
        }
        devicename = devicename.trim();

        Device device = null;
        for (Device d : websocketViewModel.getDeviceList()) {
            if (d.getName().equalsIgnoreCase(devicename)) {
                Log.i(TAG, "TTS: Found device ");
                device = d;
                break;
            }
        }
        if (device == null) {
            message = "Sorry, could not find " + devicename;
            sayMessage(message);
            return;
        }

        if (command.equalsIgnoreCase("show")) {
            switch (device.getType()) {
                case "lamp":
                case "autotoggle":
                case "element":
                    message = devicename + " is " + (device.getValue() == 0 ? " OFF" : " ON");
                    break;
                case "alarm":
                    switch (device.getValue()) {
                        case 0:
                            message = devicename + " is disarmed";
                            break;
                        case 1:
                            message = devicename + " is armed";
                            break;
                        case 2:
                            message = devicename + " is armed and triggered";
                            break;
                    }
                    break;
                case "powersensor":
                case "thermometer":
                    double temp = device.getValue();
                    message = devicename + " is at " + temp / 10;
                    break;
                default:
                    message = devicename + " is at " + device.getValue();
                    break;
            }
        }

        if (command.equalsIgnoreCase("start")) {
            switch (device.getType()) {
                case "lamp":
                case "autotoggle":
                case "element":
                    device.setValue(1);
                    break;
                case "alarm":
                    if (device.getValue() == 2) {
                        message = devicename + " is already armed and triggered";
                        sayMessage(message);
                        return;
                    }
                    device.setValue(1);
                    break;
                case "fan":
                    device.setValue(100);
                    break;
            }

            message = "Turning on " + devicename;
            ((MainActivity) context).changeDevice(device, Constant.OPCODE_CHANGE_DEVICE_STATUS);
            Log.i(TAG, "Devicename: " + devicename);
        }

        if (command.equalsIgnoreCase("stop")) {
            switch (device.getType()) {
                case "lamp":
                case "alarm":
                case "autotoggle":
                case "element":
                    // Turn off device
                    device.setValue(0);
                    message = "Turning off " + devicename;
                    ((MainActivity) context).changeDevice(device, Constant.OPCODE_CHANGE_DEVICE_STATUS);
                    Log.i(TAG, "Devicename: " + devicename);
                    break;
            }
//            if (device.getType().equalsIgnoreCase("lamp") ||
//                    device.getType().equalsIgnoreCase("element") ||
//                    device.getType().equalsIgnoreCase("fan") ||
//                    device.getType().equalsIgnoreCase("autotoggle")) {
//                // Turn off device
//                device.setValue(0);
//                message = "Turning off " + devicename;
//                ((MainActivity) context).changeDevice(device, Constant.OPCODE_CHANGE_DEVICE_STATUS);
//                Log.i(TAG, "Devicename: " + devicename);
//            }
        }

        sayMessage(message);
    }

    private void sayMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onPause();
    }
}
