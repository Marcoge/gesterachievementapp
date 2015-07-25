package com.gester.use.gesterachievement.objects;


import java.util.HashSet;
import java.util.Set;

/**
 * Created by USE on 29.06.2015.
 */
public class User {
    private long id;

    private String email;
    private String facebook;
    private String twitter;
    private String instagram;


    public User (){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public long getId() {
        return id;
    }

    public void setId(long i){
        this.id = i;
    }

}
