package com.example.mypets.Fragments;

import android.annotation.SuppressLint;
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
import com.example.mypets.R;

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
    private HashSet<String> addressMap;
    private List<ScanResult> scanResults;
    private BleDeviceAdapter bleDeviceAdapter;

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            String deviceAddress = result.getDevice().getAddress();

            @SuppressLint("MissingPermission") String deviceName = result.getDevice().getName();

            if (!addressMap.contains(deviceAddress) && deviceName != null) {
                addressMap.add(deviceAddress);
                scanResults.add(result);
                bleDeviceAdapter.notifyDataSetChanged();
            }
        }
    };

    private Button btnScan;
    private RecyclerView rvBleDevices;
    private boolean scanning = false;

    public FragmentScanBle() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_ble, container, false);

        btnScan = view.findViewById(R.id.btn_scan_ble);
        rvBleDevices = view.findViewById(R.id.rv_ble_devices);
        rvBleDevices.setLayoutManager(new LinearLayoutManager(getContext()));

        addressMap = new HashSet<>();
        scanResults = new ArrayList<>();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            // Hiển thị thông báo yêu cầu người dùng bật Bluetooth
            Log.d(TAG, "onViewCreated: yêu cầu bật Bluetooth");
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
        }

        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        bleDeviceAdapter = new BleDeviceAdapter(scanResults, position -> {
            Log.d(TAG, "onViewCreated: click device position: " + position);
            ScanResult device = scanResults.get(position);

//            Intent intent = new Intent(this, DeviceControlActivity.class);
//            intent.putExtra(DeviceControlActivity.KEY_DEVICE_ADDRESS, device.getDevice().getAddress());
//            startActivity(intent);
        });
        rvBleDevices.setAdapter(bleDeviceAdapter);

        btnScan.setOnClickListener(v -> {
            // bắt đầu quét
            Log.d(TAG, "onViewCreated: click scan");
            startBleScan();
        });

    }


    @SuppressLint("MissingPermission")
    private void startBleScan() {
        if (!scanning) {
            addressMap.clear();
            scanResults.clear();

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
}