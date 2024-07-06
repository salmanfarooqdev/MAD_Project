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

        Query query = mDatabase.child("connections")
                .orderByChild("user1Id").equalTo(userId);

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

                            // Check if a connection already exists
                            mDatabase.child("connections").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot connectionSnapshot) {
                                    boolean alreadyConnected = false;
                                    for (DataSnapshot connection : connectionSnapshot.getChildren()) {
                                        String user1 = connection.child("user1Id").getValue(String.class);
                                        String user2 = connection.child("user2Id").getValue(String.class);
                                        if ((user1.equals(userId) && user2.equals(matchedUserId)) || (user1.equals(matchedUserId) && user2.equals(userId))) {
                                            alreadyConnected = true;
                                            break;
                                        }
                                    }

                                    if (!alreadyConnected) {
                                        // Save connection in the connections node using HashMap
                                        DatabaseReference connectionsRef = mDatabase.child("connections");

                                        HashMap<String, Object> connectionMap = new HashMap<>();
                                        connectionMap.put("user1Id", userId);
                                        connectionMap.put("user1Name", userName);
                                        connectionMap.put("user2Id", matchedUserId);
                                        connectionMap.put("user2Name", matchedUserName);

                                        String connectionId = connectionsRef.push().getKey();
                                        if (connectionId != null) {
                                            connectionsRef.child(connectionId).setValue(connectionMap);
                                        }

                                        Toast.makeText(Home.this, "Connected successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Home.this, "You are already connected with this user", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Handle possible errors
                                }
                            });
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
