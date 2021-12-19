package com.example.se2_android.HomeTab;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.se2_android.MainActivity;
import com.example.se2_android.R;
import com.example.se2_android.Utils.WebsocketViewModel;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private Context context;
    private View view;
    private SharedPreferences sharedPref;
    private static WebsocketViewModel websocketViewModel;
    private Observer<Integer> observer;

    Button logoutButton;
    TextView nameOfHouse;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        websocketViewModel = new ViewModelProvider(getActivity()).get(WebsocketViewModel.class);

        nameOfHouse = view.findViewById(R.id.nameOfHouseText);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO: TEMP BUTTON
        logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                websocketViewModel.getConnectionStatus().removeObserver(observer);
                ((MainActivity) context).disconnectWebsocket();
                ((MainActivity) context).clearToken();
//                clearToken();

                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_loginFragment);

            }
        });

        begin();

        //TODO:
        //If not logged in, go to login, else skip navigation
//        if (!loginStub.isLoggedIn()) {
//            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_loginFragment);
//        }
    }

    private void begin() {
        //If sharedPref == null, go to login
        //Get token
        sharedPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", "");
        nameOfHouse.setText(token);

        //If no saved token, go to login
        if (token.equals("")) {
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_loginFragment);
        }
        Log.i(TAG, "Trying to connect with [" + token + "]");

        //Set up listener to manage connection
        observer = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Log.i(TAG, "Livedata onChanged: " + integer);
                if (integer == 0) {         // Connection not open, should open
                    Log.i(TAG, "Livedata trying to connect if ws is null: " + integer);
                    ((MainActivity) context).connectWebsocket(token);
                } else if (integer == 1) {  // Connected

                } else if (integer == 3) {  // Token invalid. Clear token and return to login
                    Log.i(TAG, "Livedata clearing token and going login: " + integer);
//                    clearToken();
//                    Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_loginFragment);
                }
            }
        };
        websocketViewModel.getConnectionStatus().observe(getActivity(), observer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        websocketViewModel.getConnectionStatus().removeObserver(observer);
    }

    private void getToken() {

    }

    private void clearToken() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", "");
        editor.commit();
    }
}
