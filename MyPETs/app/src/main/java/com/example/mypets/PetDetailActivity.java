package com.example.mypets;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mypets.Model.Pet;

public class PetDetailActivity extends AppCompatActivity {
    TextView tvTen;
    Button scan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);
        init();
        Intent intent = getIntent();
        Pet pet = (Pet) intent.getSerializableExtra("item");
        tvTen.setText(pet.getName() + "!");
    }

    public void init() {
        tvTen = findViewById(R.id.tvPet);
    }
}