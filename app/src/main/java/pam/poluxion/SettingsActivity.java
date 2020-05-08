package pam.poluxion;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pam.poluxion.helper.FirebaseHelper;
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
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser != null) {
            Log.e(TAG,"userID = " + firebaseUser.getUid());
            GeneralClass.getUserObject().updateData(firebaseUser.getUid());
        }

        Log.e(TAG, "Entered settings");

        RelativeLayout relativeSetting = findViewById(R.id.relativeSettings);
        addSwipe(relativeSetting);

        sliderDots = findViewById(R.id.sliderDot);
        createDotSlider();
        addSwipe(sliderDots);
        settingsLayout = findViewById(R.id.settingsLayout);
        addSwipe(settingsLayout);
        LinearLayout allSettings = findViewById(R.id.allSettings);
        addSwipe(allSettings);
        loadingPanelSettings = findViewById(R.id.loadingPanelSettings);

        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make default again
                user.setWeight(65.0);
                user.setWeight(170.0);
                user.setGender("male");
                user.setAge(30);

                mAuth.signOut();
                Toast.makeText(SettingsActivity.this, "Logged out successfully.", Toast.LENGTH_SHORT).show();

                enterNewActivity(LoginActivity.class);
            }
        });

        RadioGroup radioGroup = findViewById(R.id.units);
        final RadioButton ugm3RB = findViewById(R.id.ugm3);
        final RadioButton ppbRB = findViewById(R.id.ppb);

        if(GeneralClass.getAirData().getUnitMeasurement().equals("ppb")) {
            ppbRB.setChecked(true);
        } else {
            ugm3RB.setChecked(true);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(ugm3RB.isChecked()) {
                    GeneralClass.getAirData().setUnitMeasurement("ugm3");
                }
                if(ppbRB.isChecked()) {
                    GeneralClass.getAirData().setUnitMeasurement("ppb");
                }
                saveUnits();
            }
        });

        start();
    }

    private void setWeightListener() {
        FirebaseHelper firebaseHelper = GeneralClass.getFirebaseHelperObject();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        String str = weightET.getText().toString();

        if (TextUtils.isEmpty(str)) {
            Toast.makeText(SettingsActivity.this, "Enter weight.", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseHelper.inputDouble(firebaseUser.getUid() + "/weight", str);
        user.setWeight(Double.parseDouble(str));

        weightET.getText().clear();

        double weight = user.getWeight();
        weightET.setHint(weight + " kg");

        Toast.makeText(SettingsActivity.this, "Weight value has been set.", Toast.LENGTH_SHORT).show();
    }

    private void setHeightListener() {
        FirebaseHelper firebaseHelper = GeneralClass.getFirebaseHelperObject();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        String str = heightET.getText().toString();

        if (TextUtils.isEmpty(str)) {
            Toast.makeText(SettingsActivity.this, "Enter height.", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseHelper.inputDouble(firebaseUser.getUid() + "/height", str);
        user.setHeight(Double.parseDouble(str));

        heightET.getText().clear();

        double height = user.getHeight();
        heightET.setHint(height + " cm");

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

        new DotSlider(this, width, sliderDots, 0);
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
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                crossfade(settingsLayout);

                TextView helloTV = findViewById(R.id.helloText);
                String str = "Hello, " + user.getNameUser() + "! Please enter your weight and height.";
                helloTV.setText(str);

                weightET = findViewById(R.id.weight);
                weightET.setHint(user.getWeight() + " kg");

                weightET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                            setWeightListener();
                            Log.e(TAG, "Enter pressed");
                        }
                        return false;
                    }
                });

                Button weightBtn = findViewById(R.id.btnSaveWeight);
                weightBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setWeightListener();
                    }
                });

                heightET = findViewById(R.id.height);
                heightET.setHint(user.getHeight() + " cm");

                heightET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                            setHeightListener();
                            Log.e(TAG, "Enter pressed");
                        }
                        return false;
                    }
                });

                Button heightBtn = findViewById(R.id.btnSaveHeight);
                heightBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setHeightListener();
                    }
                });

            }
        }, 500);   //0.5 seconds
    }

    private void saveUnits() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefUnits",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString("units", GeneralClass.getAirData().getUnitMeasurement());
        editor.apply();
    }
}
