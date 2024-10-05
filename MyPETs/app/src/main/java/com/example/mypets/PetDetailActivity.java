package com.example.mypets;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mypets.Model.Pet;

public class PetDetailActivity extends AppCompatActivity {

    public static final String KEY_PET_DETAILS_DISPLAY = "key_pet_details";
    private static final String TAG = "PetDetailActivity";

    private TextView tvPetName, tvPetInfo, tvPetWeight, tvHeartRate, tvTemperature;
    private Button btnEditPet, btnBack;

    private Pet pet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);

        Intent intent = getIntent();
        pet = (Pet) intent.getSerializableExtra(KEY_PET_DETAILS_DISPLAY);

        initView();

        btnBack.setOnClickListener(v -> onBackPressed());

        btnEditPet.setOnClickListener(v -> {
            Log.d(TAG, "onCreate: press btn edit pet " + pet.getId() + " " + pet.getName());

            Intent intentToEditPet = new Intent(this, AddPetActivity.class);
            intentToEditPet.putExtra(AddPetActivity.KEY_PET_DETAILS_EDIT, pet);
            startActivity(intentToEditPet);
        });
    }

    public void initView() {
        // find widget
        tvPetName = findViewById(R.id.tv_pet_name);
        tvPetInfo = findViewById(R.id.tv_pet_info);
        tvPetWeight = findViewById(R.id.tv_pet_weight);
        tvHeartRate = findViewById(R.id.tv_heart_rate);
        tvTemperature = findViewById(R.id.tv_temperature);

        btnBack = findViewById(R.id.btn_back);
        btnEditPet = findViewById(R.id.btn_edit_pet);

        // set text
        tvPetName.setText(pet.getName());
        tvPetInfo.setText(pet.getBreed() + ", " + pet.getAge() + " years");
        tvPetWeight.setText(String.valueOf(pet.getWeight()) + " kg");
        tvHeartRate.setText("120 bpm");
        tvTemperature.setText("101.5 F");
    }
}