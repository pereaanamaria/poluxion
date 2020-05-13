package pam.poluxion.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.Serializable;
import java.util.ArrayList;

public class LocationService extends Service {

    private final String TAG = "LocationService";

    private LocationManager locationManager;
    private boolean locationStarted = false;
    GnssStatus.Callback gnssStatusListener = null;

    public LocationService() {
    }

    class LocalBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

    private IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service bound");
        if (!locationStarted) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }

            if (Build.VERSION.SDK_INT <= 23) {
                GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
                    @Override
                    public void onGpsStatusChanged(int event) {
                        Log.d(TAG, "GpsListener: Status changed to " + event);
                        switch (event) {
                            case GpsStatus.GPS_EVENT_STARTED:
                                Log.d(TAG, "GPS_EVENT_STARTED");
                                break;
                            case GpsStatus.GPS_EVENT_STOPPED:
                                Log.d(TAG, "GPS_EVENT_STOPPED");
                                break;

                            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                                try {
                                    GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                                    ArrayList<SatelliteStatus> list = new ArrayList<SatelliteStatus>();
                                    assert gpsStatus != null;
                                    for (GpsSatellite status : gpsStatus.getSatellites()) {
                                        list.add(new SatelliteStatus(status.getPrn(), status.usedInFix(),
                                                status.getAzimuth(), status.getElevation(), status.getSnr(),
                                                0));
                                    }
                                    updateSatellitesStatus(list);
                                } catch (SecurityException e) {
                                    Log.e(TAG, e.toString());
                                }
                                break;
                            case GpsStatus.GPS_EVENT_FIRST_FIX:
                                Log.d(TAG, "GPS_EVENT_FIRST_FIX");
                                break;
                        }
                    }
                };
                if (!locationManager.addGpsStatusListener(gpsStatusListener))
                    Log.d(TAG, "addGpsStatusListener - error");
                locationStarted = true;
            } else {
                gnssStatusListener = new GnssStatus.Callback() {
                    @Override
                    public void onSatelliteStatusChanged(GnssStatus status) {
                        int satelliteCount = status.getSatelliteCount();
                        ArrayList<SatelliteStatus> list = new ArrayList<SatelliteStatus>();
                        Log.d(TAG, "onSatelliteStatusChanged");
                        for (int i = 0; i < satelliteCount; i++) {
                            if (status.getConstellationType(i) == GnssStatus.CONSTELLATION_GPS ||
                                    status.getConstellationType(i) == GnssStatus.CONSTELLATION_GLONASS ||
                                    status.getConstellationType(i) == GnssStatus.CONSTELLATION_GALILEO) {
                                SatelliteStatus satelliteStatus = new SatelliteStatus(
                                        status.getSvid(i), status.usedInFix(i), status.getAzimuthDegrees(i),
                                        status.getElevationDegrees(i), status.getCn0DbHz(i),
                                        status.getConstellationType(i));
                                list.add(satelliteStatus);
                                //Log.d(TAG, satelliteStatus.toString());
                            }
                        }
                        updateSatellitesStatus(list);
                    }
                };
                if (!locationManager.registerGnssStatusCallback(gnssStatusListener))
                    Log.d(TAG, "registerGnssStatusCallback - error");
                locationStarted = true;
            }

        }
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 10000, 10f,
                    mLocationListeners[0]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (Build.VERSION.SDK_INT >= 24) {
            if (gnssStatusListener != null) {
                locationManager.unregisterGnssStatusCallback(gnssStatusListener);
            }
        }
        locationManager.removeUpdates(mLocationListeners[0]);
        return true;
    }

    public final static String LOCATION_STATUS = "LocationStatus";
    public final static String LOCATION_DATA = "DATA";
    public final static String SATELLITES_STATUS = "SatellitesStatus";
    public final static String SATELLITES_DATA = "DATA";

    public void updateLocationStatus(Location location) {
        Intent localIntent =
                new Intent(LOCATION_STATUS)
                        // Puts the status into the Intent
                        .putExtra(LOCATION_DATA, (Parcelable) location);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    public void updateSatellitesStatus(ArrayList<SatelliteStatus> satellites) {
        Intent localIntent =
                new Intent(SATELLITES_STATUS)
                        // Puts the status into the Intent
                        .putExtra(SATELLITES_DATA, (Serializable) satellites);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            updateLocationStatus(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER)
    };

}
