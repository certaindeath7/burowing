package com.example.burowing2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText emailTxt, passwordTxt;
    private Button signUpBtn, clearBtn;
    private TextView tvSignIn;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mReference;
    private String emailName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.this.setTitle("Sign Up");

        // create an firebase Authentication object. A
        mFirebaseAuth = FirebaseAuth.getInstance();

        //email and password EditText
        emailTxt = (EditText) findViewById(R.id.mailSignInTxt);
        passwordTxt = (EditText) findViewById(R.id.pwdSigninTxt);

        //sign up button and clear button
        signUpBtn =(Button) findViewById(R.id.signUpBtn);
        clearBtn = (Button)  findViewById(R.id.clearBtn);

        //text view
        tvSignIn = findViewById(R.id.tvSignIn);

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailTxt.setText("");
                passwordTxt.setText("");
            }
        });

        // sign up button click listener
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailTxt.getText().toString();
                String pwd = passwordTxt.getText().toString();
                if (email.isEmpty()) {
                    emailTxt.setError("Please enter your email");
                    emailTxt.requestFocus();
                } else if (pwd.isEmpty()) {
                    passwordTxt.setError("Please enter your password"); // return an error Message setError (CharSequence)
                    passwordTxt.requestFocus(); // to draw user's focus in the TextEdit
                } else if (email.isEmpty() && pwd.isEmpty()) {
                    // create an small pop up
                    Toast.makeText(MainActivity.this, "Fields can't be empty", Toast.LENGTH_LONG);
                } else if (!(email.isEmpty() && pwd.isEmpty())) {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // if the task is not successfull, pop-up a small notifcation to the user
                            if (!task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Please try again!", Toast.LENGTH_LONG).show(); // Length_long = duration time

                            }
                            // if it's successful, move to page LoginActitvity
                            else {

                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                //firebase user can't be null
                                assert firebaseUser!=null;
                                //get the current user's id
                                String uid = firebaseUser.getUid();
                                mReference = FirebaseDatabase.getInstance().getReference("Users").child(uid);

                                //convert from EditText to a string
                                emailName = emailTxt.getText().toString();

                                //create a key-value pair to store user's information in the database
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("uid", uid);
                                hashMap.put("userName", emailName);

                                mReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(MainActivity.this, "Signed up successfully", Toast.LENGTH_LONG).show(); // Length_long = duration time

                                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                            finish();
                                        }
                                    }
                                });

                            }
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Error Occured", Toast.LENGTH_LONG);

                }
            }
        });

        // Text View click listener
        // WHen you click on this text view, it will switch to LoginActivity
        tvSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));

            }

        });
    }

}


