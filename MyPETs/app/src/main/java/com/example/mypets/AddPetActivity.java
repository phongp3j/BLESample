package com.example.mypets;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mypets.Data.DataManager;
import com.example.mypets.Model.Pet;
import com.example.mypets.SQLite.PetDao;

public class AddPetActivity extends AppCompatActivity {

    public static final String KEY_DEVICE_ADDRESS = "key_device_address";
    public static final String KEY_PET_DETAILS_EDIT = "key_pet_details_edit";

    private static final String TAG = "AddPetActivity";

    private EditText edtDeviceAddress, edtPetName, edtPetAge, edtPetBreed,    // chủng loài
            edtPetWeight, edtHealthInfo;

    private PetDao petDao;
    private Pet pet;

    private Button btnBack, btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        initView();

        Intent intent = getIntent();
        String deviceAddress = intent.getStringExtra(KEY_DEVICE_ADDRESS);
        pet = (Pet) intent.getSerializableExtra(KEY_PET_DETAILS_EDIT);

        if (pet == null) {
            // add pet mode
            edtDeviceAddress.setText(deviceAddress);

            btnAdd.setText("Add pet");
        } else {
            // edit pet mode
            edtDeviceAddress.setText(pet.getDeviceAddress());
            edtPetName.setText(pet.getName());
            edtPetAge.setText(String.valueOf(pet.getAge()));
            edtPetBreed.setText(pet.getBreed());
            edtPetWeight.setText(String.valueOf(pet.getWeight()));
            edtHealthInfo.setText("");

            btnAdd.setText("Edit pet");
        }

        btnBack.setOnClickListener(view -> onBackPressed());

        btnAdd.setOnClickListener(view -> {
            Log.d(TAG, "onCreate: press button add pet");

            petDao = new PetDao(this);

            if (pet == null) {
                Log.d(TAG, "onCreate: -- add pet");
                pet = new Pet();
                getData();

                if (petDao.add(pet) < 0) {
                    Log.e(TAG, "onCreate: failed to add pet");
                    Toast.makeText(this, "Thêm thú cưng thất bại!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Thêm thú cưng thành công!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onCreate: add pet successful");
                    finish();
                }
            } else {
                Log.d(TAG, "onCreate: -- edit pet");
                getData();

                if (petDao.update(pet)<0){
                    Log.e(TAG, "onCreate: failed to edit pet");
                    Toast.makeText(this, "Cập nhật thú cưng thất bại!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Cập nhật thú cưng thành công!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onCreate: edit pet successful");
                    finish();
                }
            }


        });
    }

    private void getData() {
        pet.setUserId(DataManager.getInstance().getUserId());
        pet.setName(edtPetName.getText().toString());
        pet.setAge(Integer.parseInt(edtPetAge.getText().toString()));
        pet.setBreed(edtPetBreed.getText().toString());
        pet.setWeight(Float.parseFloat(edtPetWeight.getText().toString()));
        pet.setDeviceAddress(edtDeviceAddress.getText().toString());

        Log.d(TAG, "getData: " + pet.toString());
    }

    private void initView() {
        edtDeviceAddress = findViewById(R.id.edt_device_address);
        edtPetName = findViewById(R.id.tvTen);
        edtPetAge = findViewById(R.id.tvTuoi);
        edtPetBreed = findViewById(R.id.tvType);
        edtPetWeight = findViewById(R.id.tvWeight);
        edtHealthInfo = findViewById(R.id.tvHealthInfo);

        btnBack = findViewById(R.id.btBack);
        btnAdd = findViewById(R.id.btAdd);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        petDao.close();
    }
}