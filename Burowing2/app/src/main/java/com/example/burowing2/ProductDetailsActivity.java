package com.example.burowing2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.burowing2.Models.Items;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView details_image;
    private TextView details_title, details_price, details_description;
    private Button request_btn;
    private String pId="";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;
    private Uri imgPath;
    private StorageTask uploadTask;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        //firebase
        ref =FirebaseDatabase.getInstance().getReference().child("Data");

        details_image = (ImageView) findViewById(R.id.products_image);
        details_title = (TextView) findViewById(R.id.details_title);
        details_price = (TextView) findViewById(R.id.details_price);
        details_description = (TextView) findViewById(R.id.details_description);
        request_btn = (Button) findViewById(R.id.requestBtn);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("Requests");


        //receive data from HomeActivity
        if(getIntent() != null)
        {
            pId = getIntent().getStringExtra("pid");
        }
        if (!pId.isEmpty())
        {
            //retrieve product by checking "pId"
            retrieveProducts(pId);
        }

        //back button
        getSupportActionBar().setTitle("Product Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //send request button click listener
        request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

    }


    //send request method
    private void sendRequest() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Requests");
        //get id of the recently posted item
        String requestID = reference.push().getKey();

        //create an string - object pair in the database
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("pid", requestID);
        hashMap.put("title", details_title.getText().toString());
        hashMap.put("price", details_price.getText().toString());
        hashMap.put("description", details_description.getText().toString());
        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.child(requestID).setValue(hashMap);
        Toast.makeText(ProductDetailsActivity.this,"Sent request successfully", Toast.LENGTH_SHORT).show();
        finish();

    }


    private void retrieveProducts(String pId) {

        //search for a specific product
        ref.child(pId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check if the item exists

                    Items products = dataSnapshot.getValue(Items.class);
                    if (products!=null) {
                        // populate the view with data which got from "items" class
                        details_title.setText(products.getTitle());
                        details_price.setText(products.getPrice());
                        details_description.setText(products.getDescription());
                        Picasso.get().load(products.getImage()).into(details_image);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
