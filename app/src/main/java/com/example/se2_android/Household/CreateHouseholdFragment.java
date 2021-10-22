package com.example.se2_android.Household;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.se2_android.R;
import com.example.se2_android.Stubs.HouseholdStub;

public class CreateHouseholdFragment extends Fragment {

    View view;

    Button createHouseButton;
    EditText householdName, householdID;

    HouseholdStub householdStub;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_household, container, false);

        householdStub = HouseholdStub.getInstance();

        createHouseButton = view.findViewById(R.id.createHouse2Button);
        householdName = view.findViewById(R.id.householdName);
        householdID = view.findViewById(R.id.householdID);

        createHouseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = householdName.getText().toString();
                String id = householdID.getText().toString();
                householdStub.setHouseholdName(name);
                householdStub.setHouseholdID(id);

                Navigation.findNavController(view).navigate(R.id.action_createHouseholdFragment_to_homeFragment);
            }
        });

        return view;
    }
}