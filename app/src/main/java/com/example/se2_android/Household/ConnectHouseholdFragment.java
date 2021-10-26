package com.example.se2_android.Household;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("Join Household");

                EditText enterID = new EditText(getContext());
                enterID.setInputType(InputType.TYPE_CLASS_NUMBER);
                enterID.setHint("Enter id of household");
                dialog.setView(enterID);

                dialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String id = enterID.getText().toString();
                        checkIdSendToServer(id);
                    }
                });


                dialog.setNegativeButton("Go back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                dialog.setNeutralButton("Help", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Enter the id of the household you want to join!", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.show();
            }
        });

        return view;
    }


    public void checkIdSendToServer(String id){
        if (!id.matches("[0-9]+")){
            Toast.makeText(getContext(), "A valid ID is required!", Toast.LENGTH_SHORT).show();
        } else {
            householdStub.setHouseholdID(id);
            householdStub.setHouseholdName("Id set to - " + id);
            Toast.makeText(getContext(), "Joined Household with id - " + householdStub.getHouseholdID(), Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigate(R.id.action_connectHouseholdFragment_to_homeFragment);
        }

    }


}