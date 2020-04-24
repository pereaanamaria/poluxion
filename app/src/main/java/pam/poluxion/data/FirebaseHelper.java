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

    private double weight, height;
    private int age;
    private String name, lastName, gender;

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

    public void inputString(String child, String value) {
        myRef.child(child).setValue(value);
        Log.e(TAG,child + " data set = " + value);
    }


    public double readHeight(String child) {
        myRef.child(child + "/height").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG,"readHeight: Currently reading");

                if (dataSnapshot.getValue() != null) {
                    double read = dataSnapshot.getValue(Double.class); //gets from Firebase
                    height = read;
                    Log.e(TAG,"readHeight: DataSnapshot = " + read);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        Log.e(TAG,"readHeight: From firebase = " + height);
        return height;
    }

    public double readWeight(String child) {
        myRef.child(child + "/weight").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG,"readWeight: Currently reading");

                if (dataSnapshot.getValue() != null) {
                    double read = dataSnapshot.getValue(Double.class); //gets from Firebase
                    weight = read;
                    Log.e(TAG,"readWeight: DataSnapshot = " + read);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        Log.e(TAG,"readWeight: From firebase = " + weight);
        return weight;
    }

    public int readAge(String child) {
        myRef.child(child + "/age").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG,"readAge: Currently reading");

                if (dataSnapshot.getValue() != null) {
                    int read = dataSnapshot.getValue(Integer.class); //gets from Firebase
                    age = read;
                    Log.e(TAG,"readAge: DataSnapshot = " + read);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        Log.e(TAG,"readAge: From firebase = " + age);
        return age;
    }

    public String readName(String child) {
        myRef.child(child + "/name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG,"readName: Currently reading");

                if (dataSnapshot.getValue() != null) {
                    String read = dataSnapshot.getValue(String.class); //gets from Firebase
                    name = read;
                    Log.e(TAG,"readName: DataSnapshot = " + read);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        Log.e(TAG,"readName: From firebase = " + name);
        return name;
    }

    public String readLastName(String child) {
        myRef.child(child + "/lastName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG,"readLastName: Currently reading");

                if (dataSnapshot.getValue() != null) {
                    String read = dataSnapshot.getValue(String.class); //gets from Firebase
                    lastName = read;
                    Log.e(TAG,"readLastName: DataSnapshot = " + read);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        Log.e(TAG,"readLastName: From firebase = " + lastName);
        return lastName;
    }

    public String readGender(String child) {
        myRef.child(child + "/gender").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG,"readGender: Currently reading");

                if (dataSnapshot.getValue() != null) {
                    String read = dataSnapshot.getValue(String.class); //gets from Firebase
                    gender = read;
                    Log.e(TAG,"readGender: DataSnapshot = " + read);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        Log.e(TAG,"readGender: From firebase = " + gender);
        return gender;
    }
}
