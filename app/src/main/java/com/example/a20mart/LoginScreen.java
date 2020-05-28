package com.example.a20mart;

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
    Button signInWithEmail,firestoreAddButton;
    private FirebaseAuth mAuth;
    public static FirebaseUser currentUser;
    public static FirebaseFirestore db;
    private static final String TAG = "FB Login";

    private void intentToMain(){
        startActivity(new Intent(this,MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailET = findViewById( R.id.emailET);
        passwordET = findViewById( R.id.passwordET);
        signInWithEmail = findViewById(R.id.signInWithEmailBtn);
        firestoreAddButton = findViewById(R.id.firestoreAddButton);
        mAuth = FirebaseAuth.getInstance();
        signInWithEmail.setOnClickListener(signInWithEmailPressed);
        firestoreAddButton.setOnClickListener(firestoreAddButtonPressed);
        db = FirebaseFirestore.getInstance();


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
            mAuth.signInWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString())
                    .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                currentUser = mAuth.getCurrentUser();
                                //intentToMain();

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

