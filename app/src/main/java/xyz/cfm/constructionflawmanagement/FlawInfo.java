package xyz.cfm.constructionflawmanagement;

import java.io.Serializable;

public class FlawInfo implements Serializable {

    private String flawInfoKey;
    private String dong;
    private String ho;
    private String room;
    private String flawInfo;
    private String coopName;
    private boolean checked;

    public FlawInfo() {
    }

    public FlawInfo(String flawInfoKey, String dong, String ho, String room, String flawInfo, String coopName, boolean checked) {
        this.flawInfoKey = flawInfoKey;
        this.dong = dong;
        this.ho = ho;
        this.room = room;
        this.flawInfo = flawInfo;
        this.coopName = coopName;
        this.checked = checked;
    }

    public String getFlawInfoKey() {
        return flawInfoKey;
    }

    public void setFlawInfoKey(String flawInfoKey) {
        this.flawInfoKey = flawInfoKey;
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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

}
