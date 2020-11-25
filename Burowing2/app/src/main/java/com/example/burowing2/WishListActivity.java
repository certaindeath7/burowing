package com.example.burowing2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.burowing2.fragments.FragmentBorrowing;
import com.example.burowing2.fragments.FragmentLending;
import com.example.burowing2.fragments.FragmentWishlist;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class WishListActivity extends AppCompatActivity {

    private  BottomNavigationView bottomNavigationView;
    private static final String TAG ="WishListActivity";
    //fragment declare
    private SectionPageAdapter sectionPageAdapter;
    //viewpager declare
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);
        Log.d(TAG, "onCreate: Starting.");

        sectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());
        //init viewpager
        viewPager = (ViewPager) findViewById(R.id.container);

        //set up the viewpager with sections adapter
        setupViewPager(viewPager);

        //init tablayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //bottom navigation
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        Menu menu =bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch ( menuItem.getItemId())
                {
                    case R.id.action_listPage:

                        break;


                    case R.id.action_feedPage:
                        Intent intent1 = new Intent (WishListActivity.this, HomeActivity.class);
                        startActivity(intent1);
                        break;


                    case R.id.action_postPage:
                        Intent intent2 = new Intent (WishListActivity.this, PostActivity.class);
                        startActivity(intent2);
                        break;

                    case R.id.action_chatPage:
                        Intent intent3= new Intent (WishListActivity.this, ChatActivity.class);
                        startActivity(intent3);
                        break;


                    case R.id.action_mePage:
                        Intent intent4 = new Intent (WishListActivity.this, MeActivity.class);
                        startActivity(intent4);
                        break;

                }
                return false;
            }
        });

    }

    //create a setup of viewpager adapter and add fragments to it
    private void setupViewPager(ViewPager viewPager)
    {
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());

        //add fragments
        adapter.addFragment(new FragmentBorrowing(), "Borrowing");
        adapter.addFragment(new FragmentWishlist(), "Wishlist");
        adapter.addFragment(new FragmentLending(), "Lending");
        viewPager.setAdapter(adapter);
    }
}
