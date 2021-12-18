package com.example.se2_android.ConfigTab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.se2_android.R;


public class ConfigFragment extends Fragment {
    View view;
    ImageView changeHouseImage, editDevicesImage, notificationImage, logoutImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_config, container, false);
        changeHouseImage = view.findViewById(R.id.changeHouse);
        editDevicesImage = view.findViewById(R.id.editDevice);
        notificationImage = view.findViewById(R.id.notification);
        logoutImage = view.findViewById(R.id.logoutConfig);


        changeHouseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Joina nytt household
            }
        });

        editDevicesImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_configFragment_to_editDeviceFragment);
            }
        });

        notificationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //???
            }
        });

        logoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Logga ut user och navigera till LOGIN
                Navigation.findNavController(view).navigate(R.id.action_configFragment_to_loginFragment);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO:
        //If not logged in, go to login, else skip navigation
//        LoginStub loginStub = LoginStub.getInstance();
//        if (!loginStub.isLoggedIn()) {
//            Navigation.findNavController(view).navigate(R.id.action_configFragment_to_loginFragment);
//        }

        begin();
    }

    private void begin() {
        //If sharedPref == null, go to login
        //Get token
        SharedPreferences sharedPref = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");

        //If no saved token, go to login
        if (token.equals("")) {
            Navigation.findNavController(view).navigate(R.id.action_configFragment_to_loginFragment);
        }
    }
}