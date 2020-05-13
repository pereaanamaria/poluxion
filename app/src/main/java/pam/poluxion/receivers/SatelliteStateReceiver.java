package pam.poluxion.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import pam.poluxion.data.GeneralClass;
import pam.poluxion.services.SatelliteStatus;

import static pam.poluxion.services.LocationService.SATELLITES_DATA;

public class SatelliteStateReceiver extends BroadcastReceiver {
    private static final String TAG = "SatelliteStateReceiver";

    float avg1 = -1.0f, avg2 = -1.0f, avg3 = -1.0f;
    private boolean isAutoIndoor = true;

    // Prevents instantiation
    public SatelliteStateReceiver() {}

    // Called when the BroadcastReceiver gets an Intent it's registered to receive
    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d(TAG,intent.getExtras().toString());
        ArrayList<SatelliteStatus> satellites = (ArrayList<SatelliteStatus>) intent.getExtras().getSerializable(SATELLITES_DATA);
        if (satellites != null && satellites.size() > 0) {
            int cnt1 = 0, cnt2 = 0;
            float max = 0.0f, avg = 0.0f;
            for (int i = 0; i < satellites.size(); i++) {
                if (satellites.get(i).snr > 0.0f) {
                    if (max < satellites.get(i).snr) {
                        max = satellites.get(i).snr;
                    }
                    avg += satellites.get(i).snr;
                    cnt1++;
                } else {cnt2++;}
            }
            if (cnt1 > 0) {
                avg = avg / cnt1;
                avg1 = avg2;
                avg2 = avg3;
                avg3 = avg;
                int cnt = 3;
                if (avg1 < 0.0f) {
                    cnt--;
                }
                if (avg2 < 0.0f) {
                    cnt--;
                }
                avg = (avg1 + avg2 + avg3) / cnt;
                if (isAutoIndoor) {
                    if (avg > 26.0f) {
                        isAutoIndoor = false;
                    }
                } else {
                    if (avg < 24.0f) {
                        isAutoIndoor = true;
                    }
                }
                GeneralClass.getStepCounterObject().setIndoor(isAutoIndoor);
            }
        }
    }
}