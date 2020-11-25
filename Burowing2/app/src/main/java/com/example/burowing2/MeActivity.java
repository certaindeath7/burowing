package com.example.burowing2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.burowing2.Models.UserDTO;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MeActivity extends AppCompatActivity {

    private  BottomNavigationView bottomNavigationView;
    private ListView listView;
    private TextView profileName;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        //auth state listener for signout function
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null)
                {
                    startActivity(new Intent(MeActivity.this, LoginActivity.class));
                }
            }
        };


        //init listview
        listView = (ListView) findViewById(R.id.settings_list);
        String[] settings ={"Help", "Resume", "Logout"};
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, settings);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0)
                {
                    startActivity(new Intent(MeActivity.this, HelpActivity.class));
                }
                if(position == 1)
                {
                    startActivity(new Intent(MeActivity.this, ResumeActivity.class));
                }
                if(position == 2)
                {
                    //log out

                    mAuth.signOut();
                    finish();

                }
            }
        });


        //set profile's name of recently logged in user
        profileName= findViewById(R.id.user_profile_name);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        //print the name of currently logged in user
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserDTO userDTO = dataSnapshot.getValue(UserDTO.class);
                profileName.setText(userDTO.getUserName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // on create bottom navigation
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        Menu menu =bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {




                switch ( menuItem.getItemId())
                {
                    case R.id.action_feedPage:
                        Intent intent1 = new Intent (MeActivity.this, HomeActivity.class);
                        startActivity(intent1);
                        break;

                    case R.id.action_listPage:
                        Intent intent2 = new Intent (MeActivity.this, WishListActivity.class);
                        startActivity(intent2);
                        break;

                    case R.id.action_postPage:
                        Intent intent3 = new Intent (MeActivity.this, PostActivity.class);
                        startActivity(intent3);
                        break;

                    case R.id.action_chatPage:
                        Intent intent4 = new Intent (MeActivity.this, ChatActivity.class);
                        startActivity(intent4);
                        break;

                    case R.id.action_mePage:
                        break;
                }
                return false;
            }
        });


        //action bar declaration
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Me Page");

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
}
