package com.example.mypets.Model;

import java.io.Serializable;

public class Reminder implements Serializable {

    private int id;
    private int petId;
    private String type;    // "vaccination" hoặc "health checkup"
    private String date;
    private String time;
    private int hasDone;    // 0: false, 1: true
    private String createdAt;

    public Reminder() {
    }

    public Reminder(int id, int petId, String type, String date, String time, int hasDone, String createdAt) {
        this.id = id;
        this.petId = petId;
        this.type = type;
        this.date = date;
        this.time = time;
        this.hasDone = hasDone;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int isHasDone() {
        return hasDone;
    }

    public void setHasDone(int hasDone) {
        this.hasDone = hasDone;
    }
}
