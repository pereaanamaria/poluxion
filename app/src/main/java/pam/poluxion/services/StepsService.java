package pam.poluxion.services;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

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

public class StepsService extends Service implements SensorEventListener, StepListener {

    private static final String TAG = "StepsService";
    // Intents action that will be fired when transitions are triggered
    private final String TRANSITION_ACTION_RECEIVER = BuildConfig.APPLICATION_ID + "TRANSITION_ACTION_RECEIVER";

    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id";

    private static final int WALK_INSIDE = 0;
    private static final int WALK_OUTSIDE = 1;
    private static final int RUN_INSIDE = 2;
    private static final int RUN_OUTSIDE = 3;

    private String lastRegisteredDate;

    private boolean inside = true;

    private StepDetector stepDetector;
    private StepCounter stepCounter = GeneralClass.getStepCounterObject();
    //private AirData airData = GeneralClass.getAirData();

    private myTransitionReceiver mTransitionsReceiver = new myTransitionReceiver();

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
        initList();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground() {
        GeneralClass.setStepCounter(stepCounter);
        getSavedData();
        createNotificationChannel();
        displayNotification();
        //airData = GeneralClass.getAirData();
    }


    private void startStepCounting() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert sensorManager != null;
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_FASTEST);

        //creates a StepDetector for counting steps
        stepDetector = new StepDetector();
        stepDetector.registerListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(TRANSITION_ACTION_RECEIVER);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(StepsService.this, 0, intent, 0);
        unregisterTransitionApi(pendingIntent);
        saveData();
    }

    //step counting
    @Override
    public void step(long timeNs) {
        if(!lastRegisteredDate.equals(getCurrentDate())) {
            initData();
        }

        if(mTransitionsReceiver.getDetectedActivity().equals("Walking")) {
            if(inside) {
                stepCounter.countTotal(WALK_INSIDE);
            } else {
                stepCounter.countTotal(WALK_OUTSIDE);
            }
        }

        if(mTransitionsReceiver.getDetectedActivity().equals("Running")) {
            if(inside) {
                stepCounter.countTotal(RUN_INSIDE);
            } else {
                stepCounter.countTotal(RUN_OUTSIDE);
            }
        }

        if(stepCounter.getSteps() % 100 == 0 && stepCounter.getSteps() != 0) {
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
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Personal Notifications";
            String description = "Include all the personal notificationa";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(NOTIF_CHANNEL_ID,name,importance);
            notificationChannel.setDescription(description);
            //notificationChannel.enableLights(true);
            //notificationChannel.setLightColor(Color.WHITE);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void displayNotification() {
        startForeground(NOTIF_ID, getChannelNotification().build());
    }

    private NotificationCompat.Builder getChannelNotification() {
        Intent resultIntent = new Intent(this, Splash.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 1000, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
                .setContentTitle("You are " + mTransitionsReceiver.getDetectedActivity().toLowerCase())
                .setContentText("Your steps : " + stepCounter.getSteps())
                .setSmallIcon(R.drawable.poluxion)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);
    }


    private void getSavedData() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPref",MODE_PRIVATE);
        lastRegisteredDate = sharedPreferences.getString("date",null);

        if(lastRegisteredDate != null) {
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
            initData();
        }
        Log.e(TAG,"getSavedData");
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

        Log.e(TAG,"saveData");
    }

    private void initData() {
        lastRegisteredDate = getCurrentDate();
        stepCounter.setStepsWalkInside(0);
        stepCounter.setStepsWalkOutside(0);
        stepCounter.setStepsRunInside(0);
        stepCounter.setStepsRunOutside(0);
        saveData();
    }

    private String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        return df.format(c);
    }

    private void initList() {
        List<ActivityTransition> transitions = new ArrayList<>();

        transitions.add(addTransitions(DetectedActivity.IN_VEHICLE,ActivityTransition.ACTIVITY_TRANSITION_ENTER));
        transitions.add(addTransitions(DetectedActivity.IN_VEHICLE,ActivityTransition.ACTIVITY_TRANSITION_EXIT));
        transitions.add(addTransitions(DetectedActivity.ON_BICYCLE,ActivityTransition.ACTIVITY_TRANSITION_ENTER));
        transitions.add(addTransitions(DetectedActivity.ON_BICYCLE,ActivityTransition.ACTIVITY_TRANSITION_EXIT));
        transitions.add(addTransitions(DetectedActivity.WALKING,ActivityTransition.ACTIVITY_TRANSITION_ENTER));
        transitions.add(addTransitions(DetectedActivity.WALKING,ActivityTransition.ACTIVITY_TRANSITION_EXIT));
        transitions.add(addTransitions(DetectedActivity.RUNNING,ActivityTransition.ACTIVITY_TRANSITION_ENTER));
        transitions.add(addTransitions(DetectedActivity.RUNNING,ActivityTransition.ACTIVITY_TRANSITION_EXIT));
        transitions.add(addTransitions(DetectedActivity.STILL,ActivityTransition.ACTIVITY_TRANSITION_ENTER));
        transitions.add(addTransitions(DetectedActivity.STILL,ActivityTransition.ACTIVITY_TRANSITION_EXIT));

        ActivityTransitionRequest request = new ActivityTransitionRequest(transitions);

        Intent intent = new Intent(TRANSITION_ACTION_RECEIVER);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(StepsService.this, 0, intent, 0);
        registerReceiver(mTransitionsReceiver, new IntentFilter(TRANSITION_ACTION_RECEIVER));

        registerTransitionApi(request,pendingIntent);
    }

    private void registerTransitionApi(ActivityTransitionRequest request, PendingIntent pendingIntent) {
        // pendingIntent is the instance of PendingIntent where the app receives callbacks.
        Task<Void> task = ActivityRecognition.getClient(this).requestActivityTransitionUpdates(request, pendingIntent);

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

    private void unregisterTransitionApi(final PendingIntent pendingIntent) {
        // pendingIntent is the instance of PendingIntent where the app receives callbacks.
        final Task<Void> task = ActivityRecognition.getClient(this).removeActivityTransitionUpdates(pendingIntent);

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

    private ActivityTransition addTransitions(int detectedActivity, int activityTransition) {
        return new ActivityTransition.Builder().setActivityType(detectedActivity)
                .setActivityTransition(activityTransition).build();
    }

    public class myTransitionReceiver extends BroadcastReceiver {
        private String status = "still";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!TextUtils.equals(TRANSITION_ACTION_RECEIVER, intent.getAction())){
                Log.e(TAG,"Unsupported action received in myTransitionReceiver class: action=" + intent.getAction());
                return;
            }

            Log.e(TAG,"Got here");

            if (ActivityTransitionResult.hasResult(intent)){
                ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
                assert result != null;
                for (ActivityTransitionEvent event : result.getTransitionEvents()){
                    String theActivity = toActivityString(event.getActivityType());
                    String transType = toTransitionType(event.getTransitionType());
                    if(transType.equals("ENTER")) {
                        status = theActivity;
                    }
                }
            }
        }

        public String getDetectedActivity() {
            return status;
        }

        private String toActivityString(int activity) {
            switch (activity) {
                case DetectedActivity.STILL: return "Still";
                case DetectedActivity.WALKING: return "Walking";
                case DetectedActivity.IN_VEHICLE: return "In vehicle";
                case DetectedActivity.ON_BICYCLE: return "On bicycle";
                case DetectedActivity.RUNNING: return "Running";
                default: return "Unknown";
            }
        }

        private String toTransitionType(int transitionType) {
            switch (transitionType) {
                case ActivityTransition.ACTIVITY_TRANSITION_ENTER: return "ENTER";
                case ActivityTransition.ACTIVITY_TRANSITION_EXIT: return "EXIT";
                default: return "UNKNOWN";
            }
        }
    }
}
