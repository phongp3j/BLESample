package com.example.mypets.Adapters;

import android.annotation.SuppressLint;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypets.R;

import java.util.List;

public class BleDeviceAdapter extends RecyclerView.Adapter<BleDeviceAdapter.ViewHolder> {

    private OnItemClickListener listener;
    private List<ScanResult> devices;

    public BleDeviceAdapter(List<ScanResult> devices, OnItemClickListener listener) {
        this.listener = listener;
        this.devices = devices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_devices, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScanResult device = devices.get(position);

        holder.deviceName.setText("Permission required");
        holder.deviceAddress.setText("Unknown Address");

        @SuppressLint("MissingPermission") String deviceName = device.getDevice().getName();
        String deviceAddress = device.getDevice().getAddress();
        int deviceRssi = device.getRssi();

//        Cài đặt icon độ mạnh của sóng
//        if (rssi > -50) {
//            holder.signalIcon.setImageResource(R.drawable.ic_signal_strong);
//        } else if (rssi > -70) {
//            holder.signalIcon.setImageResource(R.drawable.ic_signal_medium);
//        } else {
//            holder.signalIcon.setImageResource(R.drawable.ic_signal_weak);
//        }

        holder.deviceName.setText(deviceName != null ? deviceName : "Unknown Device");
        holder.deviceAddress.setText(deviceAddress);
        holder.deviceRssi.setText("RSSI: " + deviceRssi);

        // Nhấn vào itemView hay btnAddPet đều chuyển sang màn hình Add pet
        holder.btnAddPet.setOnClickListener(view -> listener.onItemClick(position));

        holder.itemView.setOnClickListener(view -> listener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRssi;
        Button btnAddPet;

        ViewHolder(View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.device_name);
            deviceAddress = itemView.findViewById(R.id.device_address);
            deviceRssi = itemView.findViewById(R.id.device_rssi);
            btnAddPet = itemView.findViewById(R.id.btn_add_pet);
        }
    }
}
