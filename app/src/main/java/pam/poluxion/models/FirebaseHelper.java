package pam.poluxion.models;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pam.poluxion.BuildConfig;
import pam.poluxion.MainActivity;
import pam.poluxion.R;
import pam.poluxion.widgets.ProgressAnimation;

public class FirebaseHelper {
    private static final String TAG ="FirebaseHelper";

    private DatabaseReference myRef;

    private int AQI, Temperature;
    private double Pressure, NO2, SO2, CO, CO2, O3, VOC, Pb, PM10, PM25, PM1, NH3;
    private int weight, height, steps;
    private Weather weather;

    private SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");  //formatting the current date into a dd-MM-yyyy String type

    public FirebaseHelper(Weather weather, Context context) {
        this.weather = weather;
        //gets current date which is later formatted into dd-MM-yyyy type
        //Date currentDate = Calendar.getInstance().getTime();
        //String currentFormattedDate = df.format(currentDate);

        //stores a Firebase reference of the steps made during the current date, the weight and the height of the user
        FirebaseDatabase database = FirebaseDatabase.getInstance();  //stores Firebase instance into database variable
        String FirebaseUrl = context.getString(R.string.firebase_database_url);
        myRef = database.getReferenceFromUrl(FirebaseUrl);
        //myRef = database.getReferenceFromUrl("https://poluxion-90559.firebaseio.com//" + currentFormattedDate);
        //myRef.setValue(currentFormattedDate);
    }

