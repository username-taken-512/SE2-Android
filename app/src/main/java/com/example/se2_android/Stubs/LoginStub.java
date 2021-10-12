package com.example.se2_android.Stubs;

public class LoginStub {
    private static LoginStub loginStub = null;
    String username = "u";
    String password = "1";
    Boolean loggedIn = true;

    public static LoginStub getInstance() {
        if (loginStub == null) {
            loginStub = new LoginStub();
        }
        return loginStub;
    }

    public LoginStub() {
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
