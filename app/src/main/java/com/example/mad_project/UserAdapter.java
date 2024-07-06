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

public class UserAdapter extends FirebaseRecyclerAdapter<ConnectedUser, UserAdapter.UserViewHolder> {


    Context context;
    public UserAdapter(@NonNull FirebaseRecyclerOptions<ConnectedUser> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int i, @NonNull ConnectedUser connectedUser) {

        holder.connectedUserName.setText(connectedUser.getConnectedToName());
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
