package com.example.mypets.Data;

public class DataManager {
    private static DataManager instance;

    // user login
    private int userId;
    private String username;

    private DataManager() {
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public void setData(int newId, String newUsername) {
        this.userId = newId;
        this.username = newUsername;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}