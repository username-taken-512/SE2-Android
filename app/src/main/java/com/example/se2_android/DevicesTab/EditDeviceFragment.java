package com.example.se2_android.DevicesTab;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.se2_android.Models.Device;
import com.example.se2_android.R;
import com.example.se2_android.Stubs.LoginStub;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class EditDeviceFragment extends Fragment {

    RecyclerView recyclerView;
    View view;
    ArrayList<Device> deviceList = new ArrayList<>();
    FloatingActionButton floatingActionButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_device, container, false);

        recyclerView = view.findViewById(R.id.editDeviceView);
        floatingActionButton =view.findViewById(R.id.backButtonConfig);

        deviceList.add(new Device(0, true, "lamp", "Kitchen lamp"));
        deviceList.add(new Device(1, true, "lamp", "Bathroom lamp"));
        deviceList.add(new Device(2, false, "lamp", "Bedroom lamp"));
        EditDeviceAdapter editDeviceAdapter = new EditDeviceAdapter(deviceList);
        recyclerView.setAdapter(editDeviceAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToConfig();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //If not logged in, go to login, else skip navigation
        LoginStub loginStub = LoginStub.getInstance();
        if (!loginStub.isLoggedIn()) {
            Navigation.findNavController(view).navigate(R.id.action_configFragment_to_loginFragment);
        }
    }

    public void backToConfig(){
        Navigation.findNavController(view).navigate(R.id.action_editDeviceFragment_to_configFragment);
    }

}