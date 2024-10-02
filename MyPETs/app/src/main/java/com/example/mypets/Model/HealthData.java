package com.example.mypets.Model;

public class HealthData {

    private int id;
    private int deviceId;
    private int heartRate;          // unit: bpm
    private float temperature;      // unit: độ C
    private String recordedAt;

    public HealthData() {
    }

    public HealthData(int id, int deviceId, int heartRate, float temperature, String recordedAt) {
        this.id = id;
        this.deviceId = deviceId;
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

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
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
