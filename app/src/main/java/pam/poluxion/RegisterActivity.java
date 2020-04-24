package pam.poluxion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pam.poluxion.data.FirebaseHelper;
import pam.poluxion.data.GeneralClass;
import pam.poluxion.models.User;
import pam.poluxion.widgets.OnSwipeTouchListener;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private EditText emailET, passwordET, confirmPasswordET;
    private EditText nameET, lastNameET, ageET;
    private RadioButton maleRB, femaleRB;

    private FirebaseAuth mAuth;
    private User user = GeneralClass.getUserObject();

    private String name, lastName, age, gender;
    private boolean failureAuth = false;

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
        ageET = (EditText) findViewById(R.id.ageRegister);

        maleRB = (RadioButton) findViewById(R.id.maleRegister);
        femaleRB = (RadioButton) findViewById(R.id.femaleRegister);

        Button registerBtn = (Button) findViewById(R.id.registerButton);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
                /*try {
                    if (createNewAccount()) {
                        //GeneralClass.getUserObject().login();

                        //get all data from Firebase
                        FirebaseHelper firebaseHelper = GeneralClass.getFirebaseHelperObject();
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if(firebaseUser != null) {
                            user.setID(firebaseUser.getUid());
                            Log.e(TAG,"User id : " + user.getID());
                            user.setEmail(firebaseUser.getEmail());

                            firebaseHelper.inputString(user.getID() + "name",name);
                            user.setNameUser(name);
                            firebaseHelper.inputString(user.getID() + "lastName",lastName);
                            user.setLastNameUser(lastName);
                            firebaseHelper.inputString(user.getID() + "gender",gender);
                            user.setGender(gender);
                            firebaseHelper.inputInt(user.getID() + "age",age);
                            user.setAge(Integer.parseInt(age));

                            //user.login();
                        }

                        user.login();

                        /*RegisterActivity.this.finish();
                        Intent intent = new Intent(RegisterActivity.this, SettingsActivity.class);
                        intent.putExtra("Msg", "Left Activity");
                        startActivity(intent);*/
                    /*}
                } catch(Exception e) {
                    Log.e(TAG,"FAILURE : " + e);
                }*/
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
        age = ageET.getText().toString();


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

        /*if(!isEmailValid(email)) {
            Toast.makeText(RegisterActivity.this, "Invalid email format.", Toast.LENGTH_SHORT).show();
            return;
        }*/

        if (maleRB.isChecked())
        {
            gender = "male";
        } else if(femaleRB.isChecked()) {
            gender = "female";
        } else {
            Toast.makeText(RegisterActivity.this, "Select gender.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(age)) {
            Toast.makeText(RegisterActivity.this, "Enter age.", Toast.LENGTH_SHORT).show();
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
                                enterNewActivity(SettingsActivity.class);
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
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
        //if(isNotTyping()) {
        RegisterActivity.this.finish();
        Intent intent = new Intent(RegisterActivity.this,activityClass);
        intent.putExtra("Msg", "Left Activity");
        startActivity(intent);
        /*} else {
            Toast.makeText(RegisterActivity.this, "Still typing...", Toast.LENGTH_SHORT).show();
        }*/
    }

    private static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /*private boolean isNotTyping() {
        if(!TextUtils.isEmpty(emailET.getText().toString())) return false;
        if(!TextUtils.isEmpty(passwordET.getText().toString())) return false;
        if(!TextUtils.isEmpty(confirmPasswordET.getText().toString())) return false;
        if (maleRB.isChecked()) return false;
        if(femaleRB.isChecked()) return false;
        if(!TextUtils.isEmpty(nameET.getText().toString())) return false;
        if(!TextUtils.isEmpty(lastNameET.getText().toString())) return false;
        return !TextUtils.isEmpty(ageET.getText().toString());
    }*/
}
