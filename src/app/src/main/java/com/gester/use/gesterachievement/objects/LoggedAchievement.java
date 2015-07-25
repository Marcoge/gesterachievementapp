package com.gester.use.gesterachievement.objects;

import java.util.Date;

/**
 * Created by USE on 29.06.2015.
 */
public class LoggedAchievement {


    private long iD;
    private String userEmail;
    private long achievementID;

    private String comment;
    private String pic;
    private Date timeStamp;
    private boolean priv;

    public LoggedAchievement() {
    }

    public long getiD() {
        return iD;
    }

    public void setiD(long iD) {
        this.iD = iD;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public long getAchievementID() {
        return achievementID;
    }

    public void setAchievementID(long achievementID) {
        this.achievementID = achievementID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isPriv() {
        return priv;
    }

    public void setPriv(boolean priv) {
        this.priv = priv;
    }
}
