package com.example.mad_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    TextView signup_link;
    TextInputEditText etEmail, etPassword;
    TextView tvForgotPassword;
    Button btnLogin;
    ProgressBar progressBar;

    FirebaseAuth auth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        init();

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                ,WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        if(alreadyLoggedIn())
        {
            startActivity(new Intent(MainActivity.this, Home.class));
            finish();
        }

        signup_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etEmail.getText().toString().trim();
                String pass = etPassword.getText().toString();

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

                progressBar.setVisibility(View.VISIBLE);

                FirebaseAuth
                        .getInstance()
                        .signInWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(MainActivity.this, Home.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);

                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
    private boolean alreadyLoggedIn() {
        return (user!=null);
    }
    private void init()
    {
        signup_link = findViewById(R.id.signup_link);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
    }
}