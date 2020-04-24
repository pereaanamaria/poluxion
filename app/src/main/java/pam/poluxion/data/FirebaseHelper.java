package pam.poluxion.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pam.poluxion.R;

public class FirebaseHelper {
    private static final String TAG ="FirebaseHelper";

    private DatabaseReference myRef;

    private double tempDouble;
    private int tempInt;
    private boolean tempBoolean;
    private String tempString;

    public FirebaseHelper(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();  //stores Firebase instance into database variable
        String FirebaseUrl = context.getString(R.string.firebase_database_url);
        myRef = database.getReferenceFromUrl(FirebaseUrl);

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Log.e(TAG, "connected");

                    boolean status = readBoolean(GeneralClass.getAndroid_id()+"/connectedUser");
                    if(status) {
                        GeneralClass.getUserObject().login();
                        Log.e(TAG, "User is connected");
                    } else {
                        GeneralClass.getUserObject().logout();
                        Log.e(TAG, "User is not connected");
                    }


                } else {
                    Log.e(TAG, "not connected");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Listener was cancelled");
            }
        });

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

    public void inputBoolean(String child, boolean value) {
        myRef.child(child).setValue(value);
        Log.e(TAG,child + " data set = " + value);
    }

    public void inputString(String child, String value) {
        myRef.child(child).setValue(value);
        Log.e(TAG,child + " data set = " + value);
    }


    public double readDouble(String child) {
        myRef.child(child).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG,"readDouble: Currently reading");

                if (dataSnapshot.getValue() != null) {
                    double read = dataSnapshot.getValue(Double.class); //gets from Firebase
                    tempDouble = read;
                    Log.e(TAG,"readDouble: DataSnapshot = " + read);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        Log.e(TAG,"readDouble: From firebase = " + tempDouble);
        return tempDouble;
    }

    public int readInt(String child) {
        myRef.child(child).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG,"readInt: Currently reading");

                if (dataSnapshot.getValue() != null) {
                    int read = dataSnapshot.getValue(Integer.class); //gets from Firebase
                    tempInt = read;
                    Log.e(TAG,"readInt: DataSnapshot = " + read);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        Log.e(TAG,"readInt: From firebase = " + tempInt);
        return tempInt;
    }

    public boolean readBoolean(String child) {
        myRef.child(child).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG,"readBoolean: Currently reading");

                if (dataSnapshot.getValue() != null) {
                    boolean read = dataSnapshot.getValue(Boolean.class); //gets from Firebase
                    tempBoolean = read;
                    Log.e(TAG,"readBoolean: DataSnapshot = " + read);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        Log.e(TAG,"readBoolean: From firebase = " + tempBoolean);
        return tempBoolean;
    }

    public String readString(String child) {
        myRef.child(child).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG,"readString: Currently reading");

                if (dataSnapshot.getValue() != null) {
                    String read = dataSnapshot.getValue(String.class); //gets from Firebase
                    tempString = read;
                    Log.e(TAG,"readString: DataSnapshot = " + read);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        Log.e(TAG,"readString: From firebase = " + tempString);
        return tempString;
    }
}
