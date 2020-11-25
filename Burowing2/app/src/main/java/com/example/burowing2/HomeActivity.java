package com.example.burowing2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.burowing2.Models.Items;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private FirebaseUser mFireBaseUser;
    private DatabaseReference mReference;
    private FirebaseRecyclerAdapter<Items, ViewHolder> mFirebaseRecycler;
    private  BottomNavigationView bottomNavigationView;
    private EditText searchEditText;
    private ImageButton search_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //get id of logged in user
        mFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(mFireBaseUser.getUid());


        // on create bottom navigation
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        Menu menu =bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch ( menuItem.getItemId())
                {
                    case R.id.action_feedPage:

                        break;

                    case R.id.action_listPage:
                        Intent intent1 = new Intent (HomeActivity.this, WishListActivity.class);
                        startActivity(intent1);
                        Toast.makeText(HomeActivity.this, "Feed page",Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.action_postPage:
                        Intent intent2 = new Intent (HomeActivity.this, PostActivity.class);
                        startActivity(intent2);
                        Toast.makeText(HomeActivity.this, "Post page",Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.action_chatPage:
                        Intent intent3 = new Intent (HomeActivity.this, ChatActivity.class);
                        startActivity(intent3);
                        Toast.makeText(HomeActivity.this, "Chat page",Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.action_mePage:
                        Intent intent4 = new Intent (HomeActivity.this, MeActivity.class);
                        startActivity(intent4);
                        Toast.makeText(HomeActivity.this, "Me page",Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

        // recycler view oncreate

        //action bar declaration
        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("Feeds");

        //recycler view declaration
        mRecyclerView =(RecyclerView) findViewById(R.id.recyclerView);

        //this makes sure the change of size in RecyclerView is constant no matter what the input is
        mRecyclerView.setHasFixedSize(true);

        //set layout as linearlayout
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //send query to the firebase-database
        mReference  = FirebaseDatabase.getInstance().getReference().child("Data");

        searchEditText = findViewById(R.id.search_bar);

        search_btn = findViewById(R.id.search_btn);

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchData =  searchEditText.getText().toString();
                searchData(searchData);
                Toast.makeText(HomeActivity.this, "Clicked",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();

        // create query to retrieve data from database
        FirebaseRecyclerOptions<Items> options =
                new FirebaseRecyclerOptions.Builder<Items>()
                        .setQuery(mReference, Items.class)
                        .build();
            mFirebaseRecycler = new FirebaseRecyclerAdapter<Items, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final Items model) {
                holder.titleTV.setText(model.getTitle());
                holder.descriptionTV.setText(model.getDescription());

                // call the picasso library to retrieve photos from database
                Picasso.get().load(model.getImage()).into(holder.imageView);

                //set onclick listener for single items
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);

                        //send data from one activity to another
                        intent.putExtra("pid", model.getPid());
                        startActivity(intent);


                    }
                });
            }

            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items_row, viewGroup, false);
                ViewHolder viewView =new ViewHolder(mView);
                return viewView;
            }
        };
        mRecyclerView.setAdapter(mFirebaseRecycler);
        mFirebaseRecycler.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRecycler.stopListening();
    }

    private void searchData(String searchText)
    {
        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("Data");
        Query query = reference.orderByChild("title").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerOptions<Items> options = new FirebaseRecyclerOptions.Builder<Items>()
                        .setQuery(query, Items.class)
                        .build();
        mFirebaseRecycler = new FirebaseRecyclerAdapter<Items, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Items model) {
                holder.titleTV.setText(model.getTitle());
                holder.descriptionTV.setText(model.getDescription());

                // call the picasso library to retrieve photos from database
                Picasso.get().load(model.getImage()).into(holder.imageView);
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.items_row, parent, false);
                return new ViewHolder(view);
            }
        };

        mRecyclerView.setAdapter(mFirebaseRecycler);
        mFirebaseRecycler.notifyDataSetChanged();
        mFirebaseRecycler.startListening();
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
