package com.example.mad_project;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateWidget extends AppCompatActivity {

    private EditText etText;
    private Button updateButton;
    private String connectedUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_widget);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        etText = findViewById(R.id.etText);
        updateButton = findViewById(R.id.updateButton);

        connectedUserId = getIntent().getStringExtra("connectedUserId");

        updateButton.setOnClickListener(v -> {
            String text = etText.getText().toString().trim();
            if (!text.isEmpty()) {
                updateWidgetText(connectedUserId, text);
            } else {
                Toast.makeText(this, "Text cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWidgetText(String userId, String text) {
        DatabaseReference userWidgetRef = FirebaseDatabase.getInstance().getReference()
                .child("userWidgets").child(userId);
        userWidgetRef.setValue(text)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Widget updated successfully", Toast.LENGTH_SHORT).show();
                    NewAppWidget.updateWidgets(this);
                    navigateBackToHome();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update widget", Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateBackToHome() {
        startActivity(new Intent(UpdateWidget.this, Home.class));
        finish();
    }
}
