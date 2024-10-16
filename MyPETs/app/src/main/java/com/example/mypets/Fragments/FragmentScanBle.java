package com.example.mypets.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypets.Adapters.BleDeviceAdapter;
import com.example.mypets.Adapters.SavedBleDeviceAdapter;
import com.example.mypets.AddPetActivity;
import com.example.mypets.Data.DataManager;
import com.example.mypets.MainActivity;
import com.example.mypets.Model.Pet;
import com.example.mypets.R;
import com.example.mypets.SQLite.PetDao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FragmentScanBle extends Fragment {

    private static final String TAG = "FragmentScanBle";

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int REQUEST_PERMISSION = 2;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;      // 10 giây
    private final Handler handler = new Handler();

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;

    private List<Pet> allPet, savedPetList;
    private List<ScanResult> savedScanResults;

    private HashSet<String> addressMap;
    private List<ScanResult> scanResults;

    private BleDeviceAdapter bleDeviceAdapter;
    private SavedBleDeviceAdapter savedDeviceAdapter;
    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            String deviceAddress = result.getDevice().getAddress();

            @SuppressLint("MissingPermission") String deviceName = result.getDevice().getName();
            if (!addressMap.contains(deviceAddress) && deviceName != null) {
                int findIndex = findSavedDevice(deviceAddress);

                if (findIndex != -1) {
                    if (!savedPetList.contains(allPet.get(findIndex))) {
                        savedPetList.add(allPet.get(findIndex));
                        savedScanResults.add(result);
                        savedDeviceAdapter.notifyDataSetChanged();
                    }
                } else {
                    addressMap.add(deviceAddress);
                    scanResults.add(result);
                    bleDeviceAdapter.notifyDataSetChanged();
                }
            }
        }
    };
    private PetDao petDao;
    private Button btnScan;
    private RecyclerView rvBleDevices, rvSavedBleDevices;
    private boolean scanning = false;

    public FragmentScanBle() {
    }

    private int findSavedDevice(String deviceAddress) {
        for (int i = 0; i < allPet.size(); i++) {
            if (deviceAddress.equals(allPet.get(i).getDeviceAddress()))
                return i;
        }
        return -1;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            // Hiển thị thông báo yêu cầu người dùng bật Bluetooth
            Log.d(TAG, "onViewCreated: yêu cầu bật Bluetooth");
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
        }

        petDao = new PetDao(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        allPet = petDao.getAll(DataManager.getInstance().getUserId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_ble, container, false);

        btnScan = view.findViewById(R.id.btn_scan_ble);
        rvBleDevices = view.findViewById(R.id.rv_ble_devices);
        rvSavedBleDevices = view.findViewById(R.id.rv_saved_ble_devices);

        rvBleDevices.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSavedBleDevices.setLayoutManager(new LinearLayoutManager(getContext()));

        allPet = new ArrayList<>();
        savedPetList = new ArrayList<>();
        savedScanResults = new ArrayList<>();

        addressMap = new HashSet<>();
        scanResults = new ArrayList<>();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (bluetoothAdapter != null)
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        // todo check nhấn ble device
        bleDeviceAdapter = new BleDeviceAdapter(scanResults, position -> {
            Activity currActivity = getActivity();

            if (currActivity instanceof MainActivity) {
                // Fragment được gọi từ MainActivity
                Log.d(TAG, "MainActivity: click device position: " + position);
                ScanResult scanResult = scanResults.get(position);

                Intent intent = new Intent(getContext(), AddPetActivity.class);
                intent.putExtra(AddPetActivity.KEY_DEVICE_ADDRESS, scanResult.getDevice().getAddress());
                startActivity(intent);
            } else if (currActivity instanceof AddPetActivity) {
                // Fragment được gọi từ AddPetActivity
                Log.d(TAG, "AddPetActivity: click device position: " + position);
                ScanResult scanResult = scanResults.get(position);

                ((AddPetActivity) currActivity).returnToAddPetFragment(scanResult.getDevice().getAddress());
            }

        });
        rvBleDevices.setAdapter(bleDeviceAdapter);

        // ble device saved
        savedDeviceAdapter = new SavedBleDeviceAdapter(savedScanResults, savedPetList);
        rvSavedBleDevices.setAdapter(savedDeviceAdapter);

        btnScan.setOnClickListener(v -> {
            // bắt đầu quét
            Log.d(TAG, "onViewCreated: click scan");
            startBleScan();
        });

    }


    @SuppressLint("MissingPermission")
    private void startBleScan() {
        if (bluetoothLeScanner == null)
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        if (!scanning) {
            savedPetList.clear();
            savedScanResults.clear();
            savedDeviceAdapter.notifyDataSetChanged();

            addressMap.clear();
            scanResults.clear();
            bleDeviceAdapter.notifyDataSetChanged();

            btnScan.setText("SCANNING...");
            btnScan.setEnabled(false);

            Log.d(TAG, "startBleScan: __");

            // Stops scanning after a predefined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopBleScan();
                }
            }, SCAN_PERIOD);

            scanning = true;
            bluetoothLeScanner.startScan(scanCallback);
        }
    }

    @SuppressLint("MissingPermission")
    private void stopBleScan() {
        if (bluetoothLeScanner == null)
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        if (scanning) {
            btnScan.setText("SCAN");
            btnScan.setEnabled(true);

            Log.d(TAG, "stopBleScan: __");

            scanning = false;
            bluetoothLeScanner.stopScan(scanCallback);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopBleScan();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (petDao != null)
            petDao.close();
    }
}