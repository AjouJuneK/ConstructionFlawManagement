package xyz.cfm.constructionflawmanagement;

public class Cooperation {

    String coopKey;
    String coopName;

    public Cooperation() {
    }

    public Cooperation(String coopKey, String coopName) {
        this.coopKey = coopKey;
        this.coopName = coopName;
    }

    public String getCoopKey() {
        return coopKey;
    }

    public String getCoopName() {
        return coopName;
    }

    public void setCoopKey(String coopKey) {
        this.coopKey = coopKey;
    }

    public void setCoopName(String coopName) {
        this.coopName = coopName;
    }
}
