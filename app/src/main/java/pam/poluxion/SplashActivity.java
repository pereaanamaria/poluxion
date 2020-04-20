package pam.poluxion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import pam.poluxion.models.GeneralClass;

public class SplashActivity extends Activity {
    private static final long DELAY = 1000;
    private boolean scheduled = false;
    private Timer splashTimer;

    public static GeneralClass generalClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        generalClass = new GeneralClass(this);

        splashTimer = new Timer();
        splashTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SplashActivity.this.finish();
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
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
