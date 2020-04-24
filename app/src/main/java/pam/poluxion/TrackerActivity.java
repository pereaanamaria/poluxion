package pam.poluxion;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pam.poluxion.data.FirebaseHelper;
import pam.poluxion.data.GeneralClass;
import pam.poluxion.models.User;
import pam.poluxion.widgets.DotSlider;
import pam.poluxion.widgets.OnSwipeTouchListener;

public class TrackerActivity extends AppCompatActivity {
    private static final String TAG = "TrackerActivity";

    private LinearLayout sliderDots;
    private FirebaseAuth mAuth;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        sliderDots = (LinearLayout) findViewById(R.id.sliderDot);
        createDotSlider();

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.tracker);
        addSwipe(relativeLayout);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addSwipe(View view) {
        view.setOnTouchListener(new OnSwipeTouchListener(TrackerActivity.this) {
            public void onSwipeRight() {
                enterNewActivity(MainActivity.class);
            }
            public void onSwipeLeft() {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
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
