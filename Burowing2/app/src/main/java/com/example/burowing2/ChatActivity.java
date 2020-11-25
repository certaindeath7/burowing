package com.example.burowing2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

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
import com.example.burowing2.fragments.FragmentBorrowing;
import com.example.burowing2.fragments.FragmentDependingMessage;
import com.example.burowing2.fragments.FragmentLending;
import com.example.burowing2.fragments.FragmentUserChat;
import com.example.burowing2.fragments.FragmentWishlist;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView profilePicture;
    private TextView userName;
    private  BottomNavigationView bottomNavigationView;
    private SectionPageAdapter sectionPageAdapter;
    //viewpager declare
    private ViewPager viewPager;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //action bar declaration


        sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        //init viewpager
        viewPager = (ViewPager) findViewById(R.id.container_chat);

        //set up the viewpager with sections adapter
        setupViewPager(viewPager);

        //init tablayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_chat);
        tabLayout.setupWithViewPager(viewPager);

        //init components
        profilePicture = findViewById(R.id.user_avatar);
        userName =  findViewById(R.id.user_chat_name);

        //get the id of current user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        //print the name of currently logged in user
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserDTO userDTO = dataSnapshot.getValue(UserDTO.class);
                userName.setText(userDTO.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // on create bottom navigation
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        Menu menu =bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                switch ( menuItem.getItemId())
                {
                    case R.id.action_feedPage:
                        Intent intent1 = new Intent (ChatActivity.this, HomeActivity.class);
                        startActivity(intent1);
                        break;

                    case R.id.action_listPage:
                        Intent intent2 = new Intent (ChatActivity.this, WishListActivity.class);
                        startActivity(intent2);
                        break;

                    case R.id.action_postPage:
                        Intent intent3 = new Intent (ChatActivity.this, PostActivity.class);
                        startActivity(intent3);
                        break;

                    case R.id.action_chatPage:

                        break;

                    case R.id.action_mePage:
                        Intent intent4 = new Intent (ChatActivity.this, MeActivity.class);
                        startActivity(intent4);
                        break;
                }
                return false;
            }
        });



    }

    private void setupViewPager(ViewPager viewPager) {
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());

        //add fragments
        adapter.addFragment(new FragmentUserChat(), "User Chatting");
        adapter.addFragment(new FragmentDependingMessage(), "Depending Message");
        viewPager.setAdapter(adapter);

    }
}
