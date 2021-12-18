package com.example.se2_android.Login;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.se2_android.Models.Household;
import com.example.se2_android.Models.User;
import com.example.se2_android.R;
import com.example.se2_android.Utils.WebsocketAuth;
import com.example.se2_android.Utils.WebsocketViewModel;

public class RegisterFragment extends Fragment {
    private static final String TAG = "RegisterFragment";
    View view;
    EditText firstName, lastName, email, pWord, option;
    CheckBox checkBox;
    Button create, backButton;
    AutoCompleteTextView autoCompleteTextView;
    boolean checkAuto = false;

    private WebsocketAuth websocketAuth;
    private static WebsocketViewModel websocketViewModel;
    private Observer<Integer> observer;
    User newUser;
    Household newHousehold;

    private final static String[] OPTIONS = new String[]{
            "Join Household", "Create Household",
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register, container, false);
        websocketViewModel = new ViewModelProvider(getActivity()).get(WebsocketViewModel.class);

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
                closeKeyboard();
                String item = parent.getItemAtPosition(position).toString();
                setHintForEdit(item);
            }
        });


        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                closeKeyboard();
                autoCompleteTextView.showDropDown();
            }
        });

        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observer = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Log.i(TAG, "Livedata onChanged: " + integer);
                if (integer == 1) {         // Connection opened
                    Log.i(TAG, "Livedata onChanged: inside 1");
                    Log.i(TAG, "Livedata onChanged: inside 1: " + option.getHint().toString());
                    if (autoCompleteTextView.getText().toString().equals("Join Household")) {
                        // Send user
                        if (websocketAuth != null) {
                            Log.i(TAG, "Livedata onChanged: Create new User");
                            websocketAuth.sendNewUser(newUser);
                        }
                    } else if (autoCompleteTextView.getText().toString().equals("Create Household")) {
                        // Send Household
                        if (websocketAuth != null) {
                            Log.i(TAG, "Livedata onChanged: Create new Household");
                            websocketAuth.sendNewHousehold(newHousehold);
                        }
                    }
                } else if (integer == 4) {  // new Household returned, time to send new User
                    newUser.setHouseholdId(websocketViewModel.getHouseholdId());
                    option.setInputType(InputType.TYPE_CLASS_NUMBER);
                    autoCompleteTextView.setText("Join Household", false);
                    option.setText(String.valueOf(websocketViewModel.getHouseholdId()));
                    if (websocketAuth != null) {
                        Log.i(TAG, "Livedata onChanged: Create new User");
                        websocketAuth.sendNewUser(newUser);
                    }
                } else if (integer == 5) { // new User returned, navigate to login
                    websocketViewModel.setWebsocket(null);
                    websocketViewModel.setConnectionStatusAuth(0);
                    websocketViewModel.setConnectionStatus(0);

                    //Remove observer & Navigate to login
                    try {
                        Log.i(TAG, "Livedata onChanged, Try to navigate");
                        websocketViewModel.getConnectionStatusAuth().removeObserver(this);
                        Toast.makeText(getContext(), "User created: " + email.getText().toString(), Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment);
                    } catch (Exception e) {
                        Log.i(TAG, "Livedata onChanged, caught exception:\n" + e.getMessage());
                        e.printStackTrace();
                    }
                } else if (integer == 6) {
                    Toast.makeText(getActivity(), "User not created", Toast.LENGTH_SHORT).show();
                }
            }
        };
        websocketViewModel.getConnectionStatusAuth().observe(getActivity(), observer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        websocketViewModel.getConnectionStatusAuth().removeObserver(observer);
    }

    private void setHintForEdit(String msg) {
        if (msg.contains("Join")) {
            checkAuto = true;
            option.setHint("Enter ID of Household");
            option.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (msg.contains("Create")) {
            checkAuto = true;
            option.setHint("Enter name of Household");
            option.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        option.setText("");
    }


    private void checkBoxChange(boolean isChecked) {
        if (isChecked) {
            pWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            pWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }


    public void createUser() {
        boolean check = false;

        String firstname = firstName.getText().toString().trim();
        String lastname = lastName.getText().toString().trim();
        String emailString = email.getText().toString().trim();
        String password = pWord.getText().toString().trim();
        String optionCheck = option.getHint().toString().trim();


        if (TextUtils.isEmpty(firstname)) {
            firstName.setError("You need to enter a firstname");
            check = true;
        } else if (!firstname.matches("[a-zåäöA-ZÅÄÖ]+")) {
            firstName.setError("Firstname can only contain characters!");
            check = true;
        }

        if (TextUtils.isEmpty(lastname)) {
            lastName.setError("You need to enter a lastname");
            check = true;
        } else if (!lastname.matches("[a-zåäöA-ZÅÄÖ]+")) {
            lastName.setError("Lastname can only contain characters!");
            check = true;
        }


        if (TextUtils.isEmpty(emailString)) {
            email.setError("You need to enter an email");
            check = true;
        } else if (!emailString.matches("^[\\w-_\\.+]+\\@([\\w]+\\.)+[a-z]+[a-z]$")) {
            email.setError("This is not a valid email");
            check = true;
        }


        if (TextUtils.isEmpty(password)) {
            pWord.setError("You need to enter a password");
            check = true;
        }

        if (!checkAuto) {
            autoCompleteTextView.setError("Choose an option");
            check = true;
        } else {
            autoCompleteTextView.setError(null);
        }

        if (optionCheck.contains("ID")) {
            String number = option.getText().toString().trim();
            System.out.println("nummer " + number);
            if (!number.matches("[0-9]+")) {
                option.setError("Digits only");
            }
        } else if (optionCheck.contains("name")) {
            String name = option.getText().toString().trim();
            System.out.println("namn " + name);
            if (!name.matches("[a-zåäöA-ZÅÄÖ]+")) {
                option.setError("Characters only");
            }
        } else {
            option.setError("Please choose an option above");
        }

        if (check) {
            return;
        }

        // Prepare objects to send
        newUser = new User();
        if (autoCompleteTextView.getText().toString().equals("Create Household")) {
            newHousehold = new Household();
            newHousehold.setName(option.getText().toString());
        } else if (autoCompleteTextView.getText().toString().equals("Join Household")) {
            newUser.setHouseholdId(Integer.parseInt(option.getText().toString()));
        }
        newUser.setUsername(emailString);
        newUser.setName(firstname + " " + lastname);
        newUser.setPassword(password);

        //Connect ws houseauth
        websocketAuth = new WebsocketAuth(getContext(), view, getActivity());

//        Toast.makeText(getContext(), "Account has been created", Toast.LENGTH_SHORT).show();
//        Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment);


        //hämta värdena, gör ett obejkt o skicka till server???
        //navigera till login
    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}