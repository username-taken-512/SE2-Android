package com.example.se2_android.HomeTab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.se2_android.R;
import com.example.se2_android.Stubs.HouseholdStub;
import com.example.se2_android.Stubs.LoginStub;

public class HomeFragment extends Fragment {
    View view;
    Button logoutButton;
    TextView nameOfHouse;

    HouseholdStub householdStub;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        householdStub = HouseholdStub.getInstance();

        nameOfHouse = view.findViewById(R.id.nameOfHouseText);
        nameOfHouse.setText(householdStub.getHouseholdName());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO: TEMP BUTTON
        LoginStub loginStub = LoginStub.getInstance();
        logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginStub.setLoggedIn(false);
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_loginFragment);

            }
        });

        //TODO:
        //If not logged in, go to login, else skip navigation
        if (!loginStub.isLoggedIn()) {
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_loginFragment);
        }
    }
}
