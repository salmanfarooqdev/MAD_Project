package com.example.mad_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Random;

public class SignUp extends AppCompatActivity {

    TextView login_link;
    TextInputEditText etName, etEmail, etPassword, etCPassword;
    Button btnSignUp;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        init();

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                ,WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        login_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, MainActivity.class));
                finish();
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String pass = etPassword.getText().toString();
                String cpass = etCPassword.getText().toString();

                if(email.isEmpty())
                {
                    etEmail.setError("Email cant be empty");
                    return;
                }

                if(pass.isEmpty())
                {
                    etPassword.setError("Email cant be empty");
                    return;
                }

                if(cpass.isEmpty())
                {
                    etCPassword.setError("Email cant be empty");
                    return;
                }

                if(!pass.equals(cpass))
                {
                    etCPassword.setError("password didn't match");
                    return;
                }

                ProgressDialog progressDialog = new ProgressDialog(SignUp.this);
                progressDialog.setMessage("Registration in progress.....");
                progressDialog.show();

                FirebaseAuth
                        .getInstance()
                        .createUserWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                progressDialog.dismiss();
                                saveUserDetails(authResult.getUser().getUid(), name, email);
                                startActivity(new Intent(SignUp.this, Home.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

    }
    private void init()
    {
        login_link = findViewById(R.id.login_link);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etCPassword = findViewById(R.id.etCPassword);
        btnSignUp = findViewById(R.id.btnSignUp);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void saveUserDetails(String userId, String name, String email) {
        String connectionCode = generateRandomCode();

        HashMap<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("connectionCode", connectionCode);

        mDatabase.child("users").child(userId).setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SignUp.this, "User details saved successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUp.this, "Failed to save user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String generateRandomCode() {
        int codeLength = 5;
        StringBuilder code = new StringBuilder(codeLength);
        Random random = new Random();
        for (int i = 0; i < codeLength; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}