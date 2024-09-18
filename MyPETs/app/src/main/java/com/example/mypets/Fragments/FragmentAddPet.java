package com.example.mypets.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mypets.Data.DataManager;
import com.example.mypets.Model.Pet;
import com.example.mypets.R;
import com.example.mypets.SQLite.SQLiteHelper;

public class FragmentAddPet extends Fragment {
    EditText edTen,edTuoi,edLoai;
    Button btBack,btAdd;
    String receivedData;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_addpet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edTen = view.findViewById(R.id.tvTen);
        edTuoi = view.findViewById(R.id.tvTuoi);
        edLoai = view.findViewById(R.id.tvType);
        btBack = view.findViewById(R.id.btBack);
        btAdd =view.findViewById(R.id.btAdd);
        receivedData= DataManager.getInstance().getData();
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentMyPETs fragmentA = new FragmentMyPETs(); // Assuming Fragment A is your previous fragment
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mAddPet, fragmentA)
                        .commit();
            }
        });
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ten = edTen.getText().toString();
                String loai = edLoai.getText().toString();
                int tuoi = Integer.parseInt(edTuoi.getText().toString());
                SQLiteHelper db = new SQLiteHelper(getContext());
                db.addPet(new Pet(ten,loai,tuoi,null,receivedData));
                Toast.makeText(getContext(),"Thêm PET thành công!",Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            }
        });
    }
}
