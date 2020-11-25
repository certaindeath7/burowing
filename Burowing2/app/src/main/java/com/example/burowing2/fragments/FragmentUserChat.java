package com.example.burowing2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.burowing2.Adapter.ChatAdapter;
import com.example.burowing2.Models.UserDTO;
import com.example.burowing2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentUserChat extends Fragment {
    private static final String TAG = "Users";
    private RecyclerView userChatList;
    private ChatAdapter chatAdapter;
    private List<UserDTO> mUsers;
    private FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // create a new view by inflating existed xml file
        View view = inflater.inflate(R.layout.fragment_user_chatting,container,false);
        chatAdapter = new ChatAdapter(getContext(), mUsers);
        userChatList  = view.findViewById(R.id.chat_recycler_view);
        userChatList.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsers = new ArrayList<>();
        userChatList.setHasFixedSize(true);

        //print all the users
        printUsers();
        return view;

    }

    private void printUsers()
    {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                // check all the data found in the database
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    UserDTO userDTO = snapshot.getValue(UserDTO.class);
                    assert userDTO != null;
                    assert firebaseUser  !=null;

                    if(!userDTO.getUid().equals(firebaseUser.getUid()))
                    {mUsers.add(userDTO);}

                }
                chatAdapter = new ChatAdapter(getContext(), mUsers);
                userChatList.setAdapter(chatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}