package pam.poluxion;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import pam.poluxion.widgets.DotSlider;
import pam.poluxion.widgets.OnSwipeTouchListener;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private RelativeLayout relativeLayout;
    public static LinearLayout sliderDots;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sliderDots = (LinearLayout) findViewById(R.id.sliderDot);
        createDotSlider();

        relativeLayout = (RelativeLayout) findViewById(R.id.settings);
        relativeLayout.setOnTouchListener(new OnSwipeTouchListener(SettingsActivity.this) {
            public void onSwipeTop() {
                //Toast.makeText(SettingsActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                //Toast.makeText(SettingsActivity.this, "left", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.putExtra("Msg", "Left Activity");
                startActivity(intent);
                //Toast.makeText(SettingsActivity.this, "MainActivity", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                //Toast.makeText(SettingsActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createDotSlider() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        Log.e(TAG,"Width = " + width);

        DotSlider dotSlider = new DotSlider(this,width,sliderDots,0);
    }
}
