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
    Button signInWithEmail,firestoreAddButton,registerBtn;
    private FirebaseAuth mAuth;
    public static FirebaseUser currentUser;
    public static FirebaseFirestore db;
    private static final String TAG = "FB Login";

    private void intentToMain(){
       // startActivity(new Intent(this,MainActivity.class));
        startActivity(new Intent(this,OnboardingActivity.class));
    }
    private void intentToRegister(){
        startActivity(new Intent(this,Register.class));
    }
    public void CallDataFB(){

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName componentName=new ComponentName(this,CallDataFBService.class);
        JobInfo jobInfo;


        jobInfo = new JobInfo.Builder(952,componentName)
                .setPersisted(true) //job will be written to disk and loaded at boot.
                .setPeriodic(15*60*1000) //Periodicity
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) //Requires Any Network To Run.
                .build();

        int resultCode=jobScheduler.schedule(jobInfo);
        if(resultCode == JobScheduler.RESULT_SUCCESS){
            Log.i(TAG, "Job Scheduled Successfully");
        }
        else{
            Log.i(TAG, "Job Scheduled not Successfully");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailET = findViewById( R.id.emailET);
        passwordET = findViewById( R.id.passwordET);
        signInWithEmail = findViewById(R.id.signInWithEmailBtn);
        //firestoreAddButton = findViewById(R.id.firestoreAddButton);
        mAuth = FirebaseAuth.getInstance();
        signInWithEmail.setOnClickListener(signInWithEmailPressed);
        //firestoreAddButton.setOnClickListener(firestoreAddButtonPressed);
        db = FirebaseFirestore.getInstance();
        registerBtn  = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToRegister();
            }
        });

        //CallDataFB();

    }


    View.OnClickListener firestoreAddButtonPressed  = new  View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Map<String, Object> user = new HashMap<>();
            user.put("UserId", currentUser.getUid());
            user.put("Date", Calendar.getInstance().getTime());


            db.collection("CallData")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }
    };

    View.OnClickListener signInWithEmailPressed = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
<<<<<<< HEAD
            //mAuth.signInWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString())
            mAuth.signInWithEmailAndPassword("furkan@gmail.com","1234qwer")
=======

            //ekrem1@gmail.com UID = bKcM1RjC3iaNLnvrjSW8EzS65u12








            mAuth.signInWithEmailAndPassword("ekrem1@gmail.com", "123456")
>>>>>>> FireBase_Data_Send
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

