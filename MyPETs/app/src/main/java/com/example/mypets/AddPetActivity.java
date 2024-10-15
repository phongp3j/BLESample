package com.example.mypets;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.mypets.Data.DataManager;
import com.example.mypets.Model.Pet;
import com.example.mypets.SQLite.PetDao;

import java.io.File;
import java.io.IOException;

public class AddPetActivity extends AppCompatActivity {

    public static final String KEY_DEVICE_ADDRESS = "key_device_address";
    public static final String KEY_PET_DETAILS_EDIT = "key_pet_details_edit";

    private static final String TAG = "AddPetActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SELECT_PICTURE = 2;

    private EditText edtDeviceAddress, edtPetName, edtPetAge, edtPetBreed,    // chủng loài
            edtPetWeight, edtHealthInfo;
    private ImageView imgPetImage;

    private PetDao petDao;
    private Pet pet;
    private Uri petImageUri;

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
        } else {
            // edit pet mode
            if (pet.getImagePath() != null) {
                imgPetImage.setImageURI(Uri.parse(pet.getImagePath()));
                imgPetImage.setPadding(0, 0, 0, 0);
            }
            edtDeviceAddress.setText(pet.getDeviceAddress());
            edtPetName.setText(pet.getName());
            edtPetAge.setText(String.valueOf(pet.getAge()));
            edtPetBreed.setText(pet.getBreed());
            edtPetWeight.setText(String.valueOf(pet.getWeight()));
            edtHealthInfo.setText(String.valueOf(pet.getNote()));
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

                if (petDao.update(pet) < 0) {
                    Log.e(TAG, "onCreate: failed to edit pet");
                    Toast.makeText(this, "Cập nhật thú cưng thất bại!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Cập nhật thú cưng thành công!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onCreate: edit pet successful");
                    finish();
                }
            }
        });

        imgPetImage.setOnClickListener(view -> showImageSelectionOptions());
    }

    private void showImageSelectionOptions() {
        // Hiển thị dialog để chọn giữa máy ảnh và thư viện
        String[] options = {"Take a photo", "Select from gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Chụp ảnh
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = createImageFile(); // Tạo tệp để lưu ảnh

                    if (photoFile != null) {
                        petImageUri = FileProvider.getUriForFile(this, "com.example.mypets.fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, petImageUri);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            } else if (which == 1) {
                // Chọn ảnh từ thư viện (Gallery)
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_SELECT_PICTURE);
            }
        });
        builder.show();
    }

    private File createImageFile() {
        // Tạo tệp ảnh để lưu trữ
        String imageFileName = "IMG_" + System.currentTimeMillis();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Xử lý ảnh chụp từ máy ảnh
                imgPetImage.setImageURI(petImageUri);
            } else if (requestCode == REQUEST_SELECT_PICTURE) {
                // Xử lý ảnh được chọn từ thư viện
                petImageUri = data.getData();
                imgPetImage.setImageURI(petImageUri);
            }

            imgPetImage.setPadding(0, 0, 0, 0);
        }
    }

    private void getData() {
        pet.setUserId(DataManager.getInstance().getUserId());
        pet.setName(edtPetName.getText().toString());
        pet.setAge(Integer.parseInt(edtPetAge.getText().toString()));
        pet.setBreed(edtPetBreed.getText().toString());
        pet.setWeight(Float.parseFloat(edtPetWeight.getText().toString()));
        pet.setDeviceAddress(edtDeviceAddress.getText().toString());

        pet.setNote(edtHealthInfo.getText().toString());
        pet.setImagePath(petImageUri.toString());

        Log.d(TAG, "getData: " + pet.toString());
    }

    private void initView() {
        imgPetImage = findViewById(R.id.img_selected);
        edtDeviceAddress = findViewById(R.id.edt_device_address);
        edtPetName = findViewById(R.id.edt_pet_name);
        edtPetAge = findViewById(R.id.edt_pet_age);
        edtPetBreed = findViewById(R.id.edt_pet_breed);
        edtPetWeight = findViewById(R.id.edt_pet_weight);
        edtHealthInfo = findViewById(R.id.edt_health_info);

        btnBack = findViewById(R.id.btBack);
        btnAdd = findViewById(R.id.btAdd);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (petDao != null)
            petDao.close();
    }
}