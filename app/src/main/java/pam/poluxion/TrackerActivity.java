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
import android.widget.Toast;

import pam.poluxion.models.GeneralClass;
import pam.poluxion.models.User;
import pam.poluxion.widgets.DotSlider;
import pam.poluxion.widgets.OnSwipeTouchListener;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class TrackerActivity extends AppCompatActivity {

    private static final String TAG = "TrackerActivity";
    private RelativeLayout relativeLayout;
    private LinearLayout sliderDots;
    private User user = GeneralClass.getUserObject();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        sliderDots = (LinearLayout) findViewById(R.id.sliderDot);
        createDotSlider();

        relativeLayout = (RelativeLayout) findViewById(R.id.tracker);
       addSwipe(relativeLayout);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addSwipe(View view) {
        view.setOnTouchListener(new OnSwipeTouchListener(TrackerActivity.this) {
            public void onSwipeTop() {
                //Toast.makeText(TrackerActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                Intent intent = new Intent(TrackerActivity.this, MainActivity.class);
                intent.putExtra("Msg", "Left Activity");
                startActivity(intent);
                //Toast.makeText(TrackerActivity.this, "MainActivity", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                if (user.checkIfLogged()) {
                    Intent intent = new Intent(TrackerActivity.this, SettingsActivity.class);
                    intent.putExtra("Msg", "Left Activity");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(TrackerActivity.this, LoginActivity.class);
                    intent.putExtra("Msg", "Left Activity");
                    startActivity(intent);
                }
                //Toast.makeText(TrackerActivity.this, "SettingsActivity", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                //Toast.makeText(TrackerActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createDotSlider() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        Log.e(TAG,"Width = " + width);

        DotSlider dotSlider = new DotSlider(this,width,sliderDots,2);
    }
}
