package com.example.mypets.Adapters;

import android.annotation.SuppressLint;
import android.bluetooth.le.ScanResult;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypets.Model.Pet;
import com.example.mypets.R;

import java.util.List;

public class SavedBleDeviceAdapter extends RecyclerView.Adapter<SavedBleDeviceAdapter.ViewHolder> {

    private List<ScanResult> savedDevices;
    private List<Pet> savedPet;

    public SavedBleDeviceAdapter(List<ScanResult> savedDevices, List<Pet> savedPet) {
        this.savedDevices = savedDevices;
        this.savedPet = savedPet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_devices, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScanResult device = savedDevices.get(position);
        Pet pet = savedPet.get(position);

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

        if (pet.getImagePath() != null) {
            holder.imgPetImage.setImageURI(Uri.parse(pet.getImagePath()));
        }
    }

    @Override
    public int getItemCount() {
        return savedDevices.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRssi;
        ImageView imgPetImage;

        ViewHolder(View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.device_name);
            deviceAddress = itemView.findViewById(R.id.device_address);
            deviceRssi = itemView.findViewById(R.id.device_rssi);
            imgPetImage = itemView.findViewById(R.id.im_pet_image);
        }
    }
}
