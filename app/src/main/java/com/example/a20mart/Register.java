package com.example.a20mart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import static android.widget.Toast.LENGTH_LONG;

public class Register extends AppCompatActivity implements View.OnClickListener {
    TextView nameText,emailText,passwordText;
    Button  Register;
    FirebaseAuth  mAuth;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        nameText=findViewById(R.id.nameText);
        emailText=findViewById(R.id.emailText);
        passwordText=findViewById(R.id.passText);
        Register=findViewById(R.id.RegisterBtn);
        Register.setOnClickListener(this);
        mAuth=FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void intentToMain(){
        startActivity(new Intent(this,MainActivity.class));
    }
    @Override
    public void onClick(View v) {
        mAuth.createUserWithEmailAndPassword(emailText.getText().toString(),passwordText.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                HashMap<String,String> usr=new HashMap<>();
                usr.put("Name",nameText.getText().toString());
                usr.put("Email",emailText.getText().toString());
                db.collection("NewUsers").document(mAuth.getCurrentUser().getUid()).set(usr);
                finish();

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(com.example.a20mart.Register.this,"YOU ALREADY REGISTERED",Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
