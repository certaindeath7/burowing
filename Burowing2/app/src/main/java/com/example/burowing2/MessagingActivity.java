package com.example.burowing2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.burowing2.Adapter.MessageAdapter;
import com.example.burowing2.Models.MessageDTO;
import com.example.burowing2.Models.UserDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagingActivity extends AppCompatActivity {

    private CircleImageView profilePicture;
    private TextView userName;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private Intent intent;
    private String userid;
    private ImageButton send_btn;
    private EditText  send_message;

    //declare messageAdapter
    private MessageAdapter adapter;
    private List<MessageDTO> mChat;

    private RecyclerView  recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        // init components
        profilePicture = findViewById(R.id.user_avatar);
        userName =  findViewById(R.id.user_chat_name);
        send_btn =  findViewById(R.id.send_btn);
        send_message =  findViewById(R.id.send_message);

        //init conversation messaging adapter
        adapter = new MessageAdapter(MessagingActivity.this, mChat);
        mChat = new ArrayList<>();

        // messages recyler view
        recyclerView = findViewById(R.id.in_app_chat);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        // get the intent from ChatAdapter
        intent= getIntent();
        userid = intent.getStringExtra("uid");

        //get the id of current user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference =  FirebaseDatabase.getInstance().getReference("Users").child(userid);

        //get the name of clicked user
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    UserDTO userDTO = dataSnapshot.getValue(UserDTO.class);
                    String username =  userDTO.getUserName();
                    userName.setText(username);
                }
                else
                {
                    Toast.makeText(MessagingActivity.this,"Can't find any users", Toast.LENGTH_SHORT).show();
                }
                readMessage(firebaseUser.getUid(), userid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //send button click event listener
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = send_message.getText().toString();

                if(!text.equals(""))
                {
                    sendMessage(firebaseUser.getUid(), userid, text);
                }
                else
                {
                    Toast.makeText(MessagingActivity.this,"You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                send_message.setText("");
            }
        });

    }

    private void sendMessage(String sender, String receiver, String content)
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        // collect and retrieve data as a pair of key-value to create in the database
        HashMap<String, Object> hashMap = new  HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("content", content);

        //create a "Chats" table in the database
        ref.child("Chats").push().setValue(hashMap);

    }

    private void readMessage(final String myID, final String userID)
    {
        mChat = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    MessageDTO messageDTO = snapshot.getValue(MessageDTO.class);

                    if(messageDTO.getReceiver().equals(myID) && messageDTO.getSender().equals(userID) || messageDTO.getReceiver().equals(userID) && messageDTO.getSender().equals(myID))
                    {
                        mChat.add(messageDTO);

                    }
                }
                adapter = new MessageAdapter(MessagingActivity.this, mChat);

                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
