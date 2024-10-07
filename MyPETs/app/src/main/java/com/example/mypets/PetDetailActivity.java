package com.example.mypets;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mypets.Model.Pet;
import com.example.mypets.Model.SensorState;
import com.example.mypets.Services.BluetoothLeService;
import com.example.mypets.Utils.BluetoothGattUtils;

import java.util.List;

public class PetDetailActivity extends AppCompatActivity {

    public static final String KEY_PET_DETAILS_DISPLAY = "key_pet_details";
    private static final String TAG = "PetDetailActivity";
    private TextView tvConnectionState, tvPetName, tvPetInfo, tvPetWeight, tvHeartRate, tvTemperature;
    private Button btnEditPet, btnBack;

    private Pet pet;
    private String deviceAddress;
    private BluetoothLeService bluetoothLeService;

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothLeService.LocalBinder binder = (BluetoothLeService.LocalBinder) service;
            bluetoothLeService = binder.getService();

            if (bluetoothLeService != null) {
                if (!bluetoothLeService.initialize()) {
                    Log.e(TAG, "Không thể khởi tạo Bluetooth");
                    finish();
                }

                // Kết nối tới thiết bị BLE bằng địa chỉ MAC
                bluetoothLeService.connect(deviceAddress);
                Log.d(TAG, "onServiceConnected: __");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bluetoothLeService.disconnect();
            bluetoothLeService = null;
            Log.d(TAG, "onServiceDisconnected: __");
        }
    };

    private Boolean isConnected = false;

    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.d(TAG, "onReceive: gatt connected");
                Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
                isConnected = true;
                updateSensorStatus(SensorState.CONNECTED);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.d(TAG, "onReceive: gatt disconnected");

                Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
                bluetoothLeService = null;
                isConnected = false;
                updateSensorStatus(SensorState.DISCONNECTED);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.d(TAG, "onReceive: gatt services discovered");
                registerNotify();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.d(TAG, "onReceive: data read and notify");
                String uuid = intent.getStringExtra(BluetoothLeService.EXTRA_UUID);
                int data = intent.getIntExtra(BluetoothLeService.EXTRA_DATA, 0);
                Log.d(TAG, "onReceive: __  " + uuid + ": " + data);

                // Cập nhật UI với dữ liệu nhận được
                updateData(uuid, data);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);

        Intent intent = getIntent();
        pet = (Pet) intent.getSerializableExtra(KEY_PET_DETAILS_DISPLAY);
        deviceAddress = pet.getDeviceAddress();

        initView();

        btnBack.setOnClickListener(v -> onBackPressed());

        btnEditPet.setOnClickListener(v -> {
            Log.d(TAG, "onCreate: press btn edit pet id = " + pet.getId() + ", name = " + pet.getName());

            Intent intentToEditPet = new Intent(this, AddPetActivity.class);
            intentToEditPet.putExtra(AddPetActivity.KEY_PET_DETAILS_EDIT, pet);
            startActivity(intentToEditPet);
        });

        if (!isConnected) {
            Log.d(TAG, "onCreate: press btn connect");
            updateSensorStatus(SensorState.CONNECTING);
            Intent serviceIntent = new Intent(PetDetailActivity.this, BluetoothLeService.class);
            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        filter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        filter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        filter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);

        registerReceiver(gattUpdateReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(gattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    private void initView() {
        // find widget
        tvConnectionState = findViewById(R.id.tv_connection_state);
        tvPetName = findViewById(R.id.tv_pet_name);
        tvPetInfo = findViewById(R.id.tv_pet_info);
        tvPetWeight = findViewById(R.id.tv_pet_weight);
        tvHeartRate = findViewById(R.id.tv_heart_rate);
        tvTemperature = findViewById(R.id.tv_temperature);

        btnBack = findViewById(R.id.btn_back);
        btnEditPet = findViewById(R.id.btn_edit_pet);

        updateSensorStatus(SensorState.CONNECTING);

        tvPetName.setText(pet.getName());
        tvPetInfo.setText(pet.getBreed() + ", " + pet.getAge() + " years");
        tvPetWeight.setText(String.valueOf(pet.getWeight()) + " kg");

        tvHeartRate.setText("---");
        tvTemperature.setText("---");
    }

    public void updateSensorStatus(SensorState state) {
        tvConnectionState.setText(state.name()); // Hiển thị tên trạng thái
        tvConnectionState.setTextColor(state.getColor()); // Đổi màu theo trạng thái
    }

    private void updateData(String uuid, int data) {
        if (BluetoothGattUtils.isHeartRateMeasurement(uuid)) {
            Log.d(TAG, "updateData: HM " + data);
            tvHeartRate.setText(data + " bpm");
        } else if (BluetoothGattUtils.isTemperatureMeasurement(uuid)) {
            Log.d(TAG, "updateData: TM " + data);
            tvTemperature.setText(data + " F");
        } else if (BluetoothGattUtils.isBatteryLevel(uuid)) {
            Log.d(TAG, "updateData: BL " + data);
        }
    }

    private void registerNotify() {
        if (isConnected) {
            List<BluetoothGattService> gattServices = bluetoothLeService.getSupportedGattServices();
            for (BluetoothGattService gattService : gattServices) {
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                for (BluetoothGattCharacteristic characteristic : gattCharacteristics) {
                    final int charaProp = characteristic.getProperties();

                    // Nếu có thể nhận thông báo
                    if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0)
                        bluetoothLeService.setCharacteristicNotification(characteristic);
                }
            }
        }
    }

}