package pam.poluxion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import pam.poluxion.helper.MainHelper;
import pam.poluxion.data.GeneralClass;
import pam.poluxion.helper.ServiceMain;
import pam.poluxion.models.LoginActivity;
import pam.poluxion.steps.StepDetector;
import pam.poluxion.steps.StepListener;
import pam.poluxion.widgets.ArcProgress;
import pam.poluxion.widgets.DotSlider;
import pam.poluxion.widgets.OnSwipeTouchListener;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainScroll = (ScrollView) findViewById(R.id.mainScroll);
        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        main = (RelativeLayout) findViewById(R.id.main);
        all = (LinearLayout) findViewById(R.id.layout_all);
        sliderDots = (LinearLayout) findViewById(R.id.sliderDot);
        btnSlider = (LinearLayout) findViewById(R.id.btnSlider);
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

        locationTV = (TextView) findViewById(R.id.location);
        temperatureTV = (TextView) findViewById(R.id.temperature);
        nrAqiTV = (TextView) findViewById(R.id.nrAQI);
        pressureTV = (TextView) findViewById(R.id.pressure);
        measurementTV = (TextView) findViewById(R.id.measurement);
        unitsTV = (TextView) findViewById(R.id.units);

        addSwipe(measurementTV);
        addSwipe(unitsTV);

        arcProgressBar = (ArcProgress) findViewById(R.id.arc_progress);
        arcProgressTV = (TextView) findViewById(R.id.arc_progressTV);

        addSwipe(arcProgressBar);
        addSwipe(arcProgressTV);

        pm10Btn = (Button) findViewById(R.id.pm10);
        pm25Btn = (Button) findViewById(R.id.pm2_5);
        pm1Btn = (Button) findViewById(R.id.pm1);
        no2Btn = (Button) findViewById(R.id.no2);
        nh3Btn = (Button) findViewById(R.id.nh3);
        coBtn = (Button) findViewById(R.id.co);
        co2Btn = (Button) findViewById(R.id.co2);
        o3Btn = (Button) findViewById(R.id.o3);
        so2Btn = (Button) findViewById(R.id.so2);
        vocBtn = (Button) findViewById(R.id.voc);
        pbBtn = (Button) findViewById(R.id.pb);

        createDotSlider();

        //map is requested
        if (isServicesOK()) {
            initMap();  //initialises map
        }

        // Initialize Firebase Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser != null) {
            GeneralClass.getUserObject().updateData(firebaseUser.getUid());
        }
        startService(new Intent(getApplicationContext(), ServiceMain.class));
    }

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
                    GeneralClass.getUserObject().updateData(firebaseUser.getUid());
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
