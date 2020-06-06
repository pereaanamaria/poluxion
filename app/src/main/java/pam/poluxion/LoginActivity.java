package pam.poluxion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

        emailET = findViewById(R.id.emailLogin);
        passwordET = findViewById(R.id.passwordLogin);

        Button loginBtn = findViewById(R.id.loginButton);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //signs user in if login is successful
        passwordET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    signIn();
                    Log.e(TAG,"Enter pressed");
                }
                return false;
            }
        });

        //enters RegisterActivity
        TextView createNewAccount = findViewById(R.id.createNewAccount);
        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterNewActivity(RegisterActivity.class);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    //signs user in by checking email and password
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

        //firebase email and password authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");

                            Handler handler = new Handler();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    //updates user profile based on ID
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    Log.e(TAG, "Start Updating... : " + firebaseUser.getUid());
                                    GeneralClass.getUserObject().updateData(firebaseUser.getUid());
                                    Log.e(TAG, "Updating... : " + firebaseUser.getUid());
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                        //enters MainActivity
                        if(task.isSuccessful()) {
                            enterNewActivity(MainActivity.class);
                            Toast.makeText(LoginActivity.this, "Settings can be now accessed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
   }

    //swipe right => TrackerActivity
    //swipe left => MainActivity
    @SuppressLint("ClickableViewAccessibility")
    private void addSwipe(View view) {
        view.setOnTouchListener(new OnSwipeTouchListener(LoginActivity.this) {
            public void onSwipeRight() {enterNewActivity(TrackerActivity.class);}
            public void onSwipeLeft() {enterNewActivity(MainActivity.class);}
        });
    }

    //enters activityClass
    private void enterNewActivity(Class activityClass) {
        LoginActivity.this.finish();
        Intent intent = new Intent(LoginActivity.this, activityClass);
        intent.putExtra("Msg", "User was logged");
        startActivity(intent);
    }
}
