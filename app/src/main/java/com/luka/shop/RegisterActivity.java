package com.luka.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    TextView banner,signIn;
    Button registerUser;
    EditText etFullName,etAge,etEmail,etPassword;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        banner=findViewById(R.id.banner);
        banner.setOnClickListener(this);

        registerUser=findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        signIn=findViewById(R.id.signIn);
        signIn.setOnClickListener(this);

        etFullName=findViewById(R.id.fullName);
        etAge=findViewById(R.id.age);
        etEmail=findViewById(R.id.email);
        etPassword=findViewById(R.id.password);

        progressBar=findViewById(R.id.progressBar);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.banner:
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                break;
            case R.id.registerUser:
                registerUser();
                break;
            case R.id.signIn:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
        }
    }

    private void registerUser() {

        // get fields needed to register
        String email=etEmail.getText().toString().trim(); // removes whitespaces
        String age=etAge.getText().toString().trim();
        String fullName=etFullName.getText().toString().trim();
        String password=etPassword.getText().toString().trim();

        // validation
        if(fullName.isEmpty()){
            etFullName.setError("Full name is required");
            etFullName.requestFocus();
            return;
        }
        if(age.isEmpty()){
            etAge.setError("Age is required");
            etAge.requestFocus();
            return;
        }
        if(email.isEmpty()){
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }
        // valid email form
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Please provide valid email");
            etEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }
        //firebase wants 6+ password lengths
        if(password.length()<6){
            etPassword.setError("Min password should be at least 6 characters");
            etPassword.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"User created",Toast.LENGTH_LONG).show();
                    userId=mAuth.getCurrentUser().getUid(); // id of authenticated user
                    DocumentReference mRef = db.collection("users").document(userId); // where to push data
                    Map<String,Object> user = new HashMap<>(); // key value mapping
                    user.put("fullName",fullName);
                    user.put("age",age);
                    user.put("email",email);
                    mRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RegisterActivity.this, "user profile is created for " + userId, Toast.LENGTH_SHORT).show();
                        }
                    });
                    progressBar.setVisibility(View.GONE);
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            startActivity(new Intent(RegisterActivity.this,ProfileActivity.class));
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this,"Failed to register! Try again",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
   }
}