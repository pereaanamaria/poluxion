package pam.poluxion;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pam.poluxion.data.FirebaseHelper;
import pam.poluxion.data.GeneralClass;
import pam.poluxion.models.User;
import pam.poluxion.widgets.DotSlider;
import pam.poluxion.widgets.OnSwipeTouchListener;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    private LinearLayout sliderDots;
    private ScrollView settingsLayout;
    private RelativeLayout loadingPanelSettings;
    private EditText weightET, heightET;

    private User user = GeneralClass.getUserObject();
    private double weight, height;
    private String name;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sliderDots = (LinearLayout) findViewById(R.id.sliderDot);
        createDotSlider();
        addSwipe(sliderDots);
        settingsLayout = (ScrollView) findViewById(R.id.settingsLayout);
        addSwipe(settingsLayout);
        LinearLayout allSettings = (LinearLayout) findViewById(R.id.allSettings);
        addSwipe(allSettings);
        loadingPanelSettings = (RelativeLayout) findViewById(R.id.loadingPanelSettings);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        name = user.getNameUser();
        weight = user.getWeight();
        height = user.getHeight();

        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Toast.makeText(SettingsActivity.this, "Signed out.", Toast.LENGTH_SHORT).show();

                enterNewActivity(LoginActivity.class);
            }
        });

        user.displayInfo();

        start();
    }

    private void setWeightListener() {
        FirebaseHelper firebaseHelper = GeneralClass.getFirebaseHelperObject();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        String weight = weightET.getText().toString();

        if(TextUtils.isEmpty(weight)) {
            Toast.makeText(SettingsActivity.this, "Enter weight.", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseHelper.inputDouble(firebaseUser.getUid() + "/weight",weight);
        user.setWeight(Double.parseDouble(weight));

        weightET.getText().clear();
        Toast.makeText(SettingsActivity.this, "Weight value has been set.", Toast.LENGTH_SHORT).show();
    }

    private void setHeightListener() {
        FirebaseHelper firebaseHelper = GeneralClass.getFirebaseHelperObject();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        String height = heightET.getText().toString();

        if(TextUtils.isEmpty(height)) {
            Toast.makeText(SettingsActivity.this, "Enter height.", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseHelper.inputDouble(firebaseUser.getUid() + "/height", height);
        user.setHeight(Double.parseDouble(height));

        heightET.getText().clear();
        Toast.makeText(SettingsActivity.this, "Height value has been set.", Toast.LENGTH_SHORT).show();
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

        loadingPanelSettings.setVisibility(View.GONE);
    }

    private void start() {
        TextView helloTV = (TextView) findViewById(R.id.helloText);
        String str = "Hello, " + name + "! Please enter your weight and height.";
        helloTV.setText(str);

        weightET = (EditText) findViewById(R.id.weight);
        weightET.setHint(weight + " kg");

        Button weightBtn = (Button) findViewById(R.id.btnSaveWeight);
        weightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setWeightListener();
            }
        });

        heightET = (EditText) findViewById(R.id.height);
        heightET.setHint(height + " cm");

        Button heightBtn = (Button) findViewById(R.id.btnSaveHeight);
        heightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHeightListener();
            }
        });

        try {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    crossfade(settingsLayout);
                }
            }, 1000);   //1 seconds
        } catch (Exception e) {
            Toast.makeText(this, "Could not get key", Toast.LENGTH_SHORT).show();
        }
    }
}
