package com.example.bliss_000.simplesocialmedia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    EditText email;
    EditText password;
    EditText username;
    Button loginConfirm;
    Button chooseRegister;

    Button takePicture;
    ImageView previewProfile;
    Button confirmRegister;

    String mCurrentPhotoPath;


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    private FirebaseUser user;
    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseUser usertest = FirebaseAuth.getInstance().getCurrentUser();
        Log.i("tag", usertest.getUid());
        if (usertest != null) {
            Intent intent = new Intent(MainActivity.this, FeedActivity.class);
            startActivity(intent);
            finish();
        }



        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        email = (EditText) findViewById(R.id.emailField);
        password = (EditText) findViewById(R.id.passwordField);
        username = (EditText) findViewById(R.id.usernameField);
        loginConfirm = (Button) findViewById(R.id.loginConfirmButton);
        chooseRegister = (Button) findViewById(R.id.chooseRegisterButton);
        takePicture = (Button) findViewById(R.id.capturePicture);
        previewProfile = (ImageView) findViewById(R.id.profilePreview);
        confirmRegister = (Button) findViewById(R.id.registerConfirmButton);


        loginConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("tag",email.getText().toString());
                Log.i("tag",password.getText().toString());

                if(email.getText().toString() != null && password.getText().toString() != null)
                    loginUser(email.getText().toString(), password.getText().toString());
            }
        });
        chooseRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginConfirm.setVisibility(View.GONE);
                chooseRegister.setVisibility(View.GONE);

                takePicture.setVisibility(View.VISIBLE);
                username.setVisibility(View.VISIBLE);
            }
        });

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        confirmRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser(email.getText().toString(), password.getText().toString(), username.getText().toString());
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
            confirmRegister.setVisibility(View.VISIBLE);
            previewProfile.setVisibility(View.VISIBLE);
            setPic();
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
                .into(previewProfile);
    }




    public void createUser(String email, String password, final String userName){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("tag", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

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
                                }
                            });

                            user = FirebaseAuth.getInstance().getCurrentUser();



                            Profile newProfile = new Profile(user.getUid(), file.getLastPathSegment(), userName);

                            mDatabase.child("profiles").child(user.getUid()).setValue(newProfile);

                            Intent intent = new Intent(MainActivity.this, FeedActivity.class);
                            startActivity(intent);
                            finish();



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("tag", "Failure: "+task.getException());

                        }
                        // ...
                    }
                });


    }

    public void loginUser(String email, String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i("tag", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent intent = new Intent(MainActivity.this, FeedActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i("tag", "signInWithEmail:failure", task.getException());
                        }

                        // ...
                    }
                });
    }


}