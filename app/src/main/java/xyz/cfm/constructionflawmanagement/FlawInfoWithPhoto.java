package xyz.cfm.constructionflawmanagement;

import java.io.Serializable;

public class FlawInfoWithPhoto implements Serializable {

    private String flawInfoWithPhotoKey;
    private String dong;
    private String ho;
    private String room;
    private String flawInfo;
    private String coopName;
    private String photoInString;
    private boolean checked;

    public FlawInfoWithPhoto() {
    }

    public FlawInfoWithPhoto(String flawInfoWithPhotoKey, String dong, String ho, String room, String flawInfo, String coopName, String photoInString, boolean checked) {
        this.flawInfoWithPhotoKey = flawInfoWithPhotoKey;
        this.dong = dong;
        this.ho = ho;
        this.room = room;
        this.flawInfo = flawInfo;
        this.coopName = coopName;
        this.photoInString = photoInString;
        this.checked = checked;
    }

    public String getFlawInfoWithPhotoKey() {
        return flawInfoWithPhotoKey;
    }

    public void setFlawInfoWithPhotoKey(String flawInfoWithPhotoKey) {
        this.flawInfoWithPhotoKey = flawInfoWithPhotoKey;
    }

    public String getDong() {
        return dong;
    }

    public void setDong(String dong) {
        this.dong = dong;
    }

    public String getHo() {
        return ho;
    }

    public void setHo(String ho) {
        this.ho = ho;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getFlawInfo() {
        return flawInfo;
    }

    public void setFlawInfo(String flawInfo) {
        this.flawInfo = flawInfo;
    }

    public String getCoopName() {
        return coopName;
    }

    public void setCoopName(String coopName) {
        this.coopName = coopName;
    }

    public String getPhotoInString() {
        return photoInString;
    }

    public void setPhotoInString(String photoInString) {
        this.photoInString = photoInString;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
