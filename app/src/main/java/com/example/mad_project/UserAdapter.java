package com.example.mad_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserAdapter extends FirebaseRecyclerAdapter<ConnectedUser, UserAdapter.UserViewHolder> {

    private final DatabaseReference connectionsRef;
    private final String userId;
    Context context;
    public UserAdapter(@NonNull FirebaseRecyclerOptions<ConnectedUser> options, Context context) {
        super(options);
        this.context = context;
        this.connectionsRef = FirebaseDatabase.getInstance().getReference().child("connections");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.userId = (user != null) ? user.getUid() : null;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int i, @NonNull ConnectedUser connectedUser) {

        if (connectedUser.getUser1Id().equals(userId)) {
            holder.connectedUserName.setText(connectedUser.getUser2Name());
        } else {
            holder.connectedUserName.setText(connectedUser.getUser1Name());
        }

        holder.ivDelete.setOnClickListener(v -> {
            String connectionId = getRef(i).getKey();
            if (connectionId != null) {
                connectionsRef.child(connectionId).removeValue();
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

    public class UserViewHolder extends RecyclerView.ViewHolder{

        TextView connectedUserName;
        ImageView ivDelete;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            connectedUserName = itemView.findViewById(R.id.connectedUserName);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
