package pam.poluxion.helper;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pam.poluxion.R;
import pam.poluxion.data.GeneralClass;

public class FirebaseHelper {
    private static final String TAG ="FirebaseHelper";

    private DatabaseReference myRef;

    private double weight, height;
    private String name, lastName, gender, dob;

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
                    Log.e(TAG, "Connected to Firebase");
                } else {
                    Log.e(TAG, "Not connected to Firebase");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Listener was cancelled");
            }
        });

        Log.e(TAG, "Successfully created");
    }

    public void inputInt(String child, int value) {
        myRef.child(child).setValue(value);
    }

    public void inputDouble(String child, double value) {
        myRef.child(child).setValue(value);
    }

    public void inputDouble(String child, String value) {
        double temp = Double.parseDouble(value);
        myRef.child(child).setValue(temp);
    }

    public void inputString(String child, String value) {
        myRef.child(child).setValue(value);
    }


    public double readHeight(String child) {
        myRef.child(child + "/height").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    height = dataSnapshot.getValue(Double.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        GeneralClass.getUserObject().setHeight(height);
        return height;
    }

    public double readWeight(String child) {
        myRef.child(child + "/weight").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    weight = dataSnapshot.getValue(Double.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        GeneralClass.getUserObject().setWeight(weight);
        return weight;
    }

    public String readDOB(String child) {
        myRef.child(child + "/dob").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    dob = dataSnapshot.getValue(String.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        GeneralClass.getUserObject().setAge(dob);
        return dob;
    }

    public String readName(String child) {
        myRef.child(child + "/name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    name = dataSnapshot.getValue(String.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        GeneralClass.getUserObject().setNameUser(name);
        return name;
    }

    public String readLastName(String child) {
        myRef.child(child + "/lastName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    lastName = dataSnapshot.getValue(String.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        GeneralClass.getUserObject().setLastNameUser(lastName);
        return lastName;
    }

    public String readGender(String child) {
        myRef.child(child + "/gender").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    gender = dataSnapshot.getValue(String.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        GeneralClass.getUserObject().setGender(gender);
        return gender;
    }
}
