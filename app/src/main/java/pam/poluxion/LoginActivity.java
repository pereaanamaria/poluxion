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

import pam.poluxion.widgets.DotSlider;
import pam.poluxion.widgets.OnSwipeTouchListener;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        relativeLayout = (RelativeLayout) findViewById(R.id.login);
        addSwipe(relativeLayout);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addSwipe(View view) {
        view.setOnTouchListener(new OnSwipeTouchListener(LoginActivity.this) {
            public void onSwipeTop() {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("Msg", "Left Activity");
                startActivity(intent);
                //Toast.makeText(LoginActivity.this, "RegisterActivity", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                Intent intent = new Intent(LoginActivity.this, TrackerActivity.class);
                intent.putExtra("Msg", "Left Activity");
                startActivity(intent);
                //Toast.makeText(LoginActivity.this, "TrackerActivity", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("Msg", "Left Activity");
                startActivity(intent);
                //Toast.makeText(LoginActivity.this, "MainActivity", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                //Toast.makeText(LoginActivity.this, "bottom", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
