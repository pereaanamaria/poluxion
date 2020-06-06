package pam.poluxion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.Objects;

import pam.poluxion.helper.FirebaseHelper;
import pam.poluxion.data.GeneralClass;
import pam.poluxion.widgets.OnSwipeTouchListener;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    private EditText emailET, passwordET, confirmPasswordET;
    private EditText nameET, lastNameET, dobET, weightET, heightET;
    private RadioButton maleRB, femaleRB;

    private FirebaseAuth mAuth;

    private String name, lastName, dob, gender, weight, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ScrollView scrollView = findViewById(R.id.register);
        addSwipe(scrollView);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        emailET = findViewById(R.id.emailRegister);
        passwordET = findViewById(R.id.passwordRegister);
        confirmPasswordET = findViewById(R.id.confirmPasswordRegister);

        nameET = findViewById(R.id.nameRegister);
        lastNameET = findViewById(R.id.lastNameRegister);
        weightET = findViewById(R.id.weightRegister);
        heightET = findViewById(R.id.heightRegister);

        dobET = findViewById(R.id.birthday);
        getDobET();

        maleRB = findViewById(R.id.maleRegister);
        femaleRB = findViewById(R.id.femaleRegister);

        Button registerBtn = findViewById(R.id.registerButton);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });

        //creates new account
        confirmPasswordET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    createNewAccount();
                    Log.e(TAG,"Enter pressed");
                }
                return false;
            }
        });

        //enters LoginActivity
        TextView takeToLogin = findViewById(R.id.takeToLogin);
        takeToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterNewActivity(LoginActivity.class);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void createNewAccount() {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String confirmPassword = confirmPasswordET.getText().toString();

        name = nameET.getText().toString();
        lastName = lastNameET.getText().toString();
        weight = weightET.getText().toString();
        height = heightET.getText().toString();
        dob = dobET.getText().toString();

        //checks if name field is empty
        if(TextUtils.isEmpty(name)) {
            Toast.makeText(RegisterActivity.this, "Enter name.", Toast.LENGTH_SHORT).show();
            return;
        }
        //checks if last name field is empty
        if(TextUtils.isEmpty(lastName)) {
            Toast.makeText(RegisterActivity.this, "Enter last name.", Toast.LENGTH_SHORT).show();
            return;
        }
        //checks if email field is empty
        if(TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Enter email.", Toast.LENGTH_SHORT).show();
            return;
        }
        //checks if gender was chosen
        if (maleRB.isChecked())
        {
            gender = "male";
        } else if(femaleRB.isChecked()) {
            gender = "female";
        } else {
            Toast.makeText(RegisterActivity.this, "Select gender.", Toast.LENGTH_SHORT).show();
            return;
        }
        //checks if date of birth was picked
        if(TextUtils.isEmpty(dob)) {
            Toast.makeText(RegisterActivity.this, "Enter date of birth.", Toast.LENGTH_SHORT).show();
            return;
        }
        //checks if weight field is empty
        if(TextUtils.isEmpty(weight)) {
            Toast.makeText(RegisterActivity.this, "Enter weight.", Toast.LENGTH_SHORT).show();
            return;
        }
        //checks if height field is empty
        if(TextUtils.isEmpty(height)) {
            Toast.makeText(RegisterActivity.this, "Enter height.", Toast.LENGTH_SHORT).show();
            return;
        }
        //checks if password field is empty
        if(TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "Enter password.", Toast.LENGTH_SHORT).show();
            return;
        }
        //checks if password confirmation field is empty
        if(TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(RegisterActivity.this, "Confirm password.", Toast.LENGTH_SHORT).show();
            return;
        }
        //checks if passwords are identical
        if(!password.equals(confirmPassword)){
            Toast.makeText(RegisterActivity.this, "Passwords are not identical.", Toast.LENGTH_SHORT).show();
        } else {
            //creates new account if email is unique
            //creates user profile in firebase based on authentication ID
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Account created successfully.", Toast.LENGTH_SHORT).show();

                                Handler handler = new Handler();
                                handler.post(new Runnable() {
                                    public void run() {
                                        FirebaseHelper firebaseHelper = GeneralClass.getFirebaseHelperObject();
                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                        Log.e(TAG, "Start Updating... : " + firebaseUser.getUid());
                                        firebaseHelper.inputString(firebaseUser.getUid() + "/name", name);
                                        GeneralClass.getUserObject().setNameUser(name);
                                        firebaseHelper.inputString(firebaseUser.getUid() + "/lastName", lastName);
                                        GeneralClass.getUserObject().setLastNameUser(lastName);
                                        firebaseHelper.inputString(firebaseUser.getUid() + "/gender", gender);
                                        GeneralClass.getUserObject().setGender(gender);
                                        firebaseHelper.inputString(firebaseUser.getUid() + "/dob", dob);
                                        GeneralClass.getUserObject().setAge(dob);
                                        firebaseHelper.inputDouble(firebaseUser.getUid() + "/weight", weight);
                                        GeneralClass.getUserObject().setWeight(Double.parseDouble(weight));
                                        firebaseHelper.inputDouble(firebaseUser.getUid() + "/height", height);
                                        GeneralClass.getUserObject().setHeight(Double.parseDouble(weight));

                                        GeneralClass.getUserObject().setID(firebaseUser.getUid());
                                        Log.e(TAG, "Updating... : " + firebaseUser.getUid());
                                    }
                                });
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                            }

                            if (task.isSuccessful()) {
                                //enters MainActivity
                                enterNewActivity(MainActivity.class);
                                Toast.makeText(RegisterActivity.this, "Settings can be now accessed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    //gets date of birth string using calendar widget
    private void getDobET() {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: dd/mm/yyyy: " + day + "/" + month + "/" + year);

                dob = day + "/" + month + "/" + year;
                dobET.setText(dob);
            }
        };

        //adds listener to DOB field
        dobET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(RegisterActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, date, year,month,day);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
    }

    //swipe right => TrackerActivity
    //swipe left => MainActivity
    @SuppressLint("ClickableViewAccessibility")
    private void addSwipe(View view) {
        view.setOnTouchListener(new OnSwipeTouchListener(RegisterActivity.this) {
            public void onSwipeRight() {enterNewActivity(TrackerActivity.class);}
            public void onSwipeLeft() {enterNewActivity(MainActivity.class);}
        });
    }

    //enters activityClass
    private void enterNewActivity(Class activityClass) {
        RegisterActivity.this.finish();
        Intent intent = new Intent(RegisterActivity.this,activityClass);
        intent.putExtra("Msg", "User was logged");
        startActivity(intent);
    }
}
