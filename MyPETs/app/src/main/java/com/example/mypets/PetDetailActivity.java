package com.example.mypets;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mypets.Model.Pet;

public class PetDetailActivity extends AppCompatActivity {

    public static final String KEY_PET_DETAILS_DISPLAY = "key_pet_details";
    private static final String TAG = "PetDetailActivity";
    private Button btnEditPet;

    private Pet pet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);

        Intent intent = getIntent();
        pet = (Pet) intent.getSerializableExtra(KEY_PET_DETAILS_DISPLAY);

        initView();

        btnEditPet.setOnClickListener(v -> {
            Log.d(TAG, "onCreate: press btn edit pet " + pet.getId() + " " + pet.getName());

            Intent intentToEditPet = new Intent(this, AddPetActivity.class);
            intentToEditPet.putExtra(AddPetActivity.KEY_PET_DETAILS_EDIT, pet);
            startActivity(intentToEditPet);
        });
    }

    public void initView() {
        btnEditPet = findViewById(R.id.btn_edit_pet);
    }
}