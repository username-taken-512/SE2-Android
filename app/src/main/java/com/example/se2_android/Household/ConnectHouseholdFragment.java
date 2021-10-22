package com.example.se2_android.Household;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.se2_android.R;
import com.example.se2_android.Stubs.HouseholdStub;

public class ConnectHouseholdFragment extends Fragment {

    View view;
    Button createHouseButton, joinHouseButton;

    HouseholdStub householdStub;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_connect_household, container, false);

        householdStub = HouseholdStub.getInstance();

        createHouseButton = view.findViewById(R.id.createHouseButton);
        joinHouseButton = view.findViewById(R.id.joinHouseButton);

        createHouseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_connectHouseholdFragment_to_createHouseholdFragment);
            }
        });

        joinHouseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }


}