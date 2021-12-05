package com.example.se2_android.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.se2_android.R;

public class RegisterFragment extends Fragment {
    View view;
    EditText firstName, lastName, email, pWord, option;
    CheckBox checkBox;
    Button create, backButton;
    AutoCompleteTextView autoCompleteTextView;
    boolean checkAuto = false;

    private final static String[] OPTIONS = new String[]{
            "Join Household", "Create Household",
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register, container, false);

        firstName = view.findViewById(R.id.firstNameText);
        lastName = view.findViewById(R.id.lastNameText);
        email = view.findViewById(R.id.emailText);
        pWord = view.findViewById(R.id.passwordText);
        option = view.findViewById(R.id.optionEdit);

        checkBox = view.findViewById(R.id.checkPass);
        autoCompleteTextView = view.findViewById(R.id.option);

        create = view.findViewById(R.id.createButton);
        backButton = view.findViewById(R.id.backButton);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, OPTIONS);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setKeyListener(null);
        option.setHint("Choose an option above first");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigera till login
                Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment);
            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                setHintForEdit(item);
            }
        });


        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                autoCompleteTextView.showDropDown();
            }
        });

        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView.showDropDown();

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

    private void setHintForEdit(String msg) {
        if (msg.contains("Join")) {
            checkAuto = true;
            option.setHint("Enter ID of Household");
        } else if (msg.contains("Create")) {
            checkAuto = true;
            option.setHint("Enter name of Household");
        }
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
        String optionCheck = option.getHint().toString().trim();


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

        if (!checkAuto){
            autoCompleteTextView.setError("Choose an option");
            check = true;
        }else{
            autoCompleteTextView.setError(null);
        }

        if (optionCheck.contains("ID")){
            String number = option.getText().toString().trim();
            System.out.println("nummer " + number);
            if (!number.matches("[0-9]+")){
                option.setError("Digits only");
            }
        }else if (optionCheck.contains("name")) {
            String name = option.getText().toString().trim();
            System.out.println("namn " + name);
            if (!name.matches("[a-zåäöA-ZÅÄÖ]+")){
                option.setError("Characters only");
            }
        }else {
            option.setError("Please choose an option above");
        }

        if (check){
            return;
        }


        Toast.makeText(getContext(), "Account has been created", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment);




        //hämta värdena, gör ett obejkt o skicka till server???
        //navigera till login


    }
}