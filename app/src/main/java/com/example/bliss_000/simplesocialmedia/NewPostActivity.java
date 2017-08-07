package com.example.bliss_000.simplesocialmedia;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by bliss_000 on 8/6/2017.
 */

public class NewPostActivity extends Activity {

    Button takePicture;
    EditText message;
    ImageView content;
    Button createPost;

    boolean pictureTaken;

    Profile currentUserProfile;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    String mCurrentPhotoPath;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpost);

        pictureTaken = false;

        takePicture = (Button) findViewById(R.id.capture_image_main);
        message = (EditText) findViewById(R.id.main_content_message);
        content = (ImageView) findViewById(R.id.imageContent);
        createPost = (Button) findViewById(R.id.create_post_button);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        createProfileListener();

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewPost();
            }
        });

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
            pictureTaken = true;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i("tag", ex.getLocalizedMessage());

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.bliss_000.simplesocialmedia.fileprovider",
                        //Use a Package Name from your own Android Manifest and append ".fileprovider" to it
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    private void setPic(){
        Glide.with(this)
                .load(new File(mCurrentPhotoPath)) // Uri of the picture
                .into(content);
    }

    private void createProfileListener(){
        FirebaseUser user = mAuth.getCurrentUser();

        DatabaseReference shit = FirebaseDatabase.getInstance().getReference().child("profiles").child(user.getUid());

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                currentUserProfile = dataSnapshot.getValue(Profile.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("tag", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        shit.addListenerForSingleValueEvent(postListener);

//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
//        final String formattedDate = df.format(c.getTime());

    }

    private void createNewPost(){

        String text = message.getText().toString();


        //UPLOADING IMAGE
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        Uri file = Uri.fromFile(new File(mCurrentPhotoPath));

        StorageReference riversRef = storageRef.child("Pictures/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("tag", "Failure to Upload Picture");
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.i("tag", downloadUrl.getPath());
                Log.i("tag", "Successful Picture Upload to Firebase");

                Intent intent = new Intent(NewPostActivity.this, FeedActivity.class);
                startActivity(intent);
                finish();
            }
        });


        //FORMATTING THE DATE
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
        final String formattedDate = df.format(c.getTime());

        Log.i("tag", "POST INFORMATION-----------------------------------------");
        Log.i("tag", text);
        Log.i("tag",formattedDate);
        Log.i("tag", currentUserProfile.getUsername());
        Log.i("tag", currentUserProfile.getUniqueID());
        Log.i("tag", String.valueOf(pictureTaken));
        Log.i("tag", file.getLastPathSegment());
        Log.i("tag", currentUserProfile.getImageFileName());

        //CREATING POST FOR SAVE
        Post addNewPost = new Post(
                text,
                formattedDate,
                currentUserProfile.getUsername(),
                currentUserProfile.getUniqueID(),
                pictureTaken,
                file.getLastPathSegment(),
                currentUserProfile.getImageFileName()
        );

        mDatabase.child("posts").child(currentUserProfile.getUniqueID()).child(formattedDate).setValue(addNewPost);

    }


}
