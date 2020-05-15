package pam.poluxion.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import pam.poluxion.MainActivity;
import pam.poluxion.R;
import pam.poluxion.data.LocalData;

@SuppressLint("Registered")
public class MainHelper extends MainActivity {

    private static final String TAG = "MainHelper";

    private static final float DEFAULT_ZOOM = 15f;

    private GoogleMap mMap;
    private LatLng current;    //current position of the device

    private Context context;
    private Activity activity;
    private Intent intent;

    public MainHelper(Context context, Activity activity, Intent intent) {
        super();
        this.context = context;
        this.activity = activity;
        this.intent = intent;

        //getLocationPermission();
    }

    //gets the current position of the device
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the device's current location");

        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        try {
            if (mLocationPermissionGranted) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();

                            current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            moveCamera(current);
                            getAddress(currentLocation.getLatitude(), currentLocation.getLongitude()); //current
                            //getAddress(40.4450809,-3.6879313); //Madrid
                            //getAddress(38.89378,-77.1546629); //Washington DC
                            //getAddress(-37.818036,144.9708628); //Melbourne
                            //getAddress(48.2083972,16.3673826); //Vienna
                            //getAddress(28.6139, 77.2090); //New Delhi
                            //getAddress(30.5981739,114.2393787); //Wuhan
                            //getAddress(40.7105934,-73.8811862); //New York
                            //getAddress(39.940618,116.3661332); //Beijing
                            //getAddress(46.7727287,23.5921197; //Cluj-Napoca
                            //getAddress(45.7505258,21.2188661); //Timisoara
                            //getAddress(50.125555,8.692777); //Frankfurt
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(context, "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: Security exception: " + e.getMessage());
        }

        if (Objects.equals(intent.getStringExtra("Msg"), "Just started")) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    crossfade(MainActivity.mainScroll);
                }
            }, 2000);   //2 seconds
        } else {
            MainActivity.loadingPanel.setVisibility(View.GONE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    MainActivity.mainScroll.setVisibility(View.VISIBLE);
                }
            }, 1000);   //1 seconds
        }
    }

    public void getAddress(double lat, double lng) {
        Thread thread = new Thread();
        thread.start();
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);

            Address obj = addresses.get(0);
            String add;
            if (obj.getLocality() != null) {
                add = obj.getLocality() + ", " + obj.getCountryName();
                LocalData localData = new LocalData(obj.getLocality());
            } else {
                add = obj.getAdminArea() + ", " + obj.getCountryName();
                LocalData localData = new LocalData(obj.getAdminArea());
            }
            //Log.e("IGA", "Address : " + obj);
            MainActivity.locationTV.setText(add);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void crossfade(ViewGroup layout) {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        layout.setAlpha(0f);
        layout.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        layout.animate()
                .alpha(1f)
                .setDuration(1000)
                .setListener(null);

        MainActivity.loadingPanel.setVisibility(View.GONE);
    }

    //the map is being moved according to the current position of the device
    private void moveCamera(LatLng latLng) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + " lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MainHelper.DEFAULT_ZOOM));
    }

    //refreshes content for better accuracy
    public void content() {
        refresh();
        getDeviceLocation();
    }

    private void refresh() {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                content();
            }
        };
    }

    //all requests are being verified
    //if permissions were granted, the map is displayed
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is ready");
        mMap = googleMap;

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.mapstyle));

            if (!success) {
                Log.d(TAG, "Can't parse style properly");
            }

        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);
            mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        }
    }
}
