package pam.poluxion.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Timer;
import java.util.TimerTask;

import pam.poluxion.MainActivity;
import pam.poluxion.R;
import pam.poluxion.data.GeneralClass;

public class Splash extends Activity {

    protected final static int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    protected final static int PERMISSION_ACCESS_FINE_LOCATION = 2;
    private static final int PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 3;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final long DELAY = 2000;
    private boolean scheduled = false;
    private Timer splashTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new GeneralClass(this);

        boolean runningQOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;
        if (runningQOrLater) {
            if( PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION},PERMISSION_REQUEST_ACTIVITY_RECOGNITION);
            }
        }

        getLocationPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_ACCESS_FINE_LOCATION) {
            start();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //gets permission to use GPS and Location
    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getApplicationContext(),FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {   //checks if FINE_LOCATION permission was granted
            if (ContextCompat.checkSelfPermission(getApplicationContext(),COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {   //checks if COARSE_LOCATION permission was granted
                start();
            } else {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_ACCESS_COARSE_LOCATION);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ACCESS_FINE_LOCATION);
        }
    }

    private void start() {
        splashTimer = new Timer();
        splashTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Splash.this.finish();
                Intent intent = new Intent(Splash.this, MainActivity.class);
                intent.putExtra("Msg", "Just started");
                startActivity(intent);
            }
        }, DELAY);
        scheduled = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scheduled)
            splashTimer.cancel();
        splashTimer.purge();
    }
}
