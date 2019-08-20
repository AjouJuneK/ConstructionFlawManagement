package xyz.cfm.constructionflawmanagement;

public class User {

    String userKey;
    String userId;
    String userPw;

    public User() {
    }

    public User(String userKey, String userId, String userPw) {
        this.userKey = userKey;
        this.userId = userId;
        this.userPw = userPw;
    }

    public String getUserKey() {
        return userKey;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }

}
