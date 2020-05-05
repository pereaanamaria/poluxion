package pam.poluxion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pam.poluxion.helper.MainHelper;
import pam.poluxion.data.GeneralClass;
//import pam.poluxion.services.ActivityIntentService;
import pam.poluxion.services.StepsService;
import pam.poluxion.widgets.ArcProgress;
import pam.poluxion.widgets.DotSlider;
import pam.poluxion.widgets.OnSwipeTouchListener;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{//, SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @SuppressLint("StaticFieldLeak")
    public static TextView locationTV, temperatureTV, nrAqiTV, pressureTV,arcProgressTV,measurementTV,unitsTV;
    public static ArcProgress arcProgressBar;
    @SuppressLint("StaticFieldLeak")
    public static Button pm10Btn, pm25Btn, pm1Btn, no2Btn, nh3Btn, coBtn, co2Btn, o3Btn, so2Btn, vocBtn, pbBtn;
    @SuppressLint("StaticFieldLeak")
    public static LinearLayout all, btnSlider, sliderDots;
    @SuppressLint("StaticFieldLeak")
    public static ScrollView mainScroll;
    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout main, loadingPanel;

    private MainHelper mainHelper;
    private FirebaseUser firebaseUser;

    /*public static final String DETECTED_ACTIVITY = ".DETECTED_ACTIVITY";

    private ActivityRecognitionClient mActivityRecognitionClient;*/

    // Intents action that will be fired when transitions are triggered
    private final String TRANSITION_ACTION_RECEIVER =
            BuildConfig.APPLICATION_ID + "TRANSITION_ACTION_RECEIVER";
    private PendingIntent mPendingIntent;
    private myTransitionReceiver mTransitionsReceiver;

    private static String toActivityString(int activity) {
        switch (activity) {
            case DetectedActivity.STILL:
                return "STILL";
            case DetectedActivity.WALKING:
                return "WALKING";
            default:
                return "UNKNOWN";
        }
    }

    private static String toTransitionType(int transitionType) {
        switch (transitionType) {
            case ActivityTransition.ACTIVITY_TRANSITION_ENTER:
                return "ENTER";
            case ActivityTransition.ACTIVITY_TRANSITION_EXIT:
                return "EXIT";
            default:
                return "UNKNOWN";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainScroll = findViewById(R.id.mainScroll);
        loadingPanel = findViewById(R.id.loadingPanel);
        main = findViewById(R.id.main);
        all = findViewById(R.id.layout_all);
        sliderDots = findViewById(R.id.sliderDot);
        btnSlider = findViewById(R.id.btnSlider);
        btnSlider.setFadingEdgeLength(500);

        addSwipe(mainScroll);
        addSwipe(loadingPanel);
        addSwipe(main);
        addSwipe(all);
        addSwipe(sliderDots);

        addSwipe(findViewById(R.id.buttonLayout));
        addSwipe(findViewById(R.id.headerLayout));
        addSwipe(findViewById(R.id.nameLayout));
        addSwipe(findViewById(R.id.dataLayout));
        addSwipe(findViewById(R.id.divider1));
        addSwipe(findViewById(R.id.divider2));

        mainHelper = new MainHelper(this,this, getIntent());

        locationTV = findViewById(R.id.location);
        temperatureTV = findViewById(R.id.temperature);
        nrAqiTV = findViewById(R.id.nrAQI);
        pressureTV = findViewById(R.id.pressure);
        measurementTV = findViewById(R.id.measurement);
        unitsTV = findViewById(R.id.units);

        addSwipe(measurementTV);
        addSwipe(unitsTV);

        arcProgressBar = findViewById(R.id.arc_progress);
        arcProgressTV = findViewById(R.id.arc_progressTV);

        addSwipe(arcProgressBar);
        addSwipe(arcProgressTV);

        pm10Btn = findViewById(R.id.pm10);
        pm25Btn = findViewById(R.id.pm2_5);
        pm1Btn = findViewById(R.id.pm1);
        no2Btn = findViewById(R.id.no2);
        nh3Btn = findViewById(R.id.nh3);
        coBtn = findViewById(R.id.co);
        co2Btn = findViewById(R.id.co2);
        o3Btn = findViewById(R.id.o3);
        so2Btn = findViewById(R.id.so2);
        vocBtn = findViewById(R.id.voc);
        pbBtn = findViewById(R.id.pb);

        createDotSlider();

        //map is requested
        if (isServicesOK()) {
            initMap();  //initialises map
        }

        // Initialize Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser != null) {
            //Log.e(TAG, "Start Updating... : " + firebaseUser.getUid());
            GeneralClass.getUserObject().updateData(firebaseUser.getUid());
            //Log.e(TAG, "Updating... : " + firebaseUser.getUid());
        }

        startService(new Intent(getApplicationContext(), StepsService.class));
        /*ArrayList<DetectedActivity> detectedActivities = ActivityIntentService.detectedActivitiesFromJson(
                PreferenceManager.getDefaultSharedPreferences(this).getString(
                        DETECTED_ACTIVITY, ""));

        //detectedActivitiesListView.setAdapter(mAdapter);
        mActivityRecognitionClient = new ActivityRecognitionClient(this);*/

        Intent intent = new Intent(TRANSITION_ACTION_RECEIVER);
        mPendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

        mTransitionsReceiver = new myTransitionReceiver();
        registerReceiver(mTransitionsReceiver, new IntentFilter(TRANSITION_ACTION_RECEIVER));

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpTransitions();
    }

    @Override
    protected void onPause() {
        // Unregister the transitions:
        ActivityRecognition.getClient(this).removeActivityTransitionUpdates(mPendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Transitions successfully unregistered.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Transitions could not be unregistered: " + e);
                    }
                });

        super.onPause();
    }

    @Override
    protected void onStop() {
        if (mTransitionsReceiver != null) {
            unregisterReceiver(mTransitionsReceiver);
            mTransitionsReceiver = null;
        }
        super.onStop();
    }

    private void setUpTransitions(){
        List<ActivityTransition> transitions = new ArrayList<>();

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.STILL)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        ActivityTransitionRequest request = new ActivityTransitionRequest(transitions);

        // Register for Transitions Updates.
        Task<Void> task =
                ActivityRecognition.getClient(this)
                        .requestActivityTransitionUpdates(request, mPendingIntent);
        task.addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Log.i(TAG, "Transitions Api was successfully registered.");
                    }
                });
        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "Transitions Api could not be registered: " + e);
                    }
                });
    }

    public class myTransitionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!TextUtils.equals(TRANSITION_ACTION_RECEIVER, intent.getAction())){
                Log.e(TAG,"Unsupported action received in myTransitionReceiver class: action=" + intent.getAction());
                return;
            }

            if (ActivityTransitionResult.hasResult(intent)){
                ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
                for (ActivityTransitionEvent event : result.getTransitionEvents()){
                    String theActivity = toActivityString(event.getActivityType());
                    String transType = toTransitionType(event.getTransitionType());
                    Toast.makeText(context,"Transition: " + theActivity + " (" + transType + ")" ,Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        updateDetectedActivitiesList();
    }
    @Override
    protected void onPause() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
    public void requestUpdatesHandler(View view) {
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                3000,
                getActivityDetectionPendingIntent());
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                updateDetectedActivitiesList();
            }
        });
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, ActivityIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    protected void updateDetectedActivitiesList() {
        ArrayList<DetectedActivity> detectedActivities = ActivityIntentService.detectedActivitiesFromJson(
                PreferenceManager.getDefaultSharedPreferences(this)
                        .getString(DETECTED_ACTIVITY, ""));

        updateActivities(detectedActivities);
    }

    private void updateActivities(ArrayList<DetectedActivity> detectedActivities) {
        HashMap<Integer, Integer> detectedActivitiesMap = new HashMap<>();
        for (DetectedActivity activity : detectedActivities) {
            detectedActivitiesMap.put(activity.getType(), activity.getConfidence());
        }

        ArrayList<DetectedActivity> temporaryList = new ArrayList<>();
        for (int i = 0; i < ActivityIntentService.POSSIBLE_ACTIVITIES.length; i++) {
            int confidence = detectedActivitiesMap.containsKey(ActivityIntentService.POSSIBLE_ACTIVITIES[i]) ?
                    detectedActivitiesMap.get(ActivityIntentService.POSSIBLE_ACTIVITIES[i]) : 0;

            temporaryList.add(new DetectedActivity(ActivityIntentService.POSSIBLE_ACTIVITIES[i], confidence));
        }

        for (DetectedActivity detectedActivity: temporaryList) {
            Log.e(TAG,detectedActivity.toString());
            if(detectedActivity.getType() == DetectedActivity.STILL) {
                Toast.makeText(this, "Still : " + detectedActivity.getConfidence(), Toast.LENGTH_SHORT).show();
            } else if(detectedActivity.getType() == DetectedActivity.WALKING) {
                Toast.makeText(this, "Walking : " + detectedActivity.getConfidence(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(DETECTED_ACTIVITY)) {
            updateDetectedActivitiesList();
        }
    }*/

    //initialises the map
    protected void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Log.d(TAG, "initMap: Initializing map");
        mapFragment.getMapAsync(MainActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {mainHelper.onMapReady(googleMap);}


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mainHelper.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    //Google services are being checked in order to make map requests
    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOK: an error occured but it can be fixed");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void enterNewActivity(Class activityClass) {
        MainActivity.this.finish();
        Intent intent = new Intent(MainActivity.this, activityClass);
        intent.putExtra("Msg", "Left Activity");
        startActivity(intent);
    }

    private void createDotSlider() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        new DotSlider(this,width,sliderDots,1);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addSwipe(View view) {
        view.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeRight() {
                if(firebaseUser != null) {
                    enterNewActivity(SettingsActivity.class);
                } else {
                    enterNewActivity(LoginActivity.class);
                }
            }
            public void onSwipeLeft() {
                enterNewActivity(TrackerActivity.class);
            }
        });
    }
}
