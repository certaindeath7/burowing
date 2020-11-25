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
import com.example.burowing2.Models.MessageDTO;
import com.example.burowing2.Models.UserDTO;
import com.example.burowing2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    private android.content.Context mContext;
    private List<MessageDTO> mChat;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private FirebaseUser firebaseUser;

    // adapter constructor
    public MessageAdapter(android.content.Context mContext, List<MessageDTO> mChat)
    {
        this.mContext = mContext;
        this.mChat= mChat;

    }


    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_box_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_box_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        MessageDTO messageDTO = mChat.get(position);

        holder.show_message.setText(messageDTO.getContent());

    }


    // amount of users
    @Override
    public int getItemCount() {
        return mChat.size();
    }


    // init chat viewholder and pass this viewholder to the adapter
    public class ViewHolder extends RecyclerView.ViewHolder
    {

        public TextView show_message;
        public ImageView profilePic;

        public ViewHolder(View itemView)
        {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            profilePic = itemView.findViewById(R.id.avatar);
        }
    }

    //return the view type of the user's chat box
    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //if the logged in user sends message, it will be on the right side
        if(mChat.get(position).getSender().equals(firebaseUser.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }
    }
}
