package com.example.burowing2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {

    private  BottomNavigationView bottomNavigationView;
    //initialize buttons and imageview

    private Button chooseBtn, uploadBtn, captureBtn;
    private ImageView uploadImg;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int PIC_REQUEST_CODE = 2;
    private static final int PERMISSION_CODE = 3;
    //declare firebase variable
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    private ProgressDialog progressDialog;
    private Uri imgPath;
    private EditText description, price, title;
    private StorageTask uploadTask;
    private String myUrl ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //action bar declaration
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Post Page");

        // upload pictures
        uploadBtn = (Button)findViewById(R.id.upload_btn);
        chooseBtn= (Button)findViewById(R.id.choose_btn);
        captureBtn = (Button)findViewById(R.id.capture_btn);
        uploadImg = (ImageView)findViewById(R.id.upload_img);

        //products detail upload
        description =(EditText)findViewById(R.id.products_description);
        price =(EditText)findViewById(R.id.products_price);
        title =(EditText)findViewById(R.id.products_title);

        // init firebase query
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("Data");

        //choose button listener
        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        //upload pucture listener
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPictures();
            }
        });

        //trigure camera function picture listener
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if system os is above marshmallow, request runtime permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    {
                        // request permission
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        //show notification to request permisison
                        requestPermissions(permission, PERMISSION_CODE );
                    }
                    else
                    {
                        // permission granted
                        cameraOpen();
                    }
                }
                // os is below marshmallow
                else
                {

                }
            }
        });

        // on create bottom navigation
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        Menu menu =bottomNavigationView.getMenu();
        //set id for each tab
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch ( menuItem.getItemId())
                {
                    case R.id.action_feedPage:
                        Intent intent1 = new Intent (PostActivity.this, HomeActivity.class);
                        startActivity(intent1);
                        break;

                    case R.id.action_listPage:
                        Intent intent2 = new Intent (PostActivity.this, WishListActivity.class);
                        startActivity(intent2);
                        break;

                    case R.id.action_postPage:

                        break;

                    case R.id.action_chatPage:
                        Intent intent3 = new Intent (PostActivity.this, ChatActivity.class);
                        startActivity(intent3);
                        break;

                    case R.id.action_mePage:
                        Intent intent4 = new Intent (PostActivity.this, MeActivity.class);
                        startActivity(intent4);
                        break;
                }
                return false;
            }
        });

    }

    private void cameraOpen() {
        //store a set of values
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pictures");
        imgPath = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //open camemra intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgPath);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);

    }


    // handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission on popup granted
                    cameraOpen();
                }
                else
                {
                    Toast.makeText(PostActivity.this,"Permission denied", Toast.LENGTH_SHORT).show();

                }
        }
    }

    // detect mime type of the uploaded picture
    private String getFileExtension (Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap  mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadPictures() {
        // process popup
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        if(imgPath != null)
        {

            // create unique id for all the pictures and create a root folder as well
            final StorageReference stRef = storageReference.child(System.currentTimeMillis() +"." +getFileExtension(imgPath));
            uploadTask = stRef.putFile(imgPath);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return   stRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Data");
                        //get id of the recently posted item
                        String postID = reference.push().getKey();

                        //create an string - object pair in the database
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("pid",  postID);
                        hashMap.put("image", myUrl);
                        hashMap.put("title", title.getText().toString());
                        hashMap.put("price", price.getText().toString());
                        hashMap.put("description", description.getText().toString());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        reference.child(postID).setValue(hashMap);
                        progressDialog.dismiss();
                        Toast.makeText(PostActivity.this,"Uploaded successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(PostActivity.this,"Error while uploading" +e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else
        {
            Toast.makeText(PostActivity.this,"No selected picture" , Toast.LENGTH_SHORT).show();

        }

    }

    private void chooseImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, PIC_REQUEST_CODE);
    }

    //after user taking pictures and hit ok button, it will show the activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PIC_REQUEST_CODE && resultCode == RESULT_OK && data!=null && data.getData() !=null)
        {
            imgPath = data.getData();

            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgPath);
                uploadImg.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        // take the pictures and place it in the ImageView
        else if (resultCode == RESULT_OK)
        {
            uploadImg.setImageURI(imgPath);
        }
    }


}
