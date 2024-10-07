package com.example.mypets.Utils;

public class BluetoothGattUtils {

    public static boolean isHeartRateMeasurement(String uuid) {
        return uuid.toLowerCase().equals("00002a37-0000-1000-8000-00805f9b34fb");
    }

    public static boolean isBatteryLevel(String uuid) {
        return uuid.toLowerCase().equals("00002a19-0000-1000-8000-00805f9b34fb");
    }

    public static boolean isTemperatureMeasurement(String uuid) {
        return uuid.toLowerCase().equals("00002a1c-0000-1000-8000-00805f9b34fb");
    }

}
