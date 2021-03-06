package pam.poluxion.services;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import pam.poluxion.BuildConfig;
import pam.poluxion.R;
import pam.poluxion.data.GeneralClass;
import pam.poluxion.helper.Splash;
import pam.poluxion.models.StepCounter;
import pam.poluxion.steps.StepDetector;
import pam.poluxion.steps.StepListener;

import static pam.poluxion.services.LocationService.SATELLITES_DATA;

public class StepsService extends Service implements SensorEventListener, StepListener {

    private static final String TAG = "StepsService";
    // Intents action that will be fired when transitions are triggered
    private final String TRANSITION_ACTION_RECEIVER = BuildConfig.APPLICATION_ID + "TRANSITION_ACTION_RECEIVER";

    private static final DecimalFormat df2 = new DecimalFormat("0.00");

    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id";

    private static final int WALK_INSIDE = 0;
    private static final int WALK_OUTSIDE = 1;
    private static final int RUN_INSIDE = 2;
    private static final int RUN_OUTSIDE = 3;

    private String lastRegisteredDate;

    private StepDetector stepDetector;
    private StepCounter stepCounter = GeneralClass.getStepCounterObject();
    private int AQI = 0;

    //broadcast receivers
    private TransitionReceiver mTransitionsReceiver = new TransitionReceiver();
    private SatelliteStateReceiver mSatelliteStateReceiver = new SatelliteStateReceiver();

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
            LocationService serviceInstance = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {}
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {return null;}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AQI = GeneralClass.getAirData().getAQI();
        startStepCounting();
        startForeground();
        initActivityRecognition();

        // Bind to the BoundService
        Intent intentLocation = new Intent(this, LocationService.class);
        if (!bindService(intentLocation, serviceConnection, Context.BIND_AUTO_CREATE)) {
            Log.d("", "bindService Error!");
        }

        // The filter's action is BROADCAST_ACTION
        IntentFilter locationStatusIntentFilter = new IntentFilter(LocationService.LOCATION_STATUS);
        // The filter's action is BROADCAST_ACTION
        IntentFilter satelliteStatusIntentFilter = new IntentFilter(LocationService.SATELLITES_STATUS);

        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(mSatelliteStateReceiver, satelliteStatusIntentFilter);

