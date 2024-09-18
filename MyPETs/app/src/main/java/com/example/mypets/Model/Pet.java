package com.example.mypets.Model;

import java.io.Serializable;

public class Pet implements Serializable {
    int id;
    String name,type;
    int age;
    String addressDevice;
    String userid;
    public Pet(){}
    public Pet(int id,String name,String type,int age,String addressDevice,String userid){
        this.id = id;
        this.name= name;
        this.type = type;
        this.age = age;
        this.addressDevice = addressDevice;
        this.userid = userid;
    }
    public Pet(String name,String type,int age,String addressDevice,String userid){
        this.name= name;
        this.type = type;
        this.age = age;
        this.addressDevice = addressDevice;
        this.userid = userid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddressDevice() {
        return addressDevice;
    }

    public void setAddressDevice(String addressDevice) {
        this.addressDevice = addressDevice;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
