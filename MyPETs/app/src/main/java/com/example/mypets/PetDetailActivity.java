package com.example.mypets;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mypets.Model.HealthData;
import com.example.mypets.Model.Pet;
import com.example.mypets.Model.SensorState;
import com.example.mypets.SQLite.HealthDataDao;
import com.example.mypets.SQLite.PetDao;
import com.example.mypets.Services.BluetoothLeService;
import com.example.mypets.Utils.BluetoothGattUtils;

import java.util.List;

public class PetDetailActivity extends AppCompatActivity {

    public static final String KEY_PET_DETAILS_DISPLAY = "key_pet_details";
    private static final String TAG = "PetDetailActivity";


    private TextView tvConnectionState, tvPetName, tvPetInfo, tvPetWeight, tvHeartRate, tvTemperature, tvNote, btnShowHistory;
    private Button btnEditPet, btnBack;
    private ImageView ivPetImage;

    private Pet pet;
    private PetDao petDao;
    private HealthDataDao healthDataDao;
    private HealthData currHealthData;
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

                // Lưu dữ liệu nhận được
                saveData();
            }
        }
    };

    private void saveData() {
        Log.d(TAG, "saveData: receive " + currHealthData.getHeartRate() + " bpm, " + currHealthData.getTemperature() + " *C");
        if (currHealthData.getHeartRate() == 0) {
            return;
        }
        if (currHealthData.getTemperature() == 0) {
            return;
        }

        healthDataDao.add(currHealthData);
        Log.d(TAG, "saveData: successfully");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);

        petDao = new PetDao(this);
        healthDataDao = new HealthDataDao(this);

        Intent intent = getIntent();
        pet = (Pet) intent.getSerializableExtra(KEY_PET_DETAILS_DISPLAY);
        deviceAddress = pet.getDeviceAddress();

        currHealthData = new HealthData();
        currHealthData.setPetId(pet.getId());

        initView();

        btnBack.setOnClickListener(v -> onBackPressed());

        btnEditPet.setOnClickListener(v -> {
            Log.d(TAG, "onCreate: press btn edit pet id = " + pet.getId() + ", name = " + pet.getName());

            Intent intentToEditPet = new Intent(this, AddPetActivity.class);
            intentToEditPet.putExtra(AddPetActivity.KEY_DEVICE_ADDRESS, pet.getDeviceAddress());
            intentToEditPet.putExtra(AddPetActivity.KEY_PET_DETAILS_EDIT, pet);
            startActivity(intentToEditPet);
        });

        if (!isConnected) {
            Log.d(TAG, "onCreate: press btn connect");
            updateSensorStatus(SensorState.CONNECTING);
            Intent serviceIntent = new Intent(PetDetailActivity.this, BluetoothLeService.class);
            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }

        findViewById(R.id.btn_reminder).setOnClickListener(view -> {
            Intent toReminder = new Intent(this, ReminderActivity.class);
            toReminder.putExtra(ReminderActivity.KEY_PET, pet);
            startActivity(toReminder);
        });

        btnShowHistory.setOnClickListener(view -> {
            Intent toShowHistory = new Intent(this, HealthDataActivity.class);
            toShowHistory.putExtra(HealthDataActivity.KEY_PET_ID, pet.getId());
            startActivity(toShowHistory);
        });
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

        fillData();
    }

    private void fillData() {
        pet = petDao.getById(pet.getId());

        if (pet.getImagePath() != null) {
            ivPetImage.setImageURI(Uri.parse(pet.getImagePath()));
            ivPetImage.setPadding(0, 0, 0, 0);
        }
        tvPetName.setText(pet.getName());
        tvPetInfo.setText(getResources().getString(R.string.breed_age_info, pet.getBreed(), pet.getAge()));
        tvPetWeight.setText(String.valueOf(pet.getWeight()) + " kg");

        tvNote.setText(pet.getNote());
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

        if (petDao != null)
            petDao.close();

        if (healthDataDao != null)
            healthDataDao.close();
    }

    private void initView() {
        // find widget
        ivPetImage = findViewById(R.id.iv_pet_image);
        tvConnectionState = findViewById(R.id.tv_connection_state);
        tvPetName = findViewById(R.id.tv_pet_name);
        tvPetInfo = findViewById(R.id.tv_pet_info);
        tvPetWeight = findViewById(R.id.tv_pet_weight);
        tvHeartRate = findViewById(R.id.tv_heart_rate);
        tvTemperature = findViewById(R.id.tv_temperature);
        tvNote = findViewById(R.id.tv_note);

        btnShowHistory = findViewById(R.id.btn_show_history);

        btnBack = findViewById(R.id.btn_back);
        btnEditPet = findViewById(R.id.btn_edit_pet);

        updateSensorStatus(SensorState.CONNECTING);

        tvHeartRate.setText("---");
        tvTemperature.setText("---");
    }

    public void updateSensorStatus(SensorState state) {
        String stateString = getResources().getString(R.string.connecting);
        if (state.equals(SensorState.CONNECTED)) {
            stateString = getResources().getString(R.string.connected);
        } else if (state.equals(SensorState.DISCONNECTED)) {
            stateString = getResources().getString(R.string.disconnected);
        }

        tvConnectionState.setText(stateString); // Hiển thị tên trạng thái
        tvConnectionState.setTextColor(state.getColor()); // Đổi màu theo trạng thái
    }

    private void updateData(String uuid, int data) {
        if (BluetoothGattUtils.isHeartRateMeasurement(uuid)) {
            currHealthData.setHeartRate(data);
            Log.d(TAG, "updateData: HM " + data);
            tvHeartRate.setText(data + " bpm");
        } else if (BluetoothGattUtils.isTemperatureMeasurement(uuid)) {
            currHealthData.setTemperature(data);
            Log.d(TAG, "updateData: TM " + data);
            tvTemperature.setText(data + " °C");
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