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
import android.widget.ScrollView;

import pam.poluxion.widgets.DotSlider;
import pam.poluxion.widgets.OnSwipeTouchListener;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ScrollView scrollView = (ScrollView) findViewById(R.id.register);
        addSwipe(scrollView);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addSwipe(View view) {
        view.setOnTouchListener(new OnSwipeTouchListener(RegisterActivity.this) {
            public void onSwipeTop() {
                //Toast.makeText(RegisterActivity.this, "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                Intent intent = new Intent(RegisterActivity.this, TrackerActivity.class);
                intent.putExtra("Msg", "Left Activity");
                startActivity(intent);
                //Toast.makeText(RegisterActivity.this, "TrackerActivity", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.putExtra("Msg", "Left Activity");
                startActivity(intent);
                //Toast.makeText(RegisterActivity.this, "MainActivity", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("Msg", "Left Activity");
                startActivity(intent);
                //Toast.makeText(RegisterActivity.this, "LoginActivity", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
