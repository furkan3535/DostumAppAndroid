package com.example.a20mart;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends AppCompatActivity {

    EditText emailET,passwordET;
    Button signInWithEmail,registerBtn;
    private FirebaseAuth mAuth;
    public static FirebaseUser currentUser;
    public static FirebaseFirestore db;
    private static final String TAG = "FB Login";

    private void intentToMain(){
        startActivity(new Intent(this,MainActivity.class));
        //startActivity(new Intent(this,OnboardingActivity.class));
    }
    private void intentToRegister(){
        startActivity(new Intent(this,Register.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailET = findViewById( R.id.emailET);
        passwordET = findViewById( R.id.passwordET);
        signInWithEmail = findViewById(R.id.signInWithEmailBtn);
        mAuth = FirebaseAuth.getInstance();
        signInWithEmail.setOnClickListener(signInWithEmailPressed);
        db = FirebaseFirestore.getInstance();
        registerBtn  = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToRegister();
            }
        });


    }


    View.OnClickListener signInWithEmailPressed = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //mAuth.signInWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString())
            mAuth.signInWithEmailAndPassword("ekrem1@gmail.com","123456")
                    .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                currentUser = mAuth.getCurrentUser();
                                intentToMain();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginScreen.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }
    };
}

