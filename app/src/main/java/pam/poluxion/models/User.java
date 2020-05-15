package pam.poluxion.models;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.Calendar;

import pam.poluxion.data.GeneralClass;

public class User {
    private static final String TAG = "User";
    private static final DecimalFormat df2 = new DecimalFormat("0.00");

    private double weight, height;
    private int age;
    private String nameUser, lastNameUser, gender, ID = "0_Unknown user";

    public User() {
        weight = 65.0; //default values : kg
        height = 170.0;  //default values : cm
        gender = "male"; //default
        age = 30; //default

        Log.e(TAG,"Successfully created");
    }

    public String getKm() {
        // 1 step == 0.762 m
        // 1 step == 0.762/1000 km

        double temp = (GeneralClass.getStepCounterObject().getSteps() * 0.762) / 1000; //the value is measured in km
        return df2.format(temp) + " km";
    }

    public String getCals() {
        double speed = GeneralClass.getStepCounterObject().getAverageSpeed();
        double calPerMin = (0.035 * weight) + (0.029 * weight * ((speed * speed) / height));
        double stepsPerMin = GeneralClass.getStepCounterObject().getStepsPerMin();

        double temp = (calPerMin * GeneralClass.getStepCounterObject().getSteps()) / stepsPerMin;
        if (Double.isNaN(temp)) {
            return "0.0 cal";
        }
        return df2.format(temp) + " cal";
    }

    public String getBPI() {
        double VEMSt = 0.0, CVt = 500.0;
        if(gender != null) {
            if (gender.equals("male")) {
                VEMSt = height * 0.036 - age * 0.031 - 1.41;
                CVt = height * 0.052 - age * 0.028 - 3.2;
            }
            if (gender.equals("female")) {
                VEMSt = height * 0.026 - age * 0.028 - 0.36;
                CVt = height * 0.049 - age * 0.019 - 3.76;
            }
        }

        double temp = (VEMSt/CVt) * 100.0;
        return df2.format(temp) + " ";
    }

    public void setNameUser(String nameUser) {this.nameUser = nameUser;}
    public String getNameUser() {return nameUser;}

    public void setLastNameUser(String lastNameUser) {this.lastNameUser = lastNameUser;}
    public String getLastNameUser() {return lastNameUser;}

    public void setAge(int age) {this.age = age;}
    public void setAge(String dobStr) {this.age = getAgeFromDob(dobStr);}
    public int getAge() {return age;}
    private int getAgeFromDob(String dobStr){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        int firstBashIndex = dobStr.indexOf('/');
        int secondBashIndex = dobStr.lastIndexOf('/');

        String dayStr = dobStr.substring(0,firstBashIndex);
        String monthStr = dobStr.substring(firstBashIndex+1,secondBashIndex);
        String yearStr = dobStr.substring(secondBashIndex+1);

        int day = Integer.parseInt(dayStr);
        int month = Integer.parseInt(monthStr);
        int year = Integer.parseInt(yearStr);

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }
        this.age = age;

        Log.e(TAG,"DOB --> Age : " + dobStr + " --> " + age);
        return age;
    }

    public void setGender(String gender) {this.gender = gender;}
    public String getGender() {return gender;}

    public void setWeight(double weight) {this.weight = weight;}
    public double getWeight() {return weight;}

    public void setHeight(double height) {this.height = height;}
    public double getHeight() {return height;}
    
    public void setID(String ID) {this.ID = ID;}
    public String getID() {return ID;}

    public void updateData(String ID) {
        this.ID = ID;
        nameUser = GeneralClass.getFirebaseHelperObject().readName(ID);
        lastNameUser = GeneralClass.getFirebaseHelperObject().readLastName(ID);
        try {
            age = getAgeFromDob(GeneralClass.getFirebaseHelperObject().readDOB(ID));
        } catch(Exception e) {
            age = 30;
        }
        gender = GeneralClass.getFirebaseHelperObject().readGender(ID);
        height = GeneralClass.getFirebaseHelperObject().readHeight(ID);
        weight = GeneralClass.getFirebaseHelperObject().readWeight(ID);
    }
}