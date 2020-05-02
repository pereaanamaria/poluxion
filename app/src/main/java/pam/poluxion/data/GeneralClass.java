package pam.poluxion.data;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.provider.Settings.Secure;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import pam.poluxion.models.AirData;
import pam.poluxion.models.StepCounter;
import pam.poluxion.models.User;

public class GeneralClass extends Application {
    private static final String TAG = "GeneralClass";

    private static User user;
    private static FirebaseHelper firebaseHelper;
    private static StepCounter stepCounter;
    private static AirData airData;

    @SuppressLint("HardwareIds")
    public GeneralClass(@NotNull Context context) {
        airData = new AirData();
        user = new User();
        firebaseHelper = new FirebaseHelper(context);
        stepCounter = new StepCounter();
    }

    @Contract(pure = true)
    public static User getUserObject(){return user;}

    @Contract(pure = true)
    public static FirebaseHelper getFirebaseHelperObject(){return firebaseHelper;}

    @Contract(pure = true)
    public static StepCounter getStepCounterObject(){return stepCounter;}
    public static void setStepCounter(StepCounter stepCounter) {GeneralClass.stepCounter = stepCounter;}

    @Contract(pure = true)
    public static AirData getAirData() {return airData;}
}
