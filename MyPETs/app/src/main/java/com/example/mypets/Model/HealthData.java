package com.example.mypets.Model;

public class HealthData {

    private int id;
    private int petId;
    private int heartRate;          // unit: bpm
    private float temperature;      // unit: độ C
    private String recordedAt;

    public HealthData() {
    }

    public HealthData(int id, int petId, int heartRate, float temperature, String recordedAt) {
        this.id = id;
        this.petId = petId;
        this.heartRate = heartRate;
        this.temperature = temperature;
        this.recordedAt = recordedAt;
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

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public String getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(String recordedAt) {
        this.recordedAt = recordedAt;
    }
}