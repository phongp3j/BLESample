package com.example.mypets.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.mypets.AddPetActivity;
import com.example.mypets.Data.DataManager;
import com.example.mypets.Model.Pet;
import com.example.mypets.R;
import com.example.mypets.SQLite.PetDao;

import java.io.File;
import java.io.IOException;

public class FragmentAddPet extends Fragment {

    private static final String TAG = "FragmentAddPet";
    private static final String ARG_DEVICE_ADDRESS = "ARG_DEVICE_ADDRESS";
    private static final String ARG_PET_DETAILS_EDIT = "ARG_PET_DETAILS_EDIT";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SELECT_PICTURE = 2;
    private String mDeviceAddress;
    private Pet mPetDetailsEdit;
    private EditText edtDeviceAddress, edtPetName, edtPetAge, edtPetBreed,    // chủng loài
            edtPetWeight, edtHealthInfo;
    private ImageView imgPetImage;
    private Button btnBack, btnAdd, btnChangeAddress;

    private PetDao petDao;
    private Uri petImageUri;


    public FragmentAddPet() {
        // Required empty public constructor
    }

    public static FragmentAddPet newInstance(String deviceAddress, Pet petDetailsEdit) {
        FragmentAddPet fragment = new FragmentAddPet();
        Bundle args = new Bundle();
        args.putString(ARG_DEVICE_ADDRESS, deviceAddress);
        args.putSerializable(ARG_PET_DETAILS_EDIT, petDetailsEdit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDeviceAddress = getArguments().getString(ARG_DEVICE_ADDRESS);
            mPetDetailsEdit = (Pet) getArguments().getSerializable(ARG_PET_DETAILS_EDIT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_pet, container, false);

        petDao = new PetDao(getContext());

        initView(view);

        if (mPetDetailsEdit == null) {
            // add pet mode
            edtDeviceAddress.setText(mDeviceAddress);
        } else {
            // edit pet mode
            if (mPetDetailsEdit.getImagePath() != null) {
                petImageUri = Uri.parse(mPetDetailsEdit.getImagePath());
                imgPetImage.setImageURI(petImageUri);
                imgPetImage.setPadding(0, 0, 0, 0);
            }
            edtDeviceAddress.setText(mDeviceAddress);
            edtPetName.setText(mPetDetailsEdit.getName());
            edtPetAge.setText(String.valueOf(mPetDetailsEdit.getAge()));
            edtPetBreed.setText(mPetDetailsEdit.getBreed());
            edtPetWeight.setText(String.valueOf(mPetDetailsEdit.getWeight()));
            edtHealthInfo.setText(String.valueOf(mPetDetailsEdit.getNote()));
        }

        btnChangeAddress.setOnClickListener(v -> {
            Activity currActivity = getActivity();
            if (currActivity instanceof AddPetActivity)
                ((AddPetActivity) currActivity).showListDeviceFragment();
        });

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        btnAdd.setOnClickListener(v -> {
            Log.d(TAG, "onCreate: press button add pet");

            petDao = new PetDao(getContext());

            if (mPetDetailsEdit == null) {
                Log.d(TAG, "onCreate: -- add pet");
                mPetDetailsEdit = new Pet();
                getData();

                if (petDao.add(mPetDetailsEdit) < 0) {
                    Log.e(TAG, "onCreate: failed to add pet");
                    Toast.makeText(getContext(), "Thêm thú cưng thất bại!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Thêm thú cưng thành công!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onCreate: add pet successful");
                    requireActivity().finish();
                }
            } else {
                Log.d(TAG, "onCreate: -- edit pet");
                getData();

                if (petDao.update(mPetDetailsEdit) < 0) {
                    Log.e(TAG, "onCreate: failed to edit pet");
                    Toast.makeText(getContext(), "Cập nhật thú cưng thất bại!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Cập nhật thú cưng thành công!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onCreate: edit pet successful");
                    requireActivity().finish();
                }
            }
        });

        imgPetImage.setOnClickListener(v -> showImageSelectionOptions());

        return view;
    }


    private void showImageSelectionOptions() {
        // Hiển thị dialog để chọn giữa máy ảnh và thư viện
        String[] options = {"Chụp ảnh", "Lựa chọn ở thư viện"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose an option");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // Chụp ảnh
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    File photoFile = createImageFile(); // Tạo tệp để lưu ảnh

                    if (photoFile != null) {
                        petImageUri = FileProvider.getUriForFile(getContext(), "com.example.mypets.fileprovider", photoFile);
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
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
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
        mPetDetailsEdit.setUserId(DataManager.getInstance().getUserId());
        mPetDetailsEdit.setName(edtPetName.getText().toString());
        mPetDetailsEdit.setAge(Integer.parseInt(edtPetAge.getText().toString()));
        mPetDetailsEdit.setBreed(edtPetBreed.getText().toString());
        mPetDetailsEdit.setWeight(Float.parseFloat(edtPetWeight.getText().toString()));
        mPetDetailsEdit.setDeviceAddress(edtDeviceAddress.getText().toString());

        mPetDetailsEdit.setNote(edtHealthInfo.getText().toString());
        mPetDetailsEdit.setImagePath(petImageUri.toString());

        Log.d(TAG, "getData: " + mPetDetailsEdit.toString());
    }

    private void initView(View view) {
        imgPetImage = view.findViewById(R.id.img_selected);
        edtDeviceAddress = view.findViewById(R.id.edt_device_address);
        btnChangeAddress = view.findViewById(R.id.btn_change_address);

        edtPetName = view.findViewById(R.id.edt_pet_name);
        edtPetAge = view.findViewById(R.id.edt_pet_age);
        edtPetBreed = view.findViewById(R.id.edt_pet_breed);
        edtPetWeight = view.findViewById(R.id.edt_pet_weight);
        edtHealthInfo = view.findViewById(R.id.edt_health_info);

        btnBack = view.findViewById(R.id.btBack);
        btnAdd = view.findViewById(R.id.btAdd);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (petDao != null)
            petDao.close();
    }
}