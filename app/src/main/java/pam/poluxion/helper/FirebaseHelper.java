package pam.poluxion.helper;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import pam.poluxion.MainActivity;
import pam.poluxion.R;
import pam.poluxion.models.LocalData;
import pam.poluxion.widgets.ProgressAnimation;

public class FirebaseHelper {
    private static final String TAG ="FirebaseHelper";

    private DatabaseReference myRef;

    private double tempDouble;
    private int tempInt;

    //private SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");  //formatting the current date into a dd-MM-yyyy String type

    public FirebaseHelper(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();  //stores Firebase instance into database variable
        String FirebaseUrl = context.getString(R.string.firebase_database_url);
        myRef = database.getReferenceFromUrl(FirebaseUrl);

        Log.e(TAG, "Successfully created");
    }

    public void inputInt(String child, String value) {
        int temp = Integer.parseInt(value);
        myRef.child(child).setValue(temp);
        Log.e(TAG, child + " data set = " + temp);
    }

    public void inputDouble(String child, String value) {
        double temp = Double.parseDouble(value);
        myRef.child(child).setValue(temp);
        Log.e(TAG,child + " data set = " + temp);
    }


    public double readDouble(String child) {
        myRef.child(child).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG,"Currently reading");

                if (dataSnapshot.getValue() != null) {
                    double read = dataSnapshot.getValue(Double.class); //gets from Firebase
                    tempDouble = read;
                    Log.d(TAG,"DataSnapshot = " + read);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        Log.d(TAG,"From firebase = " + tempDouble);
        return tempDouble;
    }

    public int readInt(String child) {
        myRef.child(child).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG,"Currently reading");

                if (dataSnapshot.getValue() != null) {
                    int read = dataSnapshot.getValue(Integer.class); //gets from Firebase
                    tempInt = read;
                    Log.d(TAG,"DataSnapshot = " + read);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        Log.d(TAG,"From firebase = " + tempInt);
        return tempInt;
    }
}
