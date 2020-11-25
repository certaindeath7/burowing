package com.example.burowing2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.burowing2.Models.UserDTO;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class ResumeActivity extends AppCompatActivity {

    private ImageView resumePicture;
    private TextView saveTv, changePhotoTv;
    private MaterialEditText resume_username;
    private FirebaseUser firebaseUser;
    private StorageReference storageRef;
    private Uri imgPath;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);

        resumePicture = findViewById(R.id.resume_picture);
        saveTv = findViewById(R.id.resume_save);
        changePhotoTv = findViewById(R.id.change_photo_tv);
        resume_username = findViewById(R.id.resume_username);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("Users");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDTO userDTO =dataSnapshot.getValue(UserDTO.class);
                resume_username.setText(userDTO.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        changePhotoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // this interface was from an external library
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(ResumeActivity.this);

            }
        });

        saveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(resume_username.getText().toString());

            }
        });
    }

    private void updateProfile(String username) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userName", username);
        reference.updateChildren(hashMap);
        Toast.makeText(ResumeActivity.this,"Updated successfully", Toast.LENGTH_SHORT).show();
    }

    // detect mime type of the uploaded picture
    private String getFileExtension (Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void editPictures() {
        // process popup
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading!!!");
        progressDialog.show();

        if(imgPath != null)
        {

            // create unique id for all the pictures and create a root folder as well
            final StorageReference stRef = storageRef.child(System.currentTimeMillis() +"." +getFileExtension(imgPath));
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
                        String myUrl = downloadUri.toString();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Data");
                        //get id of the recently posted item

                        //create an string - object pair in the database
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("image", myUrl);

                        reference.updateChildren(hashMap);
                        progressDialog.dismiss();
                        Toast.makeText(ResumeActivity.this,"Updated successfully", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ResumeActivity.this,"Error while uploading" +e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            Toast.makeText(ResumeActivity.this,"No selected picture" , Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && requestCode  == RESULT_OK)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            editPictures();
        }

        else
        {
            Toast.makeText(ResumeActivity.this,"Can't crop", Toast.LENGTH_SHORT).show();
        }
    }
}
