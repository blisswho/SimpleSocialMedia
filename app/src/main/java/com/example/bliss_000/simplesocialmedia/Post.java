package com.example.bliss_000.simplesocialmedia;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Post {

    private String message;
    private String user;
    private String userID;
    private String imageURL;
    private boolean hasContent;
    private String profileImageURL;
    private String date;


    public Post(){

    }

    public Post(String mess, String dat, String use, String useID, boolean hasCont, String imageU, String proPic){

        message = mess;
        date = dat;
        user = use;
        userID = useID;
        imageURL = imageU;
        hasContent = hasCont;
        profileImageURL = proPic;

    }

    public String getDate(){
        return date;
    }
    public String getMessage(){
        return message;
    }
    public String getUser(){
        return user;
    }
    public String getUserID() {
        return userID;
    }
    public String getImageURL(){
        return imageURL;
    }
    public boolean getHasContent(){
        return hasContent;
    }
    public String getProfileImageURL(){
        return profileImageURL;
    }
    public void setProfileImageURL(String purl){
        profileImageURL = purl;
    }

    public static ArrayList<Post> createPostsList(){

        ArrayList<Post> postArrayList = new ArrayList<Post>();


        return postArrayList;
    }



}