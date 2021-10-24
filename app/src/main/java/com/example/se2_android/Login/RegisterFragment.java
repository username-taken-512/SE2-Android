package com.example.se2_android.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.se2_android.R;

public class RegisterFragment extends Fragment {
    View view;
    EditText firstName, lastName, email, pWord;
    CheckBox checkBox;
    Button create, backButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register, container, false);

        firstName = view.findViewById(R.id.firstNameText);
        lastName = view.findViewById(R.id.lastNameText);
        email = view.findViewById(R.id.emailText);
        pWord = view.findViewById(R.id.passwordText);

        checkBox = view.findViewById(R.id.checkPass);

        create = view.findViewById(R.id.createButton);

        backButton = view.findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigera till login
                Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment);
            }
        });


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBoxChange(isChecked);
            }
        });

        return view;
    }


    private void checkBoxChange(boolean isChecked) {
        if (isChecked){
            pWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }else{
            pWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }


    public void createUser(){
        boolean check = false;

        String firstname = firstName.getText().toString().trim();
        String lastname = lastName.getText().toString().trim();
        String emailString = email.getText().toString().trim();
        String password = pWord.getText().toString().trim();


        if (TextUtils.isEmpty(firstname)){
            firstName.setError("You need to enter a firstname");
            check = true;
        }else if (!firstname.matches("[a-zåäöA-ZÅÄÖ]+")){
            firstName.setError("Firstname can only contain characters!");
            check = true;
        }

        if (TextUtils.isEmpty(lastname)){
            lastName.setError("You need to enter a lastname");
            check = true;
        }else if (!lastname.matches("[a-zåäöA-ZÅÄÖ]+")){
            lastName.setError("Lastname can only contain characters!");
            check = true;
        }


        if (TextUtils.isEmpty(emailString)){
            email.setError("You need to enter an email");
            check = true;
        }else if (!emailString.matches("^[\\w-_\\.+]+\\@([\\w]+\\.)+[a-z]+[a-z]$")){
            email.setError("This is not a valid email");
            check = true;
        }


        if (TextUtils.isEmpty(password)){
            pWord.setError("You need to enter a password");
            check = true;
        }

        if (check){
            return;
        }




        //hämta värdena, gör ett obejkt o skicka till server???
        //navigera till login


    }
}