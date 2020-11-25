package com.example.burowing2.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.burowing2.MessagingActivity;
import com.example.burowing2.Models.UserDTO;
import com.example.burowing2.R;
import com.google.firebase.database.core.Context;


import java.util.List;

// users in chat room adapter
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private android.content.Context mContext;
    private List<UserDTO> mUsers;



    // adapter constructor
    public ChatAdapter(android.content.Context mContext, List<UserDTO> mUsers)
    {
        this.mContext = mContext;
        this.mUsers= mUsers;

    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // create a view from an existed xml file
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_items, parent, false);

        return new ChatAdapter.ChatViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {

       final UserDTO userDTO = mUsers.get(position);
        holder.userName.setText(userDTO.getUserName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Intent intent =  new Intent(mContext, MessagingActivity.class);
               intent.putExtra("uid", userDTO.getUid());
               mContext.startActivity(intent);
           }
       });
    }


    // amount of users
    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    // init chat viewholder and pass this viewholder to the adapter
    public class ChatViewHolder extends RecyclerView.ViewHolder
    {

        public TextView userName;
        public ImageView profilePic;

        public ChatViewHolder(View itemView)
        {
            super(itemView);
            userName = itemView.findViewById(R.id.user_chat_name);
            profilePic = itemView.findViewById(R.id.user_avatar);
        }
    }
}
