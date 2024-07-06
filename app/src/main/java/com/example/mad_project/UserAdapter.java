package com.example.mad_project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserAdapter extends FirebaseRecyclerAdapter<ConnectedUser, UserAdapter.UserViewHolder> {

    private final DatabaseReference userConnectionsRef;
    private final String userId;
    private final Context context;

    public UserAdapter(@NonNull FirebaseRecyclerOptions<ConnectedUser> options, Context context) {
        super(options);
        this.context = context;
        this.userConnectionsRef = FirebaseDatabase.getInstance().getReference().child("userConnections");
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull ConnectedUser connectedUser) {
        holder.connectedUserName.setText(connectedUser.getConnectedUserName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateWidget.class);
            intent.putExtra("connectedUserId", connectedUser.getConnectedUserId());
            context.startActivity(intent);
        });


        holder.ivDelete.setOnClickListener(v -> {
            String connectionId = getRef(position).getKey();
            if (connectionId != null) {
                // Remove the connection from current user's list
                userConnectionsRef.child(userId).child(connectionId).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            // Remove the connection from the connected user's list
                            userConnectionsRef.child(connectedUser.getConnectedUserId())
                                    .orderByChild("connectedUserId").equalTo(userId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                snapshot.getRef().removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Handle database error
                                            Toast.makeText(context, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure to remove connection
                            Toast.makeText(context, "Failed to remove connection: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_item_connected_users_layout, parent, false);
        return new UserViewHolder(view);
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView connectedUserName;
        ImageView ivDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            connectedUserName = itemView.findViewById(R.id.connectedUserName);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
