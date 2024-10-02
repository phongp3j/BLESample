package com.example.mypets.Model;

import com.example.mypets.Utils.PasswordUtils;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String username;
    private String hashedPassword;    // mật khẩu đã được băm
    private String createdAt;

    public User() {
    }

    public User(int id, String username, String rawPassword, String createdAt) {
        this.id = id;
        this.username = username;
        this.hashedPassword = PasswordUtils.hashPassword(rawPassword);
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String rawPassword) {
        this.hashedPassword = PasswordUtils.hashPassword(rawPassword);
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
