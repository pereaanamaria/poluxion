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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DecimalFormat;

import pam.poluxion.data.GeneralClass;
import pam.poluxion.models.StepCounter;
import pam.poluxion.models.User;
import pam.poluxion.widgets.ArcProgress;
import pam.poluxion.widgets.DotSlider;
import pam.poluxion.widgets.OnSwipeTouchListener;
import pam.poluxion.widgets.ProgressAnimation;

public class TrackerActivity extends AppCompatActivity implements SensorEventListener{
    private static final String TAG = "TrackerActivity";
    private static final DecimalFormat df2 = new DecimalFormat("#.00 BPI");

    private LinearLayout sliderDots;
    private TextView walkIn, walkOut, runIn, runOut, total;
    private TextView kms, cals, walkMins, runMins;

    private StepCounter stepCounter = GeneralClass.getStepCounterObject();
    private User user = GeneralClass.getUserObject();

    private FirebaseUser firebaseUser;
    private ArcProgress exposureArc;

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
            GeneralClass.getUserObject().updateData(firebaseUser.getUid());
        }

        walkIn = findViewById(R.id.walkingInside);
        walkOut = findViewById(R.id.walkingOutside);
        runIn = findViewById(R.id.runningInside);
        runOut = findViewById(R.id.runningOutside);
        addSwipe(walkIn);
        addSwipe(walkOut);
        addSwipe(runIn);
        addSwipe(runOut);

        walkMins = findViewById(R.id.walkingTime);
        runMins = findViewById(R.id.runningTime);
        addSwipe(walkMins);
        addSwipe(runMins);

        total = findViewById(R.id.totalSteps);
        kms = findViewById(R.id.km);
        cals = findViewById(R.id.cal);
        addSwipe(total);
        addSwipe(kms);
        addSwipe(cals);


        exposureArc = findViewById(R.id.exposure_progress);
        exposureArc.setMax(100);
        addSwipe(exposureArc);

        double progress = GeneralClass.getStepCounterObject().getIntakeDose();
        int progressInt = (int) Math.floor(progress);
        double decimals = progress - Math.floor(progress);

        ProgressAnimation anim = new ProgressAnimation(exposureArc, 0, progressInt);
        anim.setDuration(500);
        exposureArc.startAnimation(anim);
        exposureArc.setSuffixText(df2.format(decimals));
        exposureArc.setProgress(progressInt);

        displayInfo();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
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

        if(stepCounter.getWalkMin() < 60) {
            walkMins.setText(stepCounter.getWalkMin() + " min ");
        } else {
            walkMins.setText(stepCounter.getWalkMin() / 60 + "h " + stepCounter.getWalkMin() % 60 + " min ");
        }
        if(stepCounter.getRunMin() < 60) {
            runMins.setText(stepCounter.getRunMin() + " min ");
        } else {
            runMins.setText(stepCounter.getRunMin() / 60 + "h " + stepCounter.getRunMin() % 60 + " min ");
        }

        walkIn.setText(stepCounter.getStepsWalkInside() + " ");
        walkOut.setText(stepCounter.getStepsWalkOutside() + " ");
        runIn.setText(stepCounter.getStepsRunInside() + " ");
        runOut.setText(stepCounter.getStepsRunOutside() + " ");

        total.setText(stepCounter.getSteps() + "");

        kms.setText(user.getKm());
        cals.setText(user.getCals());

        double progress = GeneralClass.getStepCounterObject().getIntakeDose();
        int progressInt = (int) Math.floor(progress);
        double decimals = progress - Math.floor(progress);
        exposureArc.setSuffixText(df2.format(decimals));
        exposureArc.setProgress(progressInt);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addSwipe(View view) {
        view.setOnTouchListener(new OnSwipeTouchListener(TrackerActivity.this) {
            public void onSwipeRight() {enterNewActivity(MainActivity.class);}
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
