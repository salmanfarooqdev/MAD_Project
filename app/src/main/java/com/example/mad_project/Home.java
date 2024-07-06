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
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Home extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    ImageView settingsIcon;

    DatabaseReference mDatabase;
    TextView connectionCodeTextView, connectedUserInfoTextView;
    EditText connectCodeEditText;
    Button copyButton, connectButton;
    String userId, userName, connectionCode;

    RecyclerView connectedUsersRecyclerView;
    UserAdapter adapter;


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

        // Query to get connected users
        Query query = mDatabase.child("userConnections").child(userId);

        FirebaseRecyclerOptions<ConnectedUser> options =
                new FirebaseRecyclerOptions.Builder<ConnectedUser>()
                        .setQuery(query, ConnectedUser.class)
                        .build();

        adapter = new UserAdapter(options, this);
        connectedUsersRecyclerView.setAdapter(adapter);


// Combine both adapters or handle them separately




    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    private void init() {
        settingsIcon = findViewById(R.id.settingsIcon);
        connectionCodeTextView = findViewById(R.id.connectionCode);
        connectCodeEditText = findViewById(R.id.connectCodeEditText);
        copyButton = findViewById(R.id.copyButton);
        connectButton = findViewById(R.id.connectButton);

        connectedUsersRecyclerView = findViewById(R.id.connectedUsersRecyclerView);

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
                            String matchedUserName = userSnapshot.child("name").getValue(String.class);

                            DatabaseReference userConnectionsRef = mDatabase.child("userConnections");

                            // Add connection to current user's connections
                            String connectionId1 = userConnectionsRef.child(userId).push().getKey();
                            HashMap<String, Object> connectionMap1 = new HashMap<>();
                            connectionMap1.put("connectedUserId", matchedUserId);
                            connectionMap1.put("connectedUserName", matchedUserName);
                            userConnectionsRef.child(userId).child(connectionId1).setValue(connectionMap1);

                            // Add connection to matched user's connections
                            String connectionId2 = userConnectionsRef.child(matchedUserId).push().getKey();
                            HashMap<String, Object> connectionMap2 = new HashMap<>();
                            connectionMap2.put("connectedUserId", userId);
                            connectionMap2.put("connectedUserName", userName);
                            userConnectionsRef.child(matchedUserId).child(connectionId2).setValue(connectionMap2);

                            Toast.makeText(Home.this, "Connected successfully", Toast.LENGTH_SHORT).show();
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




}
