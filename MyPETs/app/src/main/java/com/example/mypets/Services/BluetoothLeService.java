package com.example.mypets.Services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.mypets.Utils.BluetoothGattUtils;

import java.util.List;
import java.util.UUID;

public class BluetoothLeService extends Service {

    public final static String ACTION_GATT_CONNECTED =
            "com.example.mypets.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.mypets.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.mypets.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.example.mypets.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "com.example.mypets.EXTRA_DATA";
    public final static String EXTRA_UUID = "com.example.mypets.EXTRA_UUID";

    public final static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    private static final String TAG = "BluetoothLeService";
    private final IBinder binder = new LocalBinder();
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;

    // GATT callback để theo dõi các sự kiện kết nối và dịch vụ
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Kết nối thành công với GATT.");

                broadcastUpdate(ACTION_GATT_CONNECTED);

                bluetoothGatt.discoverServices(); // Tự động khám phá dịch vụ
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Đã ngắt kết nối từ GATT.");

                broadcastUpdate(ACTION_GATT_DISCONNECTED);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "Khám phá dịch vụ thành công.");
                // Xử lý danh sách dịch vụ GATT
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "[READ] Đọc thành công dữ liệu từ Characteristic.");
                // Phát sự kiện khi đọc thành công dữ liệu từ Characteristic
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "[NOTIFY] Đọc thành công dữ liệu từ Characteristic.");
            // Phát sự kiện khi giá trị của Characteristic thay đổi
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private String bluetoothDeviceAddress;

    public BluetoothLeService() {
    }

    private void broadcastUpdate(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        Intent intent = new Intent(action);

        String uuid = characteristic.getUuid().toString();
        intent.putExtra(EXTRA_UUID, uuid);

        final byte[] data = characteristic.getValue();
        int value = byteArrayToInt(data);

        Log.d(TAG, "Received int: " + value);
        intent.putExtra(EXTRA_DATA, value);

        sendBroadcast(intent);
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

    // Các phương thức để kết nối với thiết bị BLE, xử lý giao tiếp BLE
    @SuppressLint("MissingPermission")
    public boolean connect(String address) {
        if (bluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter chưa được khởi tạo hoặc địa chỉ không hợp lệ.");
            return false;
        }

        // Nếu chúng ta đã kết nối với thiết bị này rồi, hãy thử reconnect
        if (bluetoothDeviceAddress != null && address.equals(bluetoothDeviceAddress) && bluetoothGatt != null) {
            Log.d(TAG, "Đang sử dụng kết nối GATT hiện tại.");
            return bluetoothGatt.connect();
        }

        // Lấy thiết bị BLE từ địa chỉ MAC
        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Không thể tìm thấy thiết bị.");
            return false;
        }

        // Kết nối GATT với thiết bị
        bluetoothGatt = device.connectGatt(this, false, gattCallback);
        bluetoothDeviceAddress = address;
        Log.d(TAG, "Bắt đầu kết nối GATT với thiết bị.");
        return true;
    }

    @SuppressLint("MissingPermission")
    public void disconnect() {
        // Mã để ngắt kết nối với thiết bị BLE
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.disconnect();
    }


    public List<BluetoothGattService> getSupportedGattServices() {
        if (bluetoothGatt == null) return null;
        return bluetoothGatt.getServices();
    }

    @SuppressLint("MissingPermission")
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (bluetoothGatt == null) {
            Log.w(TAG, "BluetoothGatt not initialized");
            return;
        }
        bluetoothGatt.readCharacteristic(characteristic);
    }

    @SuppressLint("MissingPermission")
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic) {
        if (bluetoothGatt == null) {
            Log.w(TAG, "BluetoothGatt not initialized");
            return;
        }

        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
        bluetoothGatt.setCharacteristicNotification(characteristic, true);

        // This is specific to Heart Rate Measurement.
        if (BluetoothGattUtils.isHeartRateMeasurement(characteristic.getUuid().toString())) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(descriptor);
        }
    }

    @Override
    @SuppressLint("MissingPermission")
    public boolean onUnbind(Intent intent) {
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
        return super.onUnbind(intent);
    }

    public int byteArrayToInt(byte[] data) {
        int value = 0;
        int length = Math.min(data.length, 4);

        for (int i = 0; i < length; i++)
            value |= (data[i] & 0xFF) << (8 * (length - 1 - i));

        return value;
    }

    // Lớp con của Binder cho phép truy cập vào BluetoothLeService
    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

}