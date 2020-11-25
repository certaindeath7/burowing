package com.example.burowing2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.hotspot2.pps.HomeSp;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private EditText emailTxt, passwordTxt;
    private Button loginBtn, clearBtn;
    private TextView tvSignIn, errorAlertTv;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener mAuthStateListener; // it's an interface which is called when there's a change in authentication state
    private static final int RC_SIGN_IN = 101;
    private SignInButton ggSignInButton; // initialize google sign in button
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG ="";
    private DatabaseReference mReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // set the page label /title
        LoginActivity.this.setTitle("Login");

        // create an firebase Authentication instance
        mFirebaseAuth = FirebaseAuth.getInstance();



        //email and password EditText
        emailTxt = (EditText)findViewById(R.id.mailSignInTxt);
        passwordTxt = (EditText) findViewById(R.id.pwdSigninTxt);

        //login button
        loginBtn = (Button) findViewById(R.id.loginBtn);
        clearBtn = (Button) findViewById(R.id.clearBtn);

        errorAlertTv = findViewById(R.id.errorAlertTv);

        //clear button
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailTxt.setText("");
                passwordTxt.setText("");
            }
        });

        // login with email and password
        // login button click listener
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailTxt.getText().toString();
                String pwd = passwordTxt.getText().toString();
                if(email.isEmpty())
                {
                    emailTxt.setError("Please enter your email");
                    emailTxt.requestFocus();
                }
                else if(pwd.isEmpty())
                {
                    passwordTxt.setError("Please enter your password"); // return an error Message setError (CharSequence)
                    passwordTxt.requestFocus(); // to draw user's focus in the TextEdit
                }
                else if(email.isEmpty() && pwd.isEmpty())
                {
                    // create an small pop up
                    Toast.makeText(LoginActivity.this, "Fields can't be empty", Toast.LENGTH_LONG);
                }
                else if (!(email.isEmpty() && pwd.isEmpty()))
                {
                    // sign in with email and password. addOnCompleteListener is to check whether task is completed
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            // if the task is not successfull, pop-up a small notifcation to the user
                            if(!task.isSuccessful())
                            {
                                errorAlertTv.setText("Email or password is wrong");
                                errorAlertTv.requestFocus();
                                errorAlertTv.setError( "your error message" );                            }
                            // if it's successful, move to page HomeActitvity
                            else
                            {
                                Toast.makeText(LoginActivity.this, "Signed in Successfully", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Error Occurred", Toast.LENGTH_LONG);

                }
            }
        });


        // login with google

        //google login button
        ggSignInButton = (SignInButton) findViewById(R.id.ggSignInBtn);
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //if the user singed in
                if(firebaseAuth.getCurrentUser()!=null)
                {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                }
            }
        };
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Set click listeners
        ggSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();

            }
        });


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 101);
    }

    // Start onActivityResult
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                if(account != null) firebaseAuthWithGoogle(account);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            String uid = user.getUid();
                            mReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

                            //convert from EditText to a string

                            //create a key-value pair to store user's information in the database
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("uid", uid);
                            hashMap.put("userName", user.getDisplayName());

                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                            mReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(LoginActivity.this, "Signed in successfully", Toast.LENGTH_LONG).show(); // Length_long = duration time

                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                        finish();
                                    }
                                }
                            });



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
    }


    protected  void onStart() // called when the application is visible to the users
    {
        super.onStart();

    }

}
