package pam.poluxion;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import pam.poluxion.data.GeneralClass;
import pam.poluxion.models.User;
import pam.poluxion.widgets.DotSlider;
import pam.poluxion.widgets.OnSwipeTouchListener;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private LinearLayout sliderDots;

    private User user = GeneralClass.getUserObject();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sliderDots = (LinearLayout) findViewById(R.id.sliderDot);
        createDotSlider();

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.settings);
        addSwipe(relativeLayout);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //Log.e(TAG,"Auth id : " + mAuth.getCurrentUser().getUid());
        //Log.e(TAG,"User id : " + user.getID());

        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //user.logout();
                mAuth.signOut();
                Toast.makeText(SettingsActivity.this, "Signed out.", Toast.LENGTH_SHORT).show();

                enterNewActivity(LoginActivity.class);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addSwipe(View view) {
        view.setOnTouchListener(new OnSwipeTouchListener(SettingsActivity.this) {
            public void onSwipeRight() {
                enterNewActivity(TrackerActivity.class);
            }
            public void onSwipeLeft() {
                enterNewActivity(MainActivity.class);
            }
        });
    }

    private void enterNewActivity(Class activityClass) {
        SettingsActivity.this.finish();
        Intent intent = new Intent(SettingsActivity.this, activityClass);
        intent.putExtra("Msg", "Left Activity");
        startActivity(intent);
    }

    private void createDotSlider() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        new DotSlider(this,width,sliderDots,0);
    }
}
