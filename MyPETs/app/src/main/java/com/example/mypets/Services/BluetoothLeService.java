package com.example.mypets.Services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class BluetoothLeService extends Service {

    private static final String TAG = "BluetoothLeService";

    private final IBinder binder = new LocalBinder();
    private BluetoothAdapter bluetoothAdapter;

    public BluetoothLeService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public boolean initialize() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Không thể lấy BluetoothAdapter");
            return false;
        }
        return true;
    }

    // Lớp con của Binder cho phép truy cập vào BluetoothLeService
    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

}