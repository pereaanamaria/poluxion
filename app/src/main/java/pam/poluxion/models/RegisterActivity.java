package pam.poluxion.models;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import pam.poluxion.MainActivity;
import pam.poluxion.R;
import pam.poluxion.SettingsActivity;
import pam.poluxion.TrackerActivity;
import pam.poluxion.data.FirebaseHelper;
import pam.poluxion.data.GeneralClass;
import pam.poluxion.widgets.OnSwipeTouchListener;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private final Calendar myCalendar = Calendar.getInstance();

    private EditText emailET, passwordET, confirmPasswordET;
    private EditText nameET, lastNameET, dobET;
    private RadioButton maleRB, femaleRB;

    private FirebaseAuth mAuth;

    private String name, lastName, dob, gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ScrollView scrollView = (ScrollView) findViewById(R.id.register);
        addSwipe(scrollView);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        emailET = (EditText) findViewById(R.id.emailRegister);
        passwordET = (EditText) findViewById(R.id.passwordRegister);
        confirmPasswordET = (EditText) findViewById(R.id.confirmPasswordRegister);

        nameET = (EditText) findViewById(R.id.nameRegister);
        lastNameET = (EditText) findViewById(R.id.lastNameRegister);

        dobET = (EditText) findViewById(R.id.birthday);
        getDobET();

        maleRB = (RadioButton) findViewById(R.id.maleRegister);
        femaleRB = (RadioButton) findViewById(R.id.femaleRegister);

        Button registerBtn = (Button) findViewById(R.id.registerButton);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });

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

        TextView takeToLogin = (TextView) findViewById(R.id.takeToLogin);
        takeToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterNewActivity(LoginActivity.class);
            }
        });
    }

    private void createNewAccount() {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String confirmPassword = confirmPasswordET.getText().toString();

        name = nameET.getText().toString();
        lastName = lastNameET.getText().toString();
        dob = dobET.getText().toString();


        if(TextUtils.isEmpty(name)) {
            Toast.makeText(RegisterActivity.this, "Enter name.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(lastName)) {
            Toast.makeText(RegisterActivity.this, "Enter last name.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Enter email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (maleRB.isChecked())
        {
            gender = "male";
        } else if(femaleRB.isChecked()) {
            gender = "female";
        } else {
            Toast.makeText(RegisterActivity.this, "Select gender.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(dob)) {
            Toast.makeText(RegisterActivity.this, "Enter date of birth.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "Enter password.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(RegisterActivity.this, "Confirm password.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(confirmPassword)){
            Toast.makeText(RegisterActivity.this, "Passwords are not identical.", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Account created successfully.", Toast.LENGTH_SHORT).show();

                                FirebaseHelper firebaseHelper = GeneralClass.getFirebaseHelperObject();
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                firebaseHelper.inputString(firebaseUser.getUid() + "/name",name);
                                GeneralClass.getUserObject().setNameUser(name);
                                firebaseHelper.inputString(firebaseUser.getUid() + "/lastName",lastName);
                                GeneralClass.getUserObject().setLastNameUser(lastName);
                                firebaseHelper.inputString(firebaseUser.getUid() + "/gender",gender);
                                GeneralClass.getUserObject().setGender(gender);
                                firebaseHelper.inputString(firebaseUser.getUid() + "/dob",dob);
                                //GeneralClass.getUserObject().setAge(Integer.parseInt(age));
                                firebaseHelper.inputDouble(firebaseUser.getUid() + "/weight","65.0");
                                GeneralClass.getUserObject().setWeight(65.0);
                                firebaseHelper.inputDouble(firebaseUser.getUid() + "/height","170.0");
                                GeneralClass.getUserObject().setHeight(170.0);

                                GeneralClass.getUserObject().setID(firebaseUser.getUid());
                                //enterNewActivity(SettingsActivity.class);
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                            }

                            if (task.isSuccessful()) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        enterNewActivity(SettingsActivity.class);
                                    }
                                }, 500);   //0.5 seconds
                            }
                        }
                    });
        }
    }

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

        dobET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(RegisterActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, date, year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dobET.setText(sdf.format(myCalendar.getTime()));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addSwipe(View view) {
        view.setOnTouchListener(new OnSwipeTouchListener(RegisterActivity.this) {
            public void onSwipeRight() {
                enterNewActivity(TrackerActivity.class);
            }
            public void onSwipeLeft() {
                enterNewActivity(MainActivity.class);
            }
        });
    }

    private void enterNewActivity(Class activityClass) {
        RegisterActivity.this.finish();
        Intent intent = new Intent(RegisterActivity.this,activityClass);
        intent.putExtra("Msg", "User was logged");
        startActivity(intent);
    }
}
