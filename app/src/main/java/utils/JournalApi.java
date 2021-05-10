package utils;

import android.app.Application;

public class JournalApi extends Application {
    private String userName;
    private String userId;
    private static JournalApi instance;

    public JournalApi() {
    }

    public JournalApi(String userName, String userID) {
        this.userName = userName;
        this.userId = userId;
    }

    public static JournalApi getInstance(){
        if(instance == null){
            instance = new JournalApi();
        }
        return instance;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