        return START_STICKY;
    }

    private void startForeground() {
        GeneralClass.setStepCounter(stepCounter);
        getSavedData();
        createNotificationChannel();
        displayNotification();
    }

    private void startStepCounting() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert sensorManager != null;
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);

        //creates a StepDetector for counting steps
        stepDetector = new StepDetector();
        stepDetector.registerListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        saveData();
        Intent intent = new Intent(TRANSITION_ACTION_RECEIVER);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(StepsService.this, 0, intent, 0);
        unregisterTransitionApi(pendingIntent);

        unbindService(serviceConnection);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSatelliteStateReceiver);
    }

    //step counting
    @Override
    public void step(long timeNs) {
        //new day init
        if (!lastRegisteredDate.equals(getCurrentDate())) {
            initData();
        }

        //for DetectedActivity.WALKING
        if (mTransitionsReceiver.getDetectedActivity().equals("Walking")) {
            if (stepCounter.isIndoor()) {
                stepCounter.countTotal(WALK_INSIDE);
            } else {
                stepCounter.countTotal(WALK_OUTSIDE);
            }
        }

        //for DetectedActivity.RUNNING
        if (mTransitionsReceiver.getDetectedActivity().equals("Running")) {
            if (stepCounter.isIndoor()) {
                stepCounter.countTotal(RUN_INSIDE);
            } else {
                stepCounter.countTotal(RUN_OUTSIDE);
            }
        }

        //data saving
        if (stepCounter.getSteps() % 100 == 0 && stepCounter.getSteps() != 0 && AQI != 0) {
            saveData();
        }

        //notification displaying
        if(stepCounter.getSteps() % 500 == 0 && stepCounter.getSteps() != 0) {
            displayNotification();
        }

        //change notification display
        if(GeneralClass.getAirData().getAQI() != AQI && GeneralClass.getAirData().getAQI() != 0) {
            AQI = GeneralClass.getAirData().getAQI();
            displayNotification();
            saveData();
        }
    }

    //accelerometer update based on new event
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            stepDetector.updateAccel(event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Poluxion data";
            String description = "Includes AQI and BPI value";
            int importance = NotificationManager.IMPORTANCE_NONE;

            NotificationChannel notificationChannel = new NotificationChannel(NOTIF_CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void displayNotification() {
        startForeground(NOTIF_ID, getChannelNotification().build());
    }

    //notification building with description, text and icon
    private NotificationCompat.Builder getChannelNotification() {
        Intent resultIntent = new Intent(this, Splash.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 1000,
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK),
                PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
                .setContentTitle(AQI + " AQI")
                .setContentText("BPI : " + df2.format(stepCounter.getBPI()))
                .setSmallIcon(R.drawable.poluxion)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setVibrate(new long[] {0L})
                .setSound(null);
    }

    //reading saved data from shared preferences
    private void getSavedData() {
        //shared preferences file => myPref
        SharedPreferences sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);
        lastRegisteredDate = sharedPreferences.getString("date", null);

        if (lastRegisteredDate != null) {
            if (sharedPreferences.getInt("stepWI", 0) > stepCounter.getStepsWalkInside()
                    || sharedPreferences.getInt("stepWO", 0) > stepCounter.getStepsWalkOutside()
                    || sharedPreferences.getInt("stepRI", 0) > stepCounter.getStepsRunInside()
                    || sharedPreferences.getInt("stepRO", 0) > stepCounter.getStepsRunOutside()) {
                stepCounter.setStepsWalkInside(sharedPreferences.getInt("stepWI", 0));
                stepCounter.setStepsWalkOutside(sharedPreferences.getInt("stepWO", 0));
                stepCounter.setStepsRunInside(sharedPreferences.getInt("stepRI", 0));
                stepCounter.setStepsRunOutside(sharedPreferences.getInt("stepRO", 0));
            } else {
                saveData();
            }
        } else {
            //init all attributes if new day
            initData();
        }
    }

    //save data on shared preferences
    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString("date", getCurrentDate());
        editor.putInt("stepWI", stepCounter.getStepsWalkInside());
        editor.putInt("stepWO", stepCounter.getStepsWalkOutside());
        editor.putInt("stepRI", stepCounter.getStepsRunInside());
        editor.putInt("stepRO", stepCounter.getStepsRunOutside());
        editor.apply();
    }

    //new day initiation of all attributes
    private void initData() {
        lastRegisteredDate = getCurrentDate();
        stepCounter.setStepsWalkInside(0);
        stepCounter.setStepsWalkOutside(0);
        stepCounter.setStepsRunInside(0);
        stepCounter.setStepsRunOutside(0);

        saveData();
    }

    //format current date display
    private String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        return df.format(c);
    }

    //register to Activity Recognition API on start
    private void initActivityRecognition() {
        ActivityRecognitionClient request = new ActivityRecognitionClient(this);

        Intent intent = new Intent(TRANSITION_ACTION_RECEIVER);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(StepsService.this, 0, intent, 0);
        registerReceiver(mTransitionsReceiver, new IntentFilter(TRANSITION_ACTION_RECEIVER));

        registerTransitionApi(request, pendingIntent);
    }

    //registering
    private void registerTransitionApi(ActivityRecognitionClient request, PendingIntent pendingIntent) {
        // pendingIntent is the instance of PendingIntent where the app receives callbacks.
        Task<Void> task = request.requestActivityUpdates(0, pendingIntent);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.e(TAG, "Transitions Api was successfully registered.");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NotNull Exception e) {
                Log.e(TAG, "Transitions Api could not be registered: " + e);
            }
        });
    }

    //unregistering
    private void unregisterTransitionApi(final PendingIntent pendingIntent) {
        // pendingIntent is the instance of PendingIntent where the app receives callbacks.
        final Task<Void> task = ActivityRecognition.getClient(this).removeActivityUpdates(pendingIntent);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                pendingIntent.cancel();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NotNull Exception e) {
                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            }
        });
    }

    //read activity recognition result and return activity status
    private static class TransitionReceiver extends BroadcastReceiver {
        private String status = "Still";

        @Override
        public void onReceive(Context context, Intent intent) {
            // Intents action that will be fired when transitions are triggered
            String TRANSITION_ACTION_RECEIVER = BuildConfig.APPLICATION_ID + "TRANSITION_ACTION_RECEIVER";
            if (!TextUtils.equals(TRANSITION_ACTION_RECEIVER, intent.getAction())) {
                Log.e(TAG, "Unsupported action received in myTransitionReceiver class: action=" + intent.getAction());
                return;
            }

            if (ActivityRecognitionResult.hasResult(intent)) {
                int walkConfidence = 0, runConfidence = 0;
                ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

                //most probable activity
                DetectedActivity mostProbableActivity = result.getMostProbableActivity();
                int activityType = mostProbableActivity.getType();

                //UNKNOWN and ON_FOOT cases
                List<DetectedActivity> probableActivities = result.getProbableActivities();
                for(DetectedActivity activity : probableActivities) {
                    if(activity.getType() == DetectedActivity.WALKING) {
                        walkConfidence = activity.getConfidence();
                    } else if(activity.getType() == DetectedActivity.RUNNING) {
                        runConfidence = activity.getConfidence();
                    }

                }

                status = toActivityString(activityType, walkConfidence, runConfidence);
            }
        }

        //returns activity status to step counter
        public String getDetectedActivity() {return status;}

        //from activity type (int) to status (String)
        private String toActivityString(int activity, int walkConfidence, int runConfidence) {
            switch (activity) {
                case DetectedActivity.STILL: return "Still";
                case DetectedActivity.IN_VEHICLE: return "In vehicle";
                case DetectedActivity.ON_BICYCLE: return "Cycling";
                case DetectedActivity.TILTING: return "Tilting";
                case DetectedActivity.RUNNING: return "Running";
                case DetectedActivity.WALKING: return "Walking";
                default: if(walkConfidence >= runConfidence) {
                    return "Walking";
                } else {
                    return "Running";
                }
            }
        }
    }

    //verifies if user is indoor or outdoor based on satellite signal strength
    private static class SatelliteStateReceiver extends BroadcastReceiver {
        float avg1 = -1.0f, avg2 = -1.0f, avg3 = -1.0f;
        private boolean isAutoIndoor = true;

        //prevents instantiation
        public SatelliteStateReceiver() {}

        //called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<SatelliteStatus> satellites = (ArrayList<SatelliteStatus>) intent.getExtras().getSerializable(SATELLITES_DATA);
            if (satellites != null && satellites.size() > 0) {
                int cnt1 = 0;
                float max = 0.0f, avg = 0.0f;
                for (int i = 0; i < satellites.size(); i++) {
                    if (satellites.get(i).snr > 0.0f) {
                        if (max < satellites.get(i).snr) {
                            max = satellites.get(i).snr;
                        }
                        avg += satellites.get(i).snr;
                        cnt1++;
                    }
                }
                if (cnt1 > 0) {
                    avg = avg / cnt1;
                    avg1 = avg2;
                    avg2 = avg3;
                    avg3 = avg;
                    int cnt = 3;
                    if (avg1 < 0.0f) {
                        cnt--;
                    }
                    if (avg2 < 0.0f) {
                        cnt--;
                    }
                    avg = (avg1 + avg2 + avg3) / cnt;
                    if (isAutoIndoor) {
                        if (avg > 26.0f) {
                            isAutoIndoor = false;
                        }
                    } else {
                        if (avg < 24.0f) {
                            isAutoIndoor = true;
                        }
                    }
                    GeneralClass.getStepCounterObject().setIndoor(isAutoIndoor);
                }
            }
        }
    }
}
