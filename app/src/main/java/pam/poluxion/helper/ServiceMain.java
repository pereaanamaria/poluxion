package pam.poluxion.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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

import pam.poluxion.MainActivity;
import pam.poluxion.R;
import pam.poluxion.data.GeneralClass;
import pam.poluxion.data.Splash;
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
    private PendingIntent pendingIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startStepCounting();
        startForeground();
        Log.e(TAG, "Started...");

        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground() {
        createNotificationChannel();
        displayNotification();
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
        //get saved steps
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //save steps
    }

    //step counting
    @Override
    public void step(long timeNs) {
        GeneralClass.getStepCounterObject().countTotal(WALK_INSIDE);
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
        Log.d(TAG, "Steps : " + GeneralClass.getStepCounterObject().getSteps());
    }

    private NotificationCompat.Builder getChannelNotification() {
        Intent resultIntent = new Intent(this, Splash.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
                .setContentTitle("Counting steps..")
                .setContentText("Steps : " + GeneralClass.getStepCounterObject().getSteps())
                .setSmallIcon(R.drawable.poluxion)
                .setAutoCancel(true)
                .setColor(Color.parseColor("#4b5949"))
                .setContentIntent(resultPendingIntent);
    }


    /*private void getSavedData() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String date = getCurrentDate();
        String savedDate = sharedPreferences.getString("date",null);

        if(savedDate != null && savedDate.equals(date)) {
            if(sharedPreferences.getInt("stepWI", 0) > GeneralClass.getStepCounterObject().getStepsWalkInside()
                    || sharedPreferences.getInt("stepWO", 0) > GeneralClass.getStepCounterObject().getStepsWalkOutside()
                    || sharedPreferences.getInt("stepRI", 0) > GeneralClass.getStepCounterObject().getStepsRunInside()
                    || sharedPreferences.getInt("stepRO", 0) > GeneralClass.getStepCounterObject().getStepsRunOutside())
            {
                GeneralClass.getStepCounterObject().setStepsWalkInside(sharedPreferences.getInt("stepWI", 0));
                GeneralClass.getStepCounterObject().setStepsWalkOutside(sharedPreferences.getInt("stepWO", 0));
                GeneralClass.getStepCounterObject().setStepsRunInside(sharedPreferences.getInt("stepRI", 0));
                GeneralClass.getStepCounterObject().setStepsRunOutside(sharedPreferences.getInt("stepRO", 0));
            } else {
                saveData();
            }
        } else {
            GeneralClass.getStepCounterObject().setStepsWalkInside(0);
            GeneralClass.getStepCounterObject().setStepsWalkOutside(0);
            GeneralClass.getStepCounterObject().setStepsRunInside(0);
            GeneralClass.getStepCounterObject().setStepsRunOutside(0);
        }

        Log.e(TAG,"Retrieved data....");
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString("date",getCurrentDate());
        editor.putInt("stepWI",GeneralClass.getStepCounterObject().getStepsWalkInside());
        editor.putInt("stepWO",GeneralClass.getStepCounterObject().getStepsWalkOutside());
        editor.putInt("stepRI",GeneralClass.getStepCounterObject().getStepsRunInside());
        editor.putInt("stepRO",GeneralClass.getStepCounterObject().getStepsRunOutside());
        editor.apply();

        Log.e(TAG,"Saved data....");
    }


    private String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        return df.format(c);
    }*/
}
