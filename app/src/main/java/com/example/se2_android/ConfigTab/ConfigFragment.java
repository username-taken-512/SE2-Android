package com.example.se2_android.ConfigTab;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.se2_android.MainActivity;
import com.example.se2_android.Models.Household;
import com.example.se2_android.R;
import com.example.se2_android.Utils.Constant;


public class ConfigFragment extends Fragment {
    View view;
    Context context;
    ImageView changeHouseImage, editDevicesImage, notificationImage, logoutImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_config, container, false);
        context = getActivity();
        changeHouseImage = view.findViewById(R.id.changeHouse);
        editDevicesImage = view.findViewById(R.id.editDevice);
        notificationImage = view.findViewById(R.id.notification);
        logoutImage = view.findViewById(R.id.logoutConfig);


        changeHouseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeHouseholdDialog();
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
                logout();
            }
        });

        return view;
    }

    private void logout() {
        ((MainActivity) context).disconnectWebsocket();
        ((MainActivity) context).clearToken();
        Navigation.findNavController(view).navigate(R.id.action_configFragment_to_loginFragment);
    }

    private void changeHouseholdDialog() {
        //
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Join existing or create new Household?");
        dialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createHouseholdDialog();
            }
        });
        dialog.setNegativeButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                joinHouseholdDialog();
            }
        });
        dialog.show();

    }

    private void joinHouseholdDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Join existing Houshold");

        EditText enterId = new EditText(getContext());
        enterId.setInputType(InputType.TYPE_CLASS_NUMBER);
        enterId.setMaxLines(1);
        enterId.setHint("Enter Household id: ");
        dialog.setView(enterId);

        dialog.setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int id = Integer.parseInt(enterId.getText().toString());
                ((MainActivity) context).changeHousehold(id);
            }
        });

        dialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void createHouseholdDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Create new Houshold");

        EditText enterId = new EditText(getContext());
        enterId.setInputType(InputType.TYPE_CLASS_TEXT);
        enterId.setMaxLines(1);
        enterId.setHint("Enter Household name: ");
        dialog.setView(enterId);

        dialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Household household = new Household();
                household.setName(enterId.getText().toString().trim());
                ((MainActivity) context).sendNewHousehold(household);
            }
        });

        dialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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