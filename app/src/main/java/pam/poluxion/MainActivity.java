package pam.poluxion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import pam.poluxion.models.Weather;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MainActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final float DEFAULT_ZOOM = 15f;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private boolean mLocationPermissionGranted = false;   //location permission flag (false by default)
    private GoogleMap mMap;
    private LatLng current;    //current position of the device

    public static TextView locationTV, temperatureTV, nrAqiTV, pressureTV;

    private LinearLayout sliderDots;
    private ImageView[] dots;
    private int[] layouts = {R.layout.activity_main,R.layout.activity_tracker,R.layout.activity_settings};

    private Weather weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLocationPermission();   //sets and gets the location permission flag

        locationTV = (TextView) findViewById(R.id.location);
        temperatureTV = (TextView) findViewById(R.id.temperature);
        nrAqiTV = (TextView) findViewById(R.id.nrAQI);
        pressureTV = (TextView) findViewById(R.id.pressure);

        /*if(Build.VERSION.SDK_INT>=19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }*/

        sliderDots = (LinearLayout) findViewById(R.id.sliderDot);
        createDots(1);

        initMap();  //initialises map
    }

    private void createDots(int currentPosition) {
        if(sliderDots != null) {
            sliderDots.removeAllViews();
        }

        dots = new ImageView[layouts.length];

        for(int i=0; i<layouts.length; i++) {
            dots[i] = new ImageView(this);
            if(i == currentPosition) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.active_dot));
            } else {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this,R.drawable.inactive_dot));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4,0,4,0);
            sliderDots.addView(dots[i],params);

        }
    }

    //gets the current position of the device
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the device's current location");

        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if(mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();

                            current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            getAddress(currentLocation.getLatitude(), currentLocation.getLongitude());
                            moveCamera(current,DEFAULT_ZOOM);

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MainActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: Security exception: " + e.getMessage());
        }
    }

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getLocality() + ", " + obj.getCountryName();

            weather = new Weather(obj.getLocality(),obj.getCountryName(),this);

            try {
                //Toast.makeText(MainActivity.this, weather.getKey(), Toast.LENGTH_SHORT).show();
                weather.getAQI();
                weather.getPressure();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Could not get key", Toast.LENGTH_SHORT).show();
            }
            temperatureTV.setText(weather.getTemperature());


            Log.v("IGA", "Address" + add);

            /*urlSearch = "http://dataservice.accuweather.com/locations/v1/cities/search?apikey=xhBwZVKeHlKIRrXCgwoVQoZF8ZWytkTa&q=" + obj.getLocality() + "%2C" + obj.getCountryName() + "&details=true&alias=always";

            URL url  = new URL(urlSearch);

            URLConnection urlC = url.openConnection();
            // Copy resource to local file, use remote file
            // if no local file name specified
            InputStream is = url.openStream();

            FileOutputStream fos = new FileOutputStream("weatherUnits//trial.JSON");

            int oneChar, count=0;
            while ((oneChar=is.read()) != -1)
            {
                fos.write(oneChar);
                count++;
            }
            is.close();
            fos.close();*/

            locationTV.setText(add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //the map is being moved according to the current position of the device
    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + " lng: " + latLng.longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    //refreshes content every 200ms for a better
    public void content() {
        refresh(200);
        getDeviceLocation();
    }

    private void refresh(int milisecs) {
        final Handler handler = new Handler();
        final Runnable runnable =  new Runnable() {
            @Override
            public void run() {
                content();
            }
        };
    }

    //all requests are being verified
    //if permissions were granted, the map is displayed
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is ready");
        mMap = googleMap;

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle));

            if (!success) {
                Log.d(TAG, "Can't parse style properly");
            }

        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);
            mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        }
    }

    //initialises the map
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Log.d(TAG, "initMap: Initializing map");
        mapFragment.getMapAsync(MainActivity.this);
    }

    /*@Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }*/

    //gets permission to use GPS and Location
    private void getLocationPermission(){
        String[] permissions = {FINE_LOCATION,COARSE_LOCATION};

        Log.d(TAG, "getLocationPermissions: Getting location permissions");
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {   //checks if FINE_LOCATION permission was granted
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {   //checks if COARSE_LOCATION permission was granted
                mLocationPermissionGranted = true;    //sets the flag
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }  else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //requests permission to access LOCATION
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult: Called");

        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if(grantResults.length > 0) {
                    for(int i=0; i<grantResults.length; i++) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionResult: Permission failed");
                            return;
                        }
                    }

                    mLocationPermissionGranted = true;
                    Log.d(TAG, "onRequestPermissionResult: Permission granted");
                    initMap();
                }
            }
        }
    }
}
