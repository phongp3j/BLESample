package com.example.mypets.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypets.Model.HealthData;
import com.example.mypets.R;

import java.util.List;

public class HealthDataAdapter extends RecyclerView.Adapter<HealthDataAdapter.ViewHolder> {

    private List<HealthData> healthDataList;

    public HealthDataAdapter(List<HealthData> healthDataList) {
        this.healthDataList = healthDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_health_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HealthData data = healthDataList.get(position);
        holder.tvHeartRate.setText("Heart Rate: " + String.valueOf(data.getHeartRate()) + " bpm");
        holder.tvTemperature.setText("Temperature: " + String.valueOf(data.getTemperature()) + "°C");
        holder.tvTimestamp.setText(data.getRecordedAt());
    }

    @Override
    public int getItemCount() {
        return healthDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeartRate, tvTemperature, tvTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeartRate = itemView.findViewById(R.id.tvHeartRate);
            tvTemperature = itemView.findViewById(R.id.tvTemperature);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}
