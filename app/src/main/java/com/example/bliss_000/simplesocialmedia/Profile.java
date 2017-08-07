package com.example.bliss_000.simplesocialmedia;

/**
 * Created by bliss_000 on 8/6/2017.
 */

public class Profile {

    public String uniqueID;
    public String imageFileName;
    public String username;

    public Profile(){

    }

    public Profile(String uid, String image, String user){

        uniqueID = uid;
        imageFileName = image;
        username = user;

    }

    public String getImageFileName(){
        return imageFileName;
    }

    public String getUniqueID(){
        return uniqueID;
    }

    public String getUsername(){
        return username;
    }
}