    //Write in Firebase
    public void inputAQI(String aqi) {
        AQI = Integer.parseInt(aqi);
        myRef.child("AQI").setValue(AQI); //stores AQI into Firebase
        MainActivity.nrAqiTV.setText(aqi);
        weather.setAQI(AQI);
        Log.e(TAG,"AQI data set = " + AQI);
        getAQIPercentage();
    }
    public void inputPressure(String pressure) {
        Pressure = Double.parseDouble(pressure);
        myRef.child("Pressure").setValue(Pressure); //stores pressure into Firebase
        MainActivity.pressureTV.setText(pressure + " ");
        weather.setPressure(Pressure);
        Log.e(TAG,"Pressure data set = " + Pressure);
    }
    public void inputTemperature(String temperature) {
        double temp = Double.parseDouble(temperature);
        Log.e(TAG,temp + "");
        Temperature = (int) Math.floor(temp);
        myRef.child("Temperature").setValue(Temperature); //stores temperature into Firebase
        MainActivity.temperatureTV.setText(Temperature + " ");
        weather.setTemperature(Temperature);
        Log.e(TAG,"Temperature data set = " + Temperature);
    }
    public void inputNO2(String no2) {
        if(no2 != null) {
            NO2 = Double.parseDouble(no2);
            MainActivity.no2Btn.setVisibility(View.VISIBLE);
            MainActivity.no2Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.measurementTV.setVisibility(View.VISIBLE);
                    MainActivity.unitsTV.setVisibility(View.VISIBLE);
                    MainActivity.measurementTV.setText(NO2 + " ");
                    MainActivity.unitsTV.setText("μg/m³");
                    Log.e("IAQI", "no2Btn clicked");
                }
            });
        } else {
            NO2 = 0;
            MainActivity.btnSlider.removeView(MainActivity.no2Btn);
            Log.e(TAG,"NO2 does not exist.");
        }
        myRef.child("_IAQI-NO2").setValue(NO2); //stores NO2 into Firebase
        weather.setNO2(NO2);
        Log.e(TAG,"NO2 data set = " + NO2);
    }
    public void inputSO2(String so2) {
        if(so2 != null) {
            SO2 = Double.parseDouble(so2);
            MainActivity.so2Btn.setVisibility(View.VISIBLE);
            MainActivity.so2Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.measurementTV.setVisibility(View.VISIBLE);
                    MainActivity.unitsTV.setVisibility(View.VISIBLE);
                    MainActivity.measurementTV.setText(SO2 + " ");
                    MainActivity.unitsTV.setText("μg/m³");
                    Log.e("IAQI", "so2Btn clicked");
                }
            });
        } else {
            SO2 = 0;
            MainActivity.btnSlider.removeView(MainActivity.so2Btn);
            Log.e(TAG,"SO2 does not exist.");
        }
        myRef.child("_IAQI-SO2").setValue(SO2); //stores SO2 into Firebase
        weather.setSO2(SO2);
        Log.e(TAG,"SO2 data set = " + SO2);
    }
    public void inputPM10(String pm10) {
        if(pm10 != null) {
            PM10 = Double.parseDouble(pm10);
            MainActivity.pm10Btn.setVisibility(View.VISIBLE);
            MainActivity.pm10Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.measurementTV.setVisibility(View.VISIBLE);
                    MainActivity.unitsTV.setVisibility(View.VISIBLE);
                    MainActivity.measurementTV.setText(PM10 + " ");
                    MainActivity.unitsTV.setText("μg/m³");
                    Log.e("IAQI", "pm10Btn clicked");
                }
            });
        } else {
            PM10 = 0;
            MainActivity.btnSlider.removeView(MainActivity.pm10Btn);
            Log.e(TAG,"PM1 does not exist.");
        }
        myRef.child("_IAQI-PM10").setValue(PM10); //stores PM10 into Firebase

        weather.setPM10(PM10);
        Log.e(TAG,"PM10 data set = " + PM10);
    }
    public void inputO3(String o3) {
        if(o3 != null) {
            O3 = Double.parseDouble(o3);
            MainActivity.o3Btn.setVisibility(View.VISIBLE);
            MainActivity.o3Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.measurementTV.setVisibility(View.VISIBLE);
                    MainActivity.unitsTV.setVisibility(View.VISIBLE);
                    MainActivity.measurementTV.setText(O3 + " ");
                    MainActivity.unitsTV.setText("μg/m³");
                    Log.e("IAQI", "o3Btn clicked");
                }
            });
        } else {
            O3 = 0;
            MainActivity.btnSlider.removeView(MainActivity.o3Btn);
            Log.e(TAG,"O3 does not exist.");
        }
        myRef.child("_IAQI-O3").setValue(O3); //stores O3 into Firebase
        weather.setO3(O3);
        Log.e(TAG,"O3 data set = " + O3);
    }
    public void inputPM25(String pm25) {
        if(pm25 != null) {
            PM25 = Double.parseDouble(pm25);
            MainActivity.pm25Btn.setVisibility(View.VISIBLE);
            MainActivity.pm25Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.measurementTV.setVisibility(View.VISIBLE);
                    MainActivity.unitsTV.setVisibility(View.VISIBLE);
                    MainActivity.measurementTV.setText(PM25 + " ");
                    Log.e("IAQI", "pm25Btn clicked");
                }
            });
        } else {
            //PM25 = (1310.7 + 0.567*PM10)/1000;
            //String temp = new DecimalFormat("#.##").format(PM25);
            //PM25 = Double.parseDouble(temp);
            PM25 = 0;
            MainActivity.btnSlider.removeView(MainActivity.pm25Btn);
            Log.e(TAG,"PM25 does not exist.");
        }
        myRef.child("_IAQI-PM25").setValue(PM25); //stores PM2.5 into Firebase
        weather.setPM25(PM25);
        Log.e(TAG,"PM25 data set = " + PM25);
    }
    public void inputPM1(String pm1) {
        if(pm1 != null) {
            PM1 = Double.parseDouble(pm1);
            MainActivity.pm1Btn.setVisibility(View.VISIBLE);
            MainActivity.pm1Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.measurementTV.setVisibility(View.VISIBLE);
                    MainActivity.unitsTV.setVisibility(View.VISIBLE);
                    MainActivity.measurementTV.setText(PM1 + " ");
                    Log.e("IAQI", "pm1Btn clicked");
                }
            });
        } else {
            PM1 = 0;
            MainActivity.btnSlider.removeView(MainActivity.pm1Btn);
            Log.e(TAG,"PM1 does not exist.");
        }
        myRef.child("_IAQI-PM1").setValue(PM1); //stores PM1 into Firebase
        weather.setPM1(PM1);
        Log.e(TAG,"PM1 data set = " + PM1);
    }
    public void inputNH3(String nh3) {
        if(nh3 != null) {
            NH3 = Double.parseDouble(nh3);
            MainActivity.nh3Btn.setVisibility(View.VISIBLE);
            MainActivity.nh3Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.measurementTV.setVisibility(View.VISIBLE);
                    MainActivity.unitsTV.setVisibility(View.VISIBLE);
                    MainActivity.measurementTV.setText(NH3 + " ");
                    Log.e("IAQI", "nh3Btn clicked");
                }
            });
        } else {
            NH3 = 0;
            MainActivity.btnSlider.removeView(MainActivity.nh3Btn);
            Log.e(TAG,"NH3 does not exist.");
        }
        myRef.child("_IAQI-NH3").setValue(NH3); //stores NH3 into Firebase
        weather.setNH3(NH3);
        Log.e(TAG,"NH3 data set = " + NH3);
    }
    public void inputCO(String co) {
        if(co != null) {
            CO = Double.parseDouble(co);
            MainActivity.coBtn.setVisibility(View.VISIBLE);
            MainActivity.coBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.measurementTV.setVisibility(View.VISIBLE);
                    MainActivity.unitsTV.setVisibility(View.VISIBLE);
                    MainActivity.measurementTV.setText(CO + " ");
                    MainActivity.unitsTV.setText("μg/m³");
                    Log.e("IAQI", "coBtnv clicked");
                }
            });
        } else {
            CO = 0;
            MainActivity.btnSlider.removeView(MainActivity.coBtn);
            Log.e(TAG,"CO does not exist.");
        }
        myRef.child("_IAQI-CO").setValue(CO); //stores CO into Firebase
        weather.setCO(CO);
        Log.e(TAG,"CO data set = " + CO);
    }
    public void inputCO2(String co2) {
        if(co2 != null) {
            CO2 = Double.parseDouble(co2);
            MainActivity.co2Btn.setVisibility(View.VISIBLE);
            MainActivity.co2Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.measurementTV.setVisibility(View.VISIBLE);
                    MainActivity.unitsTV.setVisibility(View.VISIBLE);
                    MainActivity.measurementTV.setText(CO2 + " ");
                    Log.e("IAQI", "co2Btn clicked");
                }
            });
        } else {
            CO2 = 0;
            MainActivity.btnSlider.removeView(MainActivity.co2Btn);
            Log.e(TAG,"CO2 does not exist.");
        }
        myRef.child("_IAQI-CO2").setValue(CO2); //stores CO2 into Firebase
        weather.setCO2(CO2);
        Log.e(TAG,"CO2 data set = " + CO2);
    }
    public void inputVOC(String voc) {
        if(voc != null) {
            VOC = Double.parseDouble(voc);
            MainActivity.vocBtn.setVisibility(View.VISIBLE);
            MainActivity.vocBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.measurementTV.setVisibility(View.VISIBLE);
                    MainActivity.unitsTV.setVisibility(View.VISIBLE);
                    Log.e("IAQI", "vocBtn clicked");
                }
            });
        } else {
            VOC = 0;
            MainActivity.btnSlider.removeView(MainActivity.vocBtn);
            Log.e(TAG,"VOC does not exist.");
        }
        myRef.child("_IAQI-VOC").setValue(VOC); //stores VOC into Firebase
        weather.setVOC(VOC);
        Log.e(TAG,"VOC data set = " + VOC);
    }
    public void inputPb(String pb) {
        if(pb != null) {
            Pb = Double.parseDouble(pb);
            MainActivity.pbBtn.setVisibility(View.VISIBLE);
            MainActivity.pbBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.measurementTV.setVisibility(View.VISIBLE);
                    MainActivity.unitsTV.setVisibility(View.VISIBLE);
                    MainActivity.measurementTV.setText(Pb + " ");
                    MainActivity.unitsTV.setText("μg/m³");
                    Log.e("IAQI", "pbBtn clicked");
                }
            });
        } else {
            Pb = 0;
            MainActivity.btnSlider.removeView(MainActivity.pbBtn);
            Log.e(TAG,"Pb does not exist.");
        }
        myRef.child("_IAQI-Pb").setValue(Pb); //stores Pb into Firebase
        weather.setPb(Pb);
        Log.e(TAG,"Pb data set = " + Pb);
    }

    //Read from Firebase and passes the value to another object type
    public int getAQI() {
        AQI = readInt("AQI");
        Log.d(TAG, "AQI = " + AQI);
        return AQI;
    }
    public double getPressure() {
        Pressure = readDouble("Pressure");
        Log.d(TAG, "Pressure = " + Pressure);
        return Pressure;
    }
    public int getTemperature() {
        Temperature = readInt("Temperature");
        Log.d(TAG, "Temperature = " + Temperature);
        return Temperature;
    }
    public double getCO() {
        CO = readDouble("_IAQI-CO");
        Log.d(TAG, "CO = " + CO);
        return CO;
    }
    public double getCO2() {
        CO2 = readDouble("_IAQI-CO2");
        Log.d(TAG, "CO2 = " + CO2);
        return CO2;
    }
    public double getNH3() {
        NH3 = readDouble("_IAQI-NH3");
        Log.d(TAG, "NH3 = " + NH3);
        return NH3;
    }
    public double getNO2() {
        NO2 = readDouble("_IAQI-NO2");
        Log.d(TAG, "NO2 = " + NO2);
        return NO2;
    }
    public double getO3() {
        O3 = readDouble("_IAQI-O3");
        Log.d(TAG, "O3 = " + O3);
        return O3;
    }
    public double getPM1() {
        PM1 = readDouble("_IAQI-PM1");
        Log.d(TAG, "PM1 = " + PM1);
        return PM1;
    }
    public double getPM10() {
        PM10 = readDouble("_IAQI-PM10");
        Log.d(TAG, "PM10 = " + PM10);
        return PM10;
    }
    public double getPM25() {
        PM25 = readDouble("_IAQI-PM25");
        Log.d(TAG, "PM25 = " + PM25);
        return PM25;
    }
    public double getPb() {
        Pb = readDouble("_IAQI-Pb");
        Log.d(TAG, "Pb = " + Pb);
        return Pb;
    }
    public double getSO2() {
        SO2 = readDouble("_IAQI-SO2");
        Log.d(TAG, "SO2 = " + SO2);
        return SO2;
    }
    public double getVOC() {
        VOC = readDouble("_IAQI-VOC");
        Log.d(TAG, "VOC = " + VOC);
        return VOC;
    }

    private double tempDouble;
    private double readDouble(String child) {
        myRef.child(child).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG,"Currently reading");

                if (dataSnapshot.getValue() != null) {
                    double read = dataSnapshot.getValue(Double.class); //gets from Firebase
                    tempDouble = read;
                    Log.d(TAG,"DataSnapshot = " + read);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        Log.d(TAG,"From firebase = " + tempDouble);
        return tempDouble;
    }

    private int tempInt;
    private int readInt(String child) {
        myRef.child(child).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG,"Currently reading");

                if (dataSnapshot.getValue() != null) {
                    int read = dataSnapshot.getValue(Integer.class); //gets from Firebase
                    tempInt = read;
                    Log.d(TAG,"DataSnapshot = " + read);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        Log.d(TAG,"From firebase = " + tempInt);
        return tempInt;
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
