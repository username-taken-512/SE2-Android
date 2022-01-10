package com.example.se2_android.Login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.se2_android.Models.User;
import com.example.se2_android.R;

import com.example.se2_android.Utils.WebsocketAuth;
import com.example.se2_android.Utils.WebsocketViewModel;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    View view;
    private static WebsocketViewModel websocketViewModel;
    private WebsocketAuth websocketAuth;

    EditText email, pWord;
    Button loginButton, forgotPasswordButton, signUpButton;
    CheckBox checkBox;
    private Observer<Integer> observer;

    User user;
    Boolean dontLogin = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);
        websocketViewModel = new ViewModelProvider(getActivity()).get(WebsocketViewModel.class);

        email = view.findViewById(R.id.uName);
        pWord = view.findViewById(R.id.pWord);

        loginButton = view.findViewById(R.id.loginButton);
        signUpButton = view.findViewById(R.id.toSignUp);
        forgotPasswordButton = view.findViewById(R.id.fgtPass);

        checkBox = view.findViewById(R.id.checkBox);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Test
//                websocketViewModel.getConnectionStatusAuth().removeObserver(observer);
//                Log.i(TAG, "signup, removing Login observer");

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Login, to know when to navigate
        observer = new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                Log.i(TAG, "Livedata onChanged: " + integer);
                if (integer == 1 && user != null) {         // Connection opened
                    Log.i(TAG, "Livedata onChanged: inside 1");
                    if (websocketAuth != null) {
                        Log.i(TAG, "Livedata onChanged: inside 1, before call");
                        websocketAuth.login(user);
                    }
                } else if (integer == 2) {  // Token returned, time to login
                    goToHome();
//                    try {
//                        Log.i(TAG, "Livedata onChanged, Try to navigate");
//                        websocketViewModel.getConnectionStatusAuth().removeObserver(this);
//                        websocketViewModel.setWebsocket(null);
//                        websocketViewModel.setConnectionStatusAuth(0);
//                        websocketViewModel.setConnectionStatus(0);
//                        Toast.makeText(getContext(), "You got logged in as user " + email.getText().toString(), Toast.LENGTH_SHORT).show();
//                        Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment);
//                    } catch (Exception e) {
//                        Log.i(TAG, "Livedata onChanged, cought exception:\n" + e.getMessage());
//                        e.printStackTrace();
//                    }
                }
            }
        };
        websocketViewModel.getConnectionStatusAuth().observe(getActivity(), observer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        websocketViewModel.getConnectionStatusAuth().removeObservers(getActivity());
        websocketViewModel.getConnectionStatusAuth().removeObserver(observer);
    }

    private void goToHome() {
        try {
            Log.i(TAG, "Livedata onChanged, Removing observer");
            websocketViewModel.getConnectionStatusAuth().removeObserver(observer);
            Log.i(TAG, "Livedata onChanged, set ws null");
            websocketViewModel.setWebsocket(null);
//            websocketViewModel.setConnectionStatusAuth(0);
            Log.i(TAG, "Livedata onChanged, set NOTHOUSE-Status 0");
            websocketViewModel.setConnectionStatus(0);
            Toast.makeText(getContext(), "You got logged in as user " + email.getText().toString(), Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Livedata onChanged, Closing ws Auth");
            websocketAuth.closeConnection();
            Log.i(TAG, "Livedata onChanged, Navigate");
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment);
        } catch (Exception e) {
            Log.i(TAG, "Livedata onChanged, cought exception:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void login() {
        boolean check = false;

        //Måste hämta värden från databas o kolla här

        if (TextUtils.isEmpty(email.getText().toString().trim())) {
            email.setError("Please input a valid email");
            check = true;
        }

        if (TextUtils.isEmpty(pWord.getText().toString().trim())) {
            pWord.setError("Please input a password");
            check = true;
        }

        if (check) {
            return;
        }

        //Create user object
        user = new User();
        user.setUsername(email.getText().toString().trim());
        user.setPassword(pWord.getText().toString());

        //Connect ws houseauth
        websocketAuth = new WebsocketAuth(getContext(), view, getActivity());


        //Store token
        //Navigate to Home

        //TODO: Dont need to check household anymore
//            if (householdStub.getHouseholdID().isEmpty()) {
//                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_connectHouseholdFragment);
//            } else {
//                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment);
//            }


    }


    public void signUp() {
        Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment);
    }


    public void forgetPassword() {
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

    public void checkBoxChange(boolean isChecked) {
        if (isChecked) {
            pWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            pWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    public void checkMailAndSendToServer(String mail) {
        //skicka mailen till server som kollar i databas om mailen finns, om den gör de skickar den nytt pw till mailen;
        if (!mail.matches("^[\\w-_\\.+]+\\@([\\w]+\\.)+[a-z]+[a-z]$")) {
            Toast.makeText(getContext(), "A valid email is required, no email sent!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Mail sent to " + mail, Toast.LENGTH_SHORT).show();
            dontLogin = true;
            websocketAuth = new WebsocketAuth(getContext(), view, getActivity());
            websocketAuth.sendForgotPassword(mail);
            dontLogin = false;
        }
    }
}