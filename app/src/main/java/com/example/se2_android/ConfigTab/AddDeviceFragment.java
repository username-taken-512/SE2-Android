package com.example.se2_android.ConfigTab;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.se2_android.MainActivity;
import com.example.se2_android.Models.Device;
import com.example.se2_android.R;
import com.example.se2_android.Utils.Constant;
import com.example.se2_android.Utils.WebsocketViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddDeviceFragment extends Fragment {
    private static WebsocketViewModel websocketViewModel;
    View view;
    EditText deviceId, deviceName;
    AutoCompleteTextView deviceType;
    FloatingActionButton backButton;
    Button addButton;
    int devID;
    String devType, devName;

    private final static String[] DEVICETYPES = new String[]{
            "lamp", "element", "timer", "alarm"
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_device, container, false);
        websocketViewModel = new ViewModelProvider(getActivity()).get(WebsocketViewModel.class);

        deviceId = view.findViewById(R.id.deviceID);
        deviceName = view.findViewById(R.id.deviceName);
        backButton = view.findViewById(R.id.backButtonEditList);
        deviceType = view.findViewById(R.id.deviceType);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, DEVICETYPES);
        deviceType.setAdapter(adapter);

        addButton = view.findViewById(R.id.addButton);

        getData();
        setData();

        deviceType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceType.showDropDown();
            }
        });

        deviceType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                deviceType.showDropDown();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_addDeviceFragment_to_editDeviceFragment);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDevice();
            }
        });


        return view;
    }

    private void sendDevice() {
        boolean check = false;
        if (TextUtils.isEmpty(deviceId.getText().toString().trim())) {
            deviceId.setError("Please input a number");
            check = true;
        }

        if (TextUtils.isEmpty(deviceName.getText().toString().trim())) {
            deviceName.setError("Please input a device name");
            check = true;
        }

        if (TextUtils.isEmpty(deviceType.getText().toString().trim())) {
            deviceType.setError("Please select a device type");
            check = true;
        }

        if (check) {
            return;
        }

        // Send device object
        Device device = new Device();
        device.setDeviceId(Integer.parseInt(deviceId.getText().toString().trim()));
        device.setName(deviceName.getText().toString().trim());
        device.setType(deviceType.getText().toString());
        device.setHouseholdId(websocketViewModel.getHouseholdId());

        ((MainActivity) getActivity()).changeDevice(device, Constant.OPCODE_CHANGE_DEVICE_INFO);
        Toast.makeText(getActivity(), "Sent message to server", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(view).navigate(R.id.action_addDeviceFragment_to_editDeviceFragment);
    }

    public void getData() {
        if (getArguments() != null) {
            devID = getArguments().getInt("devID");
            devType = getArguments().getString("devType");
            devName = getArguments().getString("devName");
        }

    }

    public void setData() {
        deviceId.setHint(String.valueOf(devID));
        deviceName.setHint(devName);
        deviceType.setHint(devType);
    }
}