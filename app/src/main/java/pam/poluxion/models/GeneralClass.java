package pam.poluxion.models;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.provider.Settings.Secure;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import pam.poluxion.helper.FirebaseHelper;

public class GeneralClass extends Application {

    static User user;
    static FirebaseHelper firebaseHelper;
    static StepCounter stepCounter;

    public GeneralClass(@NotNull Context context) {
        @SuppressLint("HardwareIds") String android_id = Secure.getString(context.getContentResolver(),Secure.ANDROID_ID);
        user = new User(android_id);
        firebaseHelper = new FirebaseHelper(context);
        stepCounter = new StepCounter();
    }

    public static void setUserObject(User userObject){
        user = userObject;
    }
    @Contract(pure = true)
    public static User getUserObject(){
        return user;
    }

    public static void setFirebaseHelperObject(FirebaseHelper firebaseHelperObject){
        firebaseHelper = firebaseHelperObject;
    }
    @Contract(pure = true)
    public static FirebaseHelper getFirebaseHelperObject(){
        return firebaseHelper;
    }

    public static void setStepCounterObject(StepCounter stepCounterObject){
        stepCounter = stepCounterObject;
    }
    @Contract(pure = true)
    public static StepCounter getStepCounterObject(){
        return stepCounter;
    }
}
