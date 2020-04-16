package pam.poluxion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import pam.poluxion.helper.MainActivityHelper;
import pam.poluxion.widgets.ArcProgress;
import pam.poluxion.widgets.DotSlider;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MainActivity";

    public static TextView locationTV, temperatureTV, nrAqiTV, pressureTV,arcProgressTV;
    public static ArcProgress arcProgressBar;
    public static Button pm10Btn, pm25Btn, pm1Btn, no2Btn, nh3Btn, coBtn, co2Btn, o3Btn, so2Btn, vocBtn, pbBtn;
    public static TextView measurementTV, unitsTV;
    public static LinearLayout sliderDots, splash,all;
    public static LinearLayout btnSlider;

    private MainActivityHelper mainActivityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivityHelper = new MainActivityHelper(this,this);

        locationTV = (TextView) findViewById(R.id.location);
        temperatureTV = (TextView) findViewById(R.id.temperature);
        nrAqiTV = (TextView) findViewById(R.id.nrAQI);
        pressureTV = (TextView) findViewById(R.id.pressure);
        measurementTV = (TextView) findViewById(R.id.measurement);
        unitsTV = (TextView) findViewById(R.id.units);

        arcProgressBar = (ArcProgress) findViewById(R.id.arc_progress);
        arcProgressTV = (TextView) findViewById(R.id.arc_progressTV);

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

        splash = (LinearLayout) findViewById(R.id.layout_splash);
        all = (LinearLayout) findViewById(R.id.layout_all);
        btnSlider = (LinearLayout) findViewById(R.id.btnSlider);
        btnSlider.setFadingEdgeLength(500);

        sliderDots = (LinearLayout) findViewById(R.id.sliderDot);
        createDotSlider();

        initMap();  //initialises map
    }

    private void createDotSlider() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        Log.e(TAG,"Width = " + width);

        DotSlider dotSlider = new DotSlider(this, width,1);
    }

    //initialises the map
    protected void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Log.d(TAG, "initMap: Initializing map");
        mapFragment.getMapAsync(MainActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mainActivityHelper.onMapReady(googleMap);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mainActivityHelper.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

}
