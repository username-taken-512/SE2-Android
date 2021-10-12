package com.example.se2_android.DevicesTab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se2_android.Models.Device;
import com.example.se2_android.R;
import com.example.se2_android.Stubs.LoginStub;

import java.util.ArrayList;

public class DevicesFragment extends Fragment {
    View view;

    //RecyclerView for electronic devices
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<Device> itemList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_devices, container, false);

        //TODO: Recyclerview demo
        itemList.add(new Device(0, true, "lamp", "Kitchen lamp"));
        itemList.add(new Device(1, true, "lamp", "Bathroom lamp"));
        itemList.add(new Device(2, false, "lamp", "Bedroom lamp"));
        setupRecyclerView();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO:
        //If not logged in, go to login, else skip navigation
        LoginStub loginStub = LoginStub.getInstance();
        if (!loginStub.isLoggedIn()) {
            Navigation.findNavController(view).navigate(R.id.action_devicesFragment_to_loginFragment);
        }
    }

    private void setupRecyclerView() {
        //Set up recyclerView for electronic devices
        mRecyclerView = view.findViewById(R.id.devicesRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(view.getContext());
        mAdapter = new DevicesAdapter(itemList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }
}
