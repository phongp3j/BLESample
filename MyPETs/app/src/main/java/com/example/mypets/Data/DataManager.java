package com.example.mypets.Data;

public class DataManager {
    private static DataManager instance;
    private String userLoginned ;

    private DataManager() {
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public void setData(String newData) {
        this.userLoginned = newData;
    }

    public String getData() {
        return userLoginned;
    }
}