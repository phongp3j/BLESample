package com.example.mypets;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mypets.Fragments.FragmentAddPet;
import com.example.mypets.Fragments.FragmentScanBle;
import com.example.mypets.Model.Pet;

public class AddPetActivity extends AppCompatActivity {

    public static final String KEY_DEVICE_ADDRESS = "key_device_address";
    public static final String KEY_PET_DETAILS_EDIT = "key_pet_details_edit";

    private static final String TAG = "AddPetActivity";

    private String mDeviceAddress;
    private Pet mPetDetailsEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra(KEY_DEVICE_ADDRESS);
        mPetDetailsEdit = (Pet) intent.getSerializableExtra(KEY_PET_DETAILS_EDIT);

        // todo chuyen sang FragmentAddPet
        if (savedInstanceState == null) {
            FragmentAddPet addPetFragment = FragmentAddPet.newInstance(mDeviceAddress, mPetDetailsEdit);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_container, addPetFragment)
                    .commit();
        }
    }

    public void showListDeviceFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_container, new FragmentScanBle())
                .addToBackStack(null)
                .commit();
    }

    public void returnToAddPetFragment(String selectedAddress) {
        FragmentAddPet addPetFragment = FragmentAddPet.newInstance(selectedAddress, mPetDetailsEdit);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_container, addPetFragment)
                .commit();
    }
}