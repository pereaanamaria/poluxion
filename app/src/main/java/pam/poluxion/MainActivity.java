package pam.poluxion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import pam.poluxion.helper.MainActivityHelper;
import pam.poluxion.models.GeneralClass;
import pam.poluxion.models.User;
import pam.poluxion.widgets.ArcProgress;
import pam.poluxion.widgets.DotSlider;
import pam.poluxion.widgets.OnSwipeTouchListener;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MainActivity";

    public static TextView locationTV, temperatureTV, nrAqiTV, pressureTV,arcProgressTV;
    public static ArcProgress arcProgressBar;
    public static Button pm10Btn, pm25Btn, pm1Btn, no2Btn, nh3Btn, coBtn, co2Btn, o3Btn, so2Btn, vocBtn, pbBtn;
    public static TextView measurementTV, unitsTV;
    public static LinearLayout all, btnSlider;
    private LinearLayout sliderDots;
    public static ScrollView mainScroll;
    public static RelativeLayout main, loadingPanel;

    private Intent intent = new Intent();

    private MainActivityHelper mainActivityHelper;
    private User user = GeneralClass.getUserObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainScroll = (ScrollView) findViewById(R.id.mainScroll);
        loadingPanel = (RelativeLayout) findViewById(R.id.loadingPanel);
        main = (RelativeLayout) findViewById(R.id.main);
        all = (LinearLayout) findViewById(R.id.layout_all);
        sliderDots = (LinearLayout) findViewById(R.id.sliderDot);
        LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
        LinearLayout headerLayout = (LinearLayout) findViewById(R.id.headerLayout);
        LinearLayout nameLayout = (LinearLayout) findViewById(R.id.nameLayout);
        LinearLayout dataLayout = (LinearLayout) findViewById(R.id.dataLayout);
        btnSlider = (LinearLayout) findViewById(R.id.btnSlider);
        btnSlider.setFadingEdgeLength(500);

        addSwipe(mainScroll);
        addSwipe(loadingPanel);
        addSwipe(main);
        addSwipe(all);
        addSwipe(sliderDots);
        addSwipe(buttonLayout);
        addSwipe(headerLayout);
        addSwipe(nameLayout);
        addSwipe(dataLayout);

        intent = getIntent();
        mainActivityHelper = new MainActivityHelper(this,this,intent);

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

        //viewSlider = findViewById(R.id.viewDotSlider);

        initMap();  //initialises map
    }

    private void createDotSlider() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        Log.e(TAG,"Width = " + width);

        DotSlider dotSlider = new DotSlider(this,width,sliderDots,1);
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

    @SuppressLint("ClickableViewAccessibility")
    private void addSwipe(View view) {
        view.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
                //Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                if(user.checkIfLogged()) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    //intent.putExtra(EXTRA_MESSAGE, message);
                    startActivity(intent);
                    //Toast.makeText(MainActivity.this, "SettingsActivity", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    //intent.putExtra(EXTRA_MESSAGE, message);
                    startActivity(intent);
                    //Toast.makeText(MainActivity.this, "LoginActivity", Toast.LENGTH_SHORT).show();
                }
            }
            public void onSwipeLeft() {
                Intent intent = new Intent(MainActivity.this, TrackerActivity.class);
                //intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
                //Toast.makeText(MainActivity.this, "TrackerActivity", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                //Toast.makeText(MainActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
