package pam.poluxion.models;

import android.util.Log;
import pam.poluxion.data.GeneralClass;

public class User {
    private static final String TAG = "User";
    private static final double AVERAGE_WALKING_SPEED = 1.3888;  // 5km/h ~= 1.38 m/s

    private double weight, height;
    private int age;
    private String nameUser, lastNameUser, gender, ID;

    public User() {
        weight = 55.0;
        height = 165.0;  //default values

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

    public void setID(String ID) {
        this.ID = ID;
    }

    public void displayInfo() {
        Log.e(TAG,"ID user = " + ID);  //user UID - fireAuth
        Log.e(TAG,"Name = " + nameUser);
        Log.e(TAG,"Last name = " + lastNameUser);
        Log.e(TAG,"Age = " + age);
        Log.e(TAG,"Gender = " + gender);
        Log.e(TAG,"Weight = " + weight);
        Log.e(TAG,"Height = " + height);
    }

    public void updateData(String ID) {
        this.ID = ID;
        nameUser = GeneralClass.getFirebaseHelperObject().readName(ID);
        lastNameUser = GeneralClass.getFirebaseHelperObject().readLastName(ID);
        age = GeneralClass.getFirebaseHelperObject().readAge(ID);
        gender = GeneralClass.getFirebaseHelperObject().readGender(ID);
        height = GeneralClass.getFirebaseHelperObject().readHeight(ID);
        weight = GeneralClass.getFirebaseHelperObject().readWeight(ID);

        Log.e(TAG,"This display is from update method");
        displayInfo();
    }
}
