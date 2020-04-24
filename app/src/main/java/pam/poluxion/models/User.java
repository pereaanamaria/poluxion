package pam.poluxion.models;

import android.util.Log;

import pam.poluxion.data.FirebaseHelper;
import pam.poluxion.data.GeneralClass;

public class User {
    private static final String TAG = "User";

    private static final double AVERAGE_WALKING_SPEED = 5000/3600;  // 5km/h ~= 1.38 m/s

    //private String deviceID;
    private double weight, height;
    private int age;
    private String nameUser, lastNameUser, email, gender, ID;
    private boolean loggedIn;

    public User() {
        //this.deviceID = deviceID;
        this.weight = 55.0;  //default value
        this.height = 165.0; //default value

        Log.e(TAG,"Successfully created: " + GeneralClass.getAndroid_id());
    }

    public String getKm() {
        // 1 step == 0.762 m
        // 1 step == 0.762/1000 km

        double temp = (GeneralClass.getStepCounterObject().getSteps() * 0.762) / 1000; //the value is measured in km
        return temp + " km";
    }

    public String getCals() {
        double calPerMin = (0.035 * weight) + (0.029 * weight * ((AVERAGE_WALKING_SPEED * AVERAGE_WALKING_SPEED) / height));
        double stepsPerMin = 110;
        double temp = (calPerMin * GeneralClass.getStepCounterObject().getSteps()) / stepsPerMin;
        return temp + " cal";
    }

    public void login() {
        loggedIn = true;
        setLoggedFB();
    }

    public void logout() {
        loggedIn = false;
        setLoggedFB();
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

    public void setID(String ID) {
        this.ID = ID;
    }
    public String getID() {
        return ID;
    }

    private void setLoggedFB() {
        GeneralClass.getFirebaseHelperObject().inputBoolean(GeneralClass.getAndroid_id()+"/connectedUser",loggedIn);
    }
}
