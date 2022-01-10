package com.example.se2_android.ConfigTab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.se2_android.MainActivity;
import com.example.se2_android.Models.Device;
import com.example.se2_android.R;
import com.example.se2_android.Utils.Constant;
import com.example.se2_android.Utils.WebsocketViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChangeDeviceInfoFragment extends Fragment {
    private static WebsocketViewModel websocketViewModel;
    View view;
    EditText deviceId, deviceName;
    AutoCompleteTextView deviceType;
    FloatingActionButton backButton;
    Button delete, update;
    int devID;
    String devType, devName;

    private final static String[] DEVICETYPES = new String[]{
            "lamp", "element", "alarm", "thermometer", "fan", "autotoggle", "autosettings", "powersensor"
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_change_device_info, container, false);
        websocketViewModel = new ViewModelProvider(getActivity()).get(WebsocketViewModel.class);

        deviceId = view.findViewById(R.id.deviceID);
        deviceName = view.findViewById(R.id.deviceName);
        backButton = view.findViewById(R.id.backButtonEditList);
        deviceType = view.findViewById(R.id.deviceType);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, DEVICETYPES);
        deviceType.setAdapter(adapter);

        delete = view.findViewById(R.id.deleteDevice);
        update = view.findViewById(R.id.addButton);

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
                Navigation.findNavController(view).navigate(R.id.action_changeDeviceInfoFragment_to_editDeviceFragment);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDevice(false);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDevice(true);
            }
        });
        return view;
    }

    private void sendDevice(boolean delete) {
        boolean check = false;
        if (TextUtils.isEmpty(deviceName.getText().toString().trim())) {
            deviceName.setError("Please input a device name");
            check = true;
        }

        if (check) {
            return;
        }

        // Send device object
        Device device = new Device();
        device.setDeviceId(devID);
        if (delete) {
            device.setName(devName);
            device.setType(devType);
            device.setHouseholdId(0);
        } else {
            device.setName(deviceName.getText().toString().trim());
            if (TextUtils.isEmpty(deviceType.getText().toString().trim())) {
                device.setType(devType);
            } else {
                device.setType(deviceType.getText().toString().trim());
            }
            device.setHouseholdId(websocketViewModel.getHouseholdId());
        }

        ((MainActivity) getActivity()).changeDevice(device, Constant.OPCODE_CHANGE_DEVICE_INFO);
        Toast.makeText(getActivity(), "Sent message to server", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(view).navigate(R.id.action_changeDeviceInfoFragment_to_editDeviceFragment);
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
        deviceName.setText(devName);
        deviceType.setHint(devType);
    }
}