package pam.poluxion.data;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import pam.poluxion.helper.FirebaseHelper;
import pam.poluxion.models.AirData;
import pam.poluxion.models.StepCounter;
import pam.poluxion.models.User;

public class GeneralClass extends Application {
    private static final String TAG = "GeneralClass";

    private static User user;
    private static FirebaseHelper firebaseHelper;
    private static StepCounter stepCounter;
    private static AirData airData;

    //creates instances of objects to be used throughout the entire project
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
    //StepCounter is reset in StepsService
    public static void setStepCounter(StepCounter stepCounter) {GeneralClass.stepCounter = stepCounter;}

    @Contract(pure = true)
    public static AirData getAirData() {return airData;}
}
