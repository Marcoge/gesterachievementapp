package com.gester.use.gesterachievement;

import android.graphics.drawable.Drawable;

/**
 * Created by USE on 10.06.2015.
 */
public class DistanceViewObject {

    private String name;
    private String distance;
    private Drawable picture;

    public DistanceViewObject(String name, String distance, Drawable picture){
        this.name = name;
        this.distance = distance;
        this.picture = picture;
    }

    public String getName(){
        return this.name;
    }

    public String getDistance(){
        return this.distance;
    }

    public Drawable getPicture(){
        return this.picture;
    }

}
