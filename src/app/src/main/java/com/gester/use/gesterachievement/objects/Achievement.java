package com.gester.use.gesterachievement.objects;

/**
 * Created by USE on 29.06.2015.
 */
public class Achievement {
    private long id;


    private String name;
    private double locationN;
    private double locationE;
    private int gfxId;
    private String description;


    public Achievement (){

    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public double getLocationN() {
        return locationN;
    }


    public void setLocationN(double location) {
        this.locationN = location;
    }

    public double getLocationE() {
        return locationE;
    }


    public void setLocationE(double location) {
        this.locationE = location;
    }


    public int getGfxId() {
        return gfxId;
    }


    public void setGfxId(int gfxId) {
        this.gfxId = gfxId;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }
}
