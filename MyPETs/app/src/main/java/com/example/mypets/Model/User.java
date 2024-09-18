package com.example.mypets.Model;

import java.io.Serializable;

public class User implements Serializable {
    int id;
    String username,password,fullname,email,phone;

    public User() {
    }

    public User(int id,String username, String password, String fullname, String email,String phone) {
        this.username = username;
        this.fullname = fullname;
        this.password = password;
        this.id = id;
        this.email = email;
        this.phone = phone;
    }

    public User(String username, String password, String fullname, String email,String phone) {
        this.username = username;
        this.fullname = fullname;
        this.password = password;
        this.email = email;
        this.phone = phone;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
