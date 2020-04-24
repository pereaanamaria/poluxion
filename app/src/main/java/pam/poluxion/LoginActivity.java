package pam.poluxion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText emailET, passwordET;


    private FirebaseAuth mAuth;

    private User user = GeneralClass.getUserObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        addSwipe(findViewById(R.id.login));

        emailET = (EditText) findViewById(R.id.emailLogin);
        passwordET = (EditText) findViewById(R.id.passwordLogin);

        Button loginBtn = (Button) findViewById(R.id.loginButton);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (signIn()) {
                        //user.login();

                        //get all data from Firebase
                        FirebaseHelper firebaseHelper = GeneralClass.getFirebaseHelperObject();
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            /*user.setID(firebaseUser.getUid());
                            Log.e(TAG,"User id : " + user.getID());
                            user.setEmail(firebaseUser.getEmail());

                            String name = firebaseHelper.readString("name");
                            user.setNameUser(name);
                            String lastName = firebaseHelper.readString("lastName");
                            user.setLastNameUser(lastName);
                            String gender = firebaseHelper.readString("gender");
                            user.setGender(gender);
                            int age = firebaseHelper.readInt("age");
                            user.setAge(age);*/

                            //user.login();
                        }

                        enterNewActivity(SettingsActivity.class);
                    }
                } catch (Exception e) {
                    Log.e(TAG,"Failure : " + e);
                }
            }
        });

        TextView createNewAccount = (TextView) findViewById(R.id.createNewAccount);
        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterNewActivity(RegisterActivity.class);
            }
        });
    }

    private boolean signIn() {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginActivity.this, "Enter email.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isEmailValid(email)) {
            Toast.makeText(LoginActivity.this, "Invalid email format.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Enter password.", Toast.LENGTH_SHORT).show();
            return false;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addSwipe(View view) {
        view.setOnTouchListener(new OnSwipeTouchListener(LoginActivity.this) {
            public void onSwipeRight() {
                enterNewActivity(TrackerActivity.class);
            }
            public void onSwipeLeft() {
                enterNewActivity(MainActivity.class);
            }
        });
    }

    private static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void enterNewActivity(Class activityClass) {
        LoginActivity.this.finish();
        Intent intent = new Intent(LoginActivity.this, activityClass);
        intent.putExtra("Msg", "Left Activity");
        startActivity(intent);
    }
}
