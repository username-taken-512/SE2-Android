package com.example.se2_android.ConfigTab;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.se2_android.R;
import com.example.se2_android.Stubs.LoginStub;

public class ConfigFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_config, container, false);
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