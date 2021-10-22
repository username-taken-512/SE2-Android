package com.example.se2_android.Login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.InputType;
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
import android.widget.Toast;

import com.example.se2_android.R;
import com.example.se2_android.Stubs.LoginStub;

public class LoginFragment extends Fragment {
    View view;

    EditText email, pWord;
    Button loginButton, forgotPasswordButton, signUpButton;
    CheckBox checkBox;
    SwitchCompat rememberSwitch;

    LoginStub loginStub;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);

        loginStub = LoginStub.getInstance();


        email = view.findViewById(R.id.uName);
        pWord = view.findViewById(R.id.pWord);

        loginButton = view.findViewById(R.id.loginButton);
        signUpButton = view.findViewById(R.id.toSignUp);
        forgotPasswordButton = view.findViewById(R.id.fgtPass);

        checkBox = view.findViewById(R.id.checkBox);
        rememberSwitch = view.findViewById(R.id.rememberMeSwitch);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });



        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPassword();
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


    public void login(){


        boolean check = false;

        //Måste hämta värden från databas o kolla här

        if (TextUtils.isEmpty(email.getText().toString().trim())){
            email.setError("Email needs to be inputted");
            check= true;
        }

        if (TextUtils.isEmpty(pWord.getText().toString().trim())){
            pWord.setError("Password needs to be inputted");
            check= true;
        }

        if (check){
            return;
        }




        if (email.getText().toString().equals(loginStub.getUsername()) && pWord.getText().toString().equals(loginStub.getPassword())){
            if (rememberSwitch.isChecked()){
                //autologin
            }
            loginStub.setLoggedIn(true);
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment);
            Toast.makeText(getContext(), "You got logged in as user " + loginStub.getUsername().toUpperCase(), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "Your accont doesnt exist", Toast.LENGTH_SHORT).show();
        }
    }


    public void signUp(){
        Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment);
    }


    public void forgetPassword(){
        //skriva in mail, kolla om den finns, finns den, få lösen på mailen av server?
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Forgot password");

        EditText enterMail = new EditText(getContext());
        enterMail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        enterMail.setHint("Enter mail");
        dialog.setView(enterMail);

        dialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mail = enterMail.getText().toString();
                checkMailAndSendToServer(mail);
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
                Toast.makeText(getContext(), "Enter your email if you want a new password!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();


    }

    public void checkBoxChange(boolean isChecked){
        if (isChecked){
            pWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }else{
            pWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }

    }

    public void checkMailAndSendToServer(String mail){
        //skicka mailen till server som kollar i databas om mailen finns, om den gör de skickar den nytt pw till mailen;
        if (!mail.matches("^[\\w-_\\.+]+\\@([\\w]+\\.)+[a-z]+[a-z]$")){
            Toast.makeText(getContext(), "No valid mail inputted, No mail sent!", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(), "Mail sent to " + mail, Toast.LENGTH_SHORT).show();
        }
    }

}