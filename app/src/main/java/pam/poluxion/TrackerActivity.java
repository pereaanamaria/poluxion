package pam.poluxion;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pam.poluxion.data.GeneralClass;
import pam.poluxion.models.LoginActivity;
import pam.poluxion.models.StepCounter;
import pam.poluxion.models.User;
import pam.poluxion.widgets.DotSlider;
import pam.poluxion.widgets.OnSwipeTouchListener;

public class TrackerActivity extends AppCompatActivity implements SensorEventListener{
    private static final String TAG = "TrackerActivity";

    private LinearLayout sliderDots;
    private TextView walkIn, walkOut, runIn, runOut, total, bpi, kms, cals;

    private StepCounter stepCounter = GeneralClass.getStepCounterObject();
    private User user = GeneralClass.getUserObject();

    private FirebaseUser firebaseUser;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(TrackerActivity.this,sensor,SensorManager.SENSOR_DELAY_FASTEST);

        sliderDots = findViewById(R.id.sliderDot);
        createDotSlider();

        RelativeLayout relativeLayout = findViewById(R.id.tracker);
        addSwipe(relativeLayout);

        // Initialize Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser != null) {
            Log.e(TAG, "Start Updating... : " + firebaseUser.getUid());
            GeneralClass.getUserObject().updateData(firebaseUser.getUid());
            Log.e(TAG, "Updating... : " + firebaseUser.getUid());
        }

        walkIn = findViewById(R.id.walkingInside);
        walkOut = findViewById(R.id.walkingOutside);
        runIn = findViewById(R.id.runningInside);
        runOut = findViewById(R.id.runningOutside);

        total = findViewById(R.id.totalSteps);
        bpi = findViewById(R.id.bpi);
        kms = findViewById(R.id.km);
        cals = findViewById(R.id.cal);

        displayInfo();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //if accelerometer sensor's state changes, a possible new step is being detected
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            displayInfo();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void displayInfo() {
        walkIn.setText(stepCounter.getStepsWalkInside() + " ");
        walkOut.setText(stepCounter.getStepsWalkOutside() + " ");
        runIn.setText(stepCounter.getStepsRunInside() + " ");
        runOut.setText(stepCounter.getStepsRunOutside() + " ");

        total.setText(stepCounter.getSteps() + "");

        kms.setText(user.getKm());
        cals.setText(user.getCals());
        bpi.setText(user.getBPI());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addSwipe(View view) {
        view.setOnTouchListener(new OnSwipeTouchListener(TrackerActivity.this) {
            public void onSwipeRight() {
                enterNewActivity(MainActivity.class);
            }
            public void onSwipeLeft() {
                if(firebaseUser != null) {
                    enterNewActivity(SettingsActivity.class);
                } else {
                    enterNewActivity(LoginActivity.class);
                }
            }
        });
    }

    private void createDotSlider() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        new DotSlider(this,width,sliderDots,2);
    }

    private void enterNewActivity(Class activityClass) {
        TrackerActivity.this.finish();
        Intent intent = new Intent(TrackerActivity.this, activityClass);
        intent.putExtra("Msg", "Left Activity");
        startActivity(intent);
    }
}
