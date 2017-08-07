package com.example.bliss_000.simplesocialmedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by bliss_000 on 8/6/2017.
 */

public class FeedActivity extends Activity {

    FloatingActionButton createNewPost;
    ArrayList<Post> posts;
    FirebaseDatabase database;
    PostsAdapter adapter;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        createNewPost = (FloatingActionButton) findViewById(R.id.floatingButton);

        user = FirebaseAuth.getInstance().getCurrentUser();

        posts = new ArrayList<Post>();

        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("posts").child(user.getUid());



        //Setting up RecyclerView
        RecyclerView rv = (RecyclerView) findViewById(R.id.rvPosts);
        adapter = new PostsAdapter(FeedActivity.this, posts);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(FeedActivity.this));


        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Post post = dataSnapshot.getValue(Post.class);
//                System.out.println(post);
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    Log.i("tag", post.getDate());
                    Log.i("tag", post.getMessage());
                    posts.add(post);
                    adapter.notifyItemInserted(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        createNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FeedActivity.this, NewPostActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //End of the stupid bullshit


    }
}
