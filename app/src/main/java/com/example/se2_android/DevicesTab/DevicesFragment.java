package com.example.se2_android.DevicesTab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se2_android.Models.Device;
import com.example.se2_android.R;
import com.example.se2_android.Utils.WebsocketViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.WebSocket;


public class DevicesFragment extends Fragment {
    private static final int OPCODE_SEND_DEVICE = 10;
    private static final int OPCODE_SEND_ALL_DEVICES = 11;
    private static final int OPCODE_CHANGE_DEVICE_STATUS = 20;
    private static final int OPCODE_CHANGE_DEVICE_HOUSEHOLD = 21;

    private static final String TAG = "DeviceFragment";

    private static WebSocket webSocket;
    private static String SERVER_PATH = "ws://192.168.0.100:7071/house?token=123";
    private static Gson gson;

    private WebsocketViewModel websocketViewModel;

    View view;
    Context context;

    //RecyclerView for electronic devices
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<Device> itemList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_devices, container, false);
        context = getActivity();

        websocketViewModel = new ViewModelProvider(getActivity()).get(WebsocketViewModel.class);
        gson = new Gson();

        //Get devicelist to fill recyclerView
        if (!websocketViewModel.getDeviceList().isEmpty()) {
            itemList = websocketViewModel.getDeviceList();
        }
        setupRecyclerView();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO:
        //If not logged in, go to login, else skip navigation
//        LoginStub loginStub = LoginStub.getInstance();
//        if (!loginStub.isLoggedIn()) {
//            Navigation.findNavController(view).navigate(R.id.action_devicesFragment_to_loginFragment);
//        }

        begin();
    }

    private void setupRecyclerView() {
        //Set up recyclerView for electronic devices
        mRecyclerView = view.findViewById(R.id.devicesRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(view.getContext());

        mAdapter = new DevicesAdapter(itemList, context);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        websocketViewModel.getDeviceListVersion().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                Log.i(TAG, "Livedata onChanged: " + integer);
                List<Device> sortedList = new ArrayList();
                if (websocketViewModel.getDeviceList().size() > 0) {
                    Collections.sort(websocketViewModel.getDeviceList(), new Comparator<Device>() {
                        @Override
                        public int compare(final Device object1, final Device object2) {
                            return object2.getType().compareTo(object1.getType());
                        }
                    });
                }


                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void begin() {
        //If sharedPref == null, go to login
        //Get token
        SharedPreferences sharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");

        //If no saved token, go to login
        if (token.equals("")) {
            Navigation.findNavController(view).navigate(R.id.action_devicesFragment_to_loginFragment);
        }
    }
}
