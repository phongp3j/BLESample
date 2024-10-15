package com.example.mypets.Model;

import java.io.Serializable;

public class Pet implements Serializable {
    private int id;
    private int userId;

    private String imagePath;
    private String name;
    private int age;
    private String breed;   // Chủng loại
    private float weight;   // Cân nặng
    private String note;
    private String deviceAddress;
    private String createdAt;

    public Pet() {
    }

    public Pet(int id, int userId, String name, int age, String breed, float weight, String deviceAddress, String createdAt, String imagePath, String note) {
        this.id = id;
        this.userId = userId;
        this.imagePath = imagePath;
        this.name = name;
        this.age = age;
        this.breed = breed;
        this.weight = weight;
        this.note = note;
        this.deviceAddress = deviceAddress;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
