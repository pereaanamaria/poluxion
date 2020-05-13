package pam.poluxion.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;

import pam.poluxion.BuildConfig;

public class TransitionReceiver extends BroadcastReceiver {
    private static final String TAG = "TransitionReceiver";
    private String status = "still";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Intents action that will be fired when transitions are triggered
        String TRANSITION_ACTION_RECEIVER = BuildConfig.APPLICATION_ID + "TRANSITION_ACTION_RECEIVER";
        if (!TextUtils.equals(TRANSITION_ACTION_RECEIVER, intent.getAction())){
            Log.e(TAG,"Unsupported action received in myTransitionReceiver class: action=" + intent.getAction());
            return;
        }

        Log.e(TAG,"Got here");

        if (ActivityTransitionResult.hasResult(intent)){
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            assert result != null;
            for (ActivityTransitionEvent event : result.getTransitionEvents()){
                String theActivity = toActivityString(event.getActivityType());
                String transType = toTransitionType(event.getTransitionType());
                if(transType.equals("ENTER")) {
                    status = theActivity;
                }
            }
        }
    }

    public String getDetectedActivity() {
        return status;
    }

    private String toActivityString(int activity) {
        switch (activity) {
            case DetectedActivity.STILL: return "Still";
            case DetectedActivity.WALKING: return "Walking";
            case DetectedActivity.IN_VEHICLE: return "In vehicle";
            case DetectedActivity.ON_BICYCLE: return "Cycling";
            case DetectedActivity.RUNNING: return "Running";
            default: return "Unknown";
        }
    }

    private String toTransitionType(int transitionType) {
        switch (transitionType) {
            case ActivityTransition.ACTIVITY_TRANSITION_ENTER: return "ENTER";
            case ActivityTransition.ACTIVITY_TRANSITION_EXIT: return "EXIT";
            default: return "UNKNOWN";
        }
    }
}
