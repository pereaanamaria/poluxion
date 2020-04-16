package pam.poluxion;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import pam.poluxion.widgets.DotSlider;

public class TrackerActivity extends AppCompatActivity {

    private static final String TAG = "TrackerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        createDotSlider();
    }

    private void createDotSlider() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        Log.e(TAG,"Width = " + width);

        DotSlider dotSlider = new DotSlider(this, width,2);
    }
}
