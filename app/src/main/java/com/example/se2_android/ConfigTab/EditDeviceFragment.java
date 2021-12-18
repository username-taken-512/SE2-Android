package com.example.se2_android.ConfigTab;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.se2_android.DevicesTab.DevicesAdapter;
import com.example.se2_android.Models.Device;
import com.example.se2_android.R;
import com.example.se2_android.Utils.WebsocketViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class EditDeviceFragment extends Fragment {
    private static final String TAG = "EditDeviceFragment";

    private WebsocketViewModel websocketViewModel;
    Context context;
    RecyclerView recyclerView;
    View view;
    ArrayList<Device> deviceList = new ArrayList<>();
    FloatingActionButton backButton, addButtonConfig;
    ArrayList<Device> itemList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_device, container, false);
        context = getActivity();
        websocketViewModel = new ViewModelProvider(getActivity()).get(WebsocketViewModel.class);

        recyclerView = view.findViewById(R.id.editDeviceView);
        backButton = view.findViewById(R.id.backButtonConfig);
        addButtonConfig = view.findViewById(R.id.addButtonConfig);

        //Get devicelist to fill recyclerView
        if (!websocketViewModel.getDeviceList().isEmpty()) {
            itemList = websocketViewModel.getDeviceList();
        }
        setupRecyclerView();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToConfig();
            }
        });

        addButtonConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_editDeviceFragment_to_addDeviceFragment);
            }
        });
        return view;
    }

    private void setupRecyclerView() {
        //Set up recyclerView for electronic devices
        EditDeviceAdapter editDeviceAdapter = new EditDeviceAdapter(itemList, context);
        recyclerView.setAdapter(editDeviceAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        websocketViewModel.getDeviceListVersion().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                editDeviceAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void backToConfig() {
        Navigation.findNavController(view).navigate(R.id.action_editDeviceFragment_to_configFragment);
    }
}