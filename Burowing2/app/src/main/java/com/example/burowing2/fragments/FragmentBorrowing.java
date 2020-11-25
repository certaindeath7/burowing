package com.example.burowing2.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.burowing2.HomeActivity;
import com.example.burowing2.Models.Items;
import com.example.burowing2.ProductDetailsActivity;
import com.example.burowing2.R;
import com.example.burowing2.ViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FragmentBorrowing extends Fragment {
    private static final String TAG = "Burrowing";

    private RecyclerView recyclerView;
    private DatabaseReference mReference;
    private FirebaseRecyclerAdapter<Items, ViewHolder> mFirebaseRecycler;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_borrowing,container,false);
        recyclerView =  view.findViewById(R.id.recyclerView_borrowing);

        //this makes sure the change of size in RecyclerView is constant no matter what the input is
        recyclerView.setHasFixedSize(true);

        //set layout as linearlayout
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //send query to the firebase-database
        mReference  = FirebaseDatabase.getInstance().getReference().child("Data");


        return view;
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

            }

            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View mView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items_row, viewGroup, false);
                ViewHolder viewView =new ViewHolder(mView);
                return viewView;
            }
        };
        recyclerView.setAdapter(mFirebaseRecycler);
        mFirebaseRecycler.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRecycler.stopListening();
    }
}
