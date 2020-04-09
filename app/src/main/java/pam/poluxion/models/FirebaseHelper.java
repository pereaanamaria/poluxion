package pam.poluxion.models;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pam.poluxion.MainActivity;
import pam.poluxion.widgets.ProgressAnimation;

public class FirebaseHelper {
    private DatabaseReference myPressureRef, myAQIRef, myTemperatureRef;  //refrences to Firebase

    private static final String TAG ="FirebaseHelper";

    private int AQI, Temperature;
    private double Pressure;
    private int weight, height, steps; //variables storing Firebase data
    private Weather weather;

    private SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");  //formatting the current date into a dd-MM-yyyy String type

    public FirebaseHelper(Weather weather) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();  //stores Firebase instance into database variable

        //gets current date which is later formatted into dd-MM-yyyy type
        //Date currentDate = Calendar.getInstance().getTime();
        //String currentFormattedDate = df.format(currentDate);

        this.weather = weather;

        //stores a Firebase reference of the steps made during the current date, the weight and the height of the user
        myAQIRef = database.getReferenceFromUrl("https://poluxion-90559.firebaseio.com//AQI");
        myPressureRef = database.getReferenceFromUrl("https://poluxion-90559.firebaseio.com//Pressure");
        myTemperatureRef = database.getReferenceFromUrl("https://poluxion-90559.firebaseio.com//Temperature");
        //myRef = database.getReferenceFromUrl("https://poluxion-90559.firebaseio.com//" + currentFormattedDate);
        //myRef.setValue(currentFormattedDate);
    }

    //Write in Firebase
    public void inputAQI(String aqi) {
        myAQIRef.setValue(Integer.parseInt(aqi)); //stores AQI into Firebase
        MainActivity.nrAqiTV.setText(aqi);
        AQI = Integer.parseInt(aqi);
        Log.e(TAG,"AQI data set = " + AQI);
        getAQIPercentage();
    }

    public void inputPressure(String pressure) {
        myPressureRef.setValue(Double.parseDouble(pressure)); //stores pressure into Firebase
        MainActivity.pressureTV.setText(pressure + " ");
        Pressure = Double.parseDouble(pressure);
        Log.e(TAG,"Pressure data set = " + Pressure);
    }

    public void inputTemperature(String temperature) {
        double temp = Double.parseDouble(temperature);
        Log.e(TAG,temp + "");
        Temperature = (int) Math.floor(temp);
        myTemperatureRef.setValue(Temperature); //stores temperature into Firebase
        MainActivity.temperatureTV.setText(Temperature + " ");
        Log.e(TAG,"Temperature data set = " + Temperature);
    }

    //Read from Firebase and passes the value to another object type
    public int getAQI() {
        readAQI();
        Log.d(TAG, "AQI = " + AQI);
        return AQI;
    }

    public double getPressure() {
        readPressure();
        Log.d(TAG, "Prssure = " + Pressure);
        return Pressure;
    }

    public int getTemperature() {
        readTemperature();
        Log.d(TAG, "Temperature = " + Temperature);
        return Temperature;
    }

    //reading from Firebase
    private void readAQI() {
        myAQIRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG,"Currently reading steps that were previously saved");

                if (dataSnapshot.getValue() != null) {
                    int read = dataSnapshot.getValue(Integer.class); //gets AQI from Firebase
                    weather.setAQI(read);
                    Log.d(TAG,"AQI dataSnapshot = " + read);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        AQI = weather.getAQI();
        Log.d(TAG,"AQI from firebase = " + AQI);
    }

    private void readPressure() {
        myPressureRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG,"Currently reading steps that were previously saved");

                if (dataSnapshot.getValue() != null) {
                    double read = dataSnapshot.getValue(Double.class); //gets Pressure from Firebase
                    weather.setPressure(read);
                    Log.d(TAG,"AQI dataSnapshot = " + read);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        Pressure = weather.getPressure();
        Log.d(TAG,"Pressure from firebase = " + Pressure);
    }

    private void readTemperature() {
        myTemperatureRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG,"Currently reading steps that were previously saved");

                if (dataSnapshot.getValue() != null) {
                    int read = dataSnapshot.getValue(Integer.class); //gets Temperature from Firebase
                    weather.setTemperature(read);
                    Log.d(TAG,"Temperature dataSnapshot = " + read);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        Temperature = weather.getTemperature();
        Log.d(TAG,"Temperature from firebase = " + Temperature);
    }

    private void getAQIPercentage() {
        String status, status2 = "";
        int progress;
        if (AQI <= 50) {
            status = "Good";
        } else if (AQI <= 100) {
            status = "Moderate";
        } else if (AQI <= 150) {
            status = "Unhealthy";
            status2 += "Active children and adults, and people with respiratory disease, such as asthma, should limit prolonged outdoor exertion.";
        } else if (AQI <= 200) {
            status = "Unhealthy";
            status2 += "Active children and adults, and people with respiratory disease, such as asthma, should avoid prolonged outdoor exertion.";
        } else if (AQI <= 300) {
            status = "Very Unhealthy";
            status2 += "Active children and adults, and people with respiratory disease, such as asthma, should avoid all outdoor exertion.";
        } else {
            status = "Hazardous";
            status2 += "Everyone should avoid all outdoor exertion.";
        }

        double progr = AQI / 3.5;
        Log.e("Weather", "Progress unaltered = " + progr);
        progress = 100 - (int) progr;
        //progress = AQI;
        Log.e("Weather", "Progress altered = " + progress);

        ProgressAnimation anim = new ProgressAnimation(MainActivity.arcProgressBar, 0, progress);
        anim.setDuration(1000);
        MainActivity.arcProgressBar.startAnimation(anim);

        MainActivity.arcProgressBar.setProgress(progress);
        MainActivity.arcProgressBar.setBottomText(status);
        MainActivity.arcProgressTV.setText(status2);
    }
}
