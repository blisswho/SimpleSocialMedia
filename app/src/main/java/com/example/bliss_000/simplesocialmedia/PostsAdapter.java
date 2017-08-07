package com.example.bliss_000.simplesocialmedia;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Created by bliss_000 on 7/30/2017.
 */

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{

    private List<Post> mPosts;
    // Store the context for easy access
    private Context mContext;
    private StorageReference mStorageRef;

    public PostsAdapter(Context context, List<Post> posts) {
        mPosts = posts;
        mContext = context;
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.item_post, parent, false);

        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Post post = mPosts.get(position);

        TextView dateView = holder.date;
        dateView.setText(post.getDate());

        TextView messageView = holder.message;
        messageView.setText(post.getMessage());

        TextView profileNameView = holder.profileName;
        profileNameView.setText(post.getUser());

        ImageView profilePic = holder.profile;
//        if(!post.isFirebase()) {
//            Glide.with(mContext)
//                    .load(post.getProfileImageURL())
//                    .into(profilePic);
//        }else{
            StorageReference photoRef = mStorageRef.child("Pictures")
                    .child(post.getProfileImageURL());

            Glide.with(mContext)
                    .using(new FirebaseImageLoader())
                    .load(photoRef)
                    .into(profilePic);
//        }

        ImageView contentPic = holder.content;

//        if(!post.isFirebase()) {
//            Glide.with(mContext)
//                    .load(post.getImageURL())
//                    .into(contentPic);
//        }else{
            StorageReference photoRef2 = mStorageRef.child("Pictures")
                    .child(post.getImageURL());

            Glide.with(mContext)
                    .using(new FirebaseImageLoader())
                    .load(photoRef2)
                    .into(contentPic);
//        }
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView message;
        public ImageView profile;
        public ImageView content;
        public TextView profileName;
        public TextView date;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            message = (TextView) itemView.findViewById(R.id.Message);
            profile = (ImageView) itemView.findViewById(R.id.profilePicture);
            content = (ImageView) itemView.findViewById(R.id.postContent);
            profileName = (TextView) itemView.findViewById(R.id.profileName);
            date = (TextView) itemView.findViewById(R.id.postDate);
        }
    }
}