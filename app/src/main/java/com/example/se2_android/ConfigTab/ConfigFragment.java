package com.example.se2_android.ConfigTab;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.example.se2_android.Stubs.LoginStub;

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
        LoginStub loginStub = LoginStub.getInstance();

        changeHouseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Joina nytt household

                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

                dialog.setTitle("Join new Household.");
                dialog.setMessage("Are you sure you want to join a new Household?");

                dialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                dialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Navigera till join fragment klassen.
                        Navigation.findNavController(view).navigate(R.id.action_configFragment_to_connectHouseholdFragment);
                    }
                });

                dialog.show();

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
                loginStub.setLoggedIn(false);
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
        LoginStub loginStub = LoginStub.getInstance();
        if (!loginStub.isLoggedIn()) {
            Navigation.findNavController(view).navigate(R.id.action_configFragment_to_loginFragment);
        }
    }
}