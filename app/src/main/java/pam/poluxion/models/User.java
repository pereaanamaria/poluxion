package pam.poluxion.models;

import android.util.Log;

import pam.poluxion.helper.FirebaseHelper;

public class User {
    private static final String TAG = "User";

    private String deviceID;

    private double weight, height;
    private int age;
    private String nameUser, lastNameUser, email, password, gender;
    private boolean loggedIn = false;

    public User(String deviceID) {
        this.deviceID = deviceID;

        Log.e(TAG,"Successfully created: " + deviceID);
    }

    public boolean checkIfLogged() {
        return loggedIn;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }
    public String getNameUser() {
        return nameUser;
    }

    public void setLastNameUser(String lastNameUser) {
        this.lastNameUser = lastNameUser;
    }
    public String getLastNameUser() {
        return lastNameUser;
    }

    public void setAge(int age) {
        this.age = age;
    }
    public int getAge() {
        return age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getGender() {
        return gender;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
    public double getWeight() {
        return weight;
    }

    public void setHeight(double height) {
        this.height = height;
    }
    public double getHeight() {
        return height;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }
}
