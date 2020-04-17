package pam.poluxion;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {
    private static final long DELAY = 3000;
    private boolean scheduled = false;
    private Timer splashTimer;
    private LinearLayout splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splash = (LinearLayout) findViewById(R.id.splash);

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
