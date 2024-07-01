package com.example.mad_project;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    ImageView settingsIcon;

    DatabaseReference mDatabase;
    TextView connectionCodeTextView, connectedUserInfoTextView;
    EditText connectCodeEditText;
    Button copyButton, connectButton;
    String userId, userName, connectionCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        init();

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, Settings.class));
                finish();
            }
        });

        // Retrieve user's connection code and name from Firebase
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userName = dataSnapshot.child("name").getValue(String.class);
                    connectionCode = dataSnapshot.child("connectionCode").getValue(String.class);
                    if (connectionCode != null) {
                        connectionCodeTextView.setText(connectionCode);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard(connectionCode);
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = connectCodeEditText.getText().toString().trim();
                if (!code.isEmpty()) {
                    connectWithCode(code);
                } else {
                    Toast.makeText(Home.this, "Please enter a code", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Listen for connection changes
        mDatabase.child("users").child(userId).child("connectedTo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String connectedUserId = dataSnapshot.getValue(String.class);
                    if (connectedUserId != null) {
                        fetchConnectedUserInfo(connectedUserId);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void init() {
        settingsIcon = findViewById(R.id.settingsIcon);
        connectionCodeTextView = findViewById(R.id.connectionCode);
        connectedUserInfoTextView = findViewById(R.id.connectedUserInfo);
        connectCodeEditText = findViewById(R.id.connectCodeEditText);
        copyButton = findViewById(R.id.copyButton);
        connectButton = findViewById(R.id.connectButton);
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Connection Code", text);
        clipboard.setPrimaryClip(clip);
    }

    private void connectWithCode(String code) {
        mDatabase.child("users").orderByChild("connectionCode").equalTo(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String matchedUserId = userSnapshot.getKey();
                        if (matchedUserId != null && !matchedUserId.equals(userId)) {
                            // Establish connection for both users
                            mDatabase.child("users").child(userId).child("connectedTo").setValue(matchedUserId);
                            mDatabase.child("users").child(matchedUserId).child("connectedTo").setValue(userId);

                            Toast.makeText(Home.this, "Connected successfully", Toast.LENGTH_SHORT).show();
                            return; // Exit loop after first match
                        }
                    }
                } else {
                    Toast.makeText(Home.this, "No user found with that code", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }

    private void fetchConnectedUserInfo(String connectedUserId) {
        mDatabase.child("users").child(connectedUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String connectedUserName = dataSnapshot.child("name").getValue(String.class);
                if (connectedUserName != null) {
                    connectedUserInfoTextView.setText(connectedUserName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}
