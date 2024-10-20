package com.example.mypets;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypets.Adapters.HealthDataAdapter;
import com.example.mypets.Model.HealthData;
import com.example.mypets.SQLite.HealthDataDao;

import java.util.List;

public class HealthDataActivity extends AppCompatActivity {

    public static final String KEY_PET_ID = "KEY_PET_ID";
    private static final String TAG = "HealthDataActivity";

    private int petId;
    private HealthDataDao healthDataDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_data);

        Intent intent = getIntent();
        petId = intent.getIntExtra(KEY_PET_ID, -1);

        healthDataDao = new HealthDataDao(this);

        if (petId != -1) {
            RecyclerView recyclerView = findViewById(R.id.rv_health_data);
            List<HealthData> healthDataList = healthDataDao.getAllByPetId(petId);
            if (healthDataList != null) {
                HealthDataAdapter adapter = new HealthDataAdapter(this, healthDataList);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(adapter);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (healthDataDao != null)
            healthDataDao.close();
    }
}