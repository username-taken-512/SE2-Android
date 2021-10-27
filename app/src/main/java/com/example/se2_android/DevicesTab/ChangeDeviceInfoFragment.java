package com.example.se2_android.DevicesTab;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.example.se2_android.R;

import java.util.ArrayList;

public class ChangeDeviceInfoFragment extends Fragment {
    View view;
    EditText deviceID, deviceName;
    AutoCompleteTextView deviceType;
    Button delete, update;
    int devID;
    String devType, devName;

    private final static String[] DEVICETYPES = new String[]{
            "Lamp", "Alarm",
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_change_device_info, container, false);

        deviceID = view.findViewById(R.id.deviceID);
        deviceName = view.findViewById(R.id.deviceName);

        deviceType = view.findViewById(R.id.deviceType);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, DEVICETYPES);
        deviceType.setAdapter(adapter);

        delete = view.findViewById(R.id.deleteDevice);
        update = view.findViewById(R.id.updateButton);

        getData();
        setData();

        deviceType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceType.showDropDown();
            }
        }) ;

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Uppdatera i databas med hjälp av server.
                // Kolla vilka edit fields som inte är tomma, tomma edit ska ha samma värde som innan.
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: tabort i databas med hjälp av server.
            }
        });





        return view;
    }


    public void getData(){
        if (getArguments() != null){
            devID =getArguments().getInt("devID");
            devType = getArguments().getString("devType");
            devName = getArguments().getString("devName");
        }

    }

    public void setData(){
        deviceID.setHint(String.valueOf(devID));
        deviceName.setHint(devName);
        deviceType.setHint(devType);
    }
}