package pam.poluxion.data;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.provider.Settings.Secure;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import pam.poluxion.models.StepCounter;
import pam.poluxion.models.User;

public class GeneralClass extends Application {
    private static final String TAG = "GeneralClass";

    static User user;
    static FirebaseHelper firebaseHelper;
    static StepCounter stepCounter;
    static String android_id;

    @SuppressLint("HardwareIds")
    public GeneralClass(@NotNull Context context) {
        android_id = Secure.getString(context.getContentResolver(),Secure.ANDROID_ID);
        user = new User();
        firebaseHelper = new FirebaseHelper(context);
        stepCounter = new StepCounter();
    }

    @Contract(pure = true)
    public static User getUserObject(){
        return user;
    }

    @Contract(pure = true)
    public static FirebaseHelper getFirebaseHelperObject(){
        return firebaseHelper;
    }

    @Contract(pure = true)
    public static StepCounter getStepCounterObject(){
        return stepCounter;
    }

    @Contract(pure = true)
    public static String getAndroid_id() {
        return android_id;
    }
}
