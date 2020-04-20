package pam.poluxion.models;

import android.util.Log;

public class StepCounter {
    private static final String TAG = "StepCounter";

    private int steps;

    public StepCounter() {
        Log.e(TAG,"Successfully created");
    }

    public int getSteps() {
        return steps;
    }
}
