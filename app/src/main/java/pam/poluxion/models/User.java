package pam.poluxion.models;

import pam.poluxion.helper.FirebaseHelper;

public class User {
    private double weight, height;
    private int steps, age;
    private String name, lastName, email, password, gender;
    private boolean loggedIn = false;

    private FirebaseHelper FBHelper;

    public User(FirebaseHelper FBHelper) {
        this.FBHelper = FBHelper;
    }

    public boolean checkIfLogged() {
        return loggedIn;
    }
}
