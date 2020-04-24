package pam.poluxion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import pam.poluxion.data.FirebaseHelper;
import pam.poluxion.data.GeneralClass;
import pam.poluxion.widgets.OnSwipeTouchListener;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText emailET, passwordET;
    private FirebaseAuth mAuth;

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
                signIn();
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

    private void signIn() {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(LoginActivity.this, "Enter email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Enter password.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseHelper firebaseHelper = GeneralClass.getFirebaseHelperObject();
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            GeneralClass.getUserObject().setID(firebaseUser.getUid());
                            Log.e(TAG,"Firebase auth UID : " + firebaseUser.getUid());

                            enterNewActivity(SettingsActivity.class);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

    private void enterNewActivity(Class activityClass) {
        LoginActivity.this.finish();
        Intent intent = new Intent(LoginActivity.this, activityClass);
        intent.putExtra("Msg", "User was logged");
        startActivity(intent);
    }
}
