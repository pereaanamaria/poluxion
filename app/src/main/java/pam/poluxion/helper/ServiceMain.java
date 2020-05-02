package pam.poluxion.helper;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pam.poluxion.MainActivity;
import pam.poluxion.R;
import pam.poluxion.data.GeneralClass;
import pam.poluxion.data.Splash;
import pam.poluxion.models.AirData;
import pam.poluxion.models.StepCounter;
import pam.poluxion.steps.StepDetector;
import pam.poluxion.steps.StepListener;

public class ServiceMain extends Service implements SensorEventListener, StepListener {

    private static final String TAG = "ServiceMain";

    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id";

    private static final int WALK_INSIDE = 0;
    private static final int WALK_OUTSIDE = 1;
    private static final int RUN_INSIDE = 2;
    private static final int RUN_OUTSIDE = 3;

    private StepDetector stepDetector;
    private StepCounter stepCounter = GeneralClass.getStepCounterObject();
    private AirData airData = GeneralClass.getAirData();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getSavedData();
        startStepCounting();
        startForeground();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground() {
        GeneralClass.setStepCounter(stepCounter);
        createNotificationChannel();
        displayNotification();
        airData = GeneralClass.getAirData();
    }


    private void startStepCounting() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_FASTEST);

        //creates a StepDetector for counting steps
        stepDetector = new StepDetector();
        stepDetector.registerListener(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getSavedData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveData();
    }

    //step counting
    @Override
    public void step(long timeNs) {
        stepCounter.countTotal(WALK_INSIDE);

        if(stepCounter.getSteps() % 50 == 0 && stepCounter.getSteps() != 0) {
            saveData();
        }

        displayNotification();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //if accelerometer sensor's state changes, a possible new step is being detected
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            stepDetector.updateAccel(event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void createNotificationChannel() {
        getSavedData();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Personal Notifications";
            String description = "Include all the personal notificationa";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(NOTIF_CHANNEL_ID,name,importance);
            notificationChannel.setDescription(description);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.WHITE);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void displayNotification() {
        startForeground(NOTIF_ID, getChannelNotification().build());
        Log.d(TAG, "Steps : " + stepCounter.getSteps());
    }

    private NotificationCompat.Builder getChannelNotification() {
        Intent resultIntent = new Intent(this, Splash.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
                .setContentTitle("AQI : " + airData.getAQI())
                .setContentText("Steps : " + stepCounter.getSteps())
                .setSmallIcon(R.drawable.poluxion)
                .setAutoCancel(true)
                .setColor(Color.parseColor("#4b5949"))
                .setContentIntent(resultPendingIntent);
    }


    private void getSavedData() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPref",MODE_PRIVATE);
        String date = getCurrentDate();
        String savedDate = sharedPreferences.getString("date",null);

        if(savedDate != null && savedDate.equals(date)) {
            if(sharedPreferences.getInt("stepWI", 0) > stepCounter.getStepsWalkInside()
                    || sharedPreferences.getInt("stepWO", 0) > stepCounter.getStepsWalkOutside()
                    || sharedPreferences.getInt("stepRI", 0) > stepCounter.getStepsRunInside()
                    || sharedPreferences.getInt("stepRO", 0) > stepCounter.getStepsRunOutside())
            {
                stepCounter.setStepsWalkInside(sharedPreferences.getInt("stepWI", 0));
                stepCounter.setStepsWalkOutside(sharedPreferences.getInt("stepWO", 0));
                stepCounter.setStepsRunInside(sharedPreferences.getInt("stepRI", 0));
                stepCounter.setStepsRunOutside(sharedPreferences.getInt("stepRO", 0));
            } else {
                saveData();
            }
        } else {
            stepCounter.setStepsWalkInside(0);
            stepCounter.setStepsWalkOutside(0);
            stepCounter.setStepsRunInside(0);
            stepCounter.setStepsRunOutside(0);
        }

        /*Log.e(TAG,"Retrieved data....");
        Log.e(TAG,"stepWI + stepWO + stepRI + stepRO = " + stepCounter.getStepsWalkInside() + " + " +
                stepCounter.getStepsWalkOutside() + " + " + stepCounter.getStepsRunInside() + " + " +
                stepCounter.getStepsRunOutside() + " = " + stepCounter.getSteps());*/
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPref",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString("date",getCurrentDate());
        editor.putInt("stepWI",stepCounter.getStepsWalkInside());
        editor.putInt("stepWO",stepCounter.getStepsWalkOutside());
        editor.putInt("stepRI",stepCounter.getStepsRunInside());
        editor.putInt("stepRO",stepCounter.getStepsRunOutside());
        editor.apply();

        /*Log.e(TAG,"Saved data....");
        Log.e(TAG,"stepWI + stepWO + stepRI + stepRO = " + stepCounter.getStepsWalkInside() + " + " +
                stepCounter.getStepsWalkOutside() + " + " + stepCounter.getStepsRunInside() + " + " +
                stepCounter.getStepsRunOutside() + " = " + stepCounter.getSteps());*/
    }


    private String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        return df.format(c);
    }
}
