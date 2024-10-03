package com.example.mypets.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypets.Adapters.RecycleViewAdapter;
import com.example.mypets.Data.DataManager;
import com.example.mypets.Model.Pet;
import com.example.mypets.PetDetailActivity;
import com.example.mypets.R;
import com.example.mypets.SQLite.PetDao;

import java.util.List;

public class FragmentMyPETs extends Fragment implements RecycleViewAdapter.ItemListener {
    private RecycleViewAdapter adapter;
    private RecyclerView recyclerView;
    private TextView tvdalam;

    private PetDao petDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mypets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclView);
        tvdalam = view.findViewById(R.id.dathem);

        adapter = new RecycleViewAdapter();
        petDao = new PetDao(getContext());

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.setItemListener(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        Pet pet = adapter.getItem(position);
        Intent intent = new Intent(getActivity(), PetDetailActivity.class);
        intent.putExtra(PetDetailActivity.KEY_PET_DETAILS_DISPLAY, pet);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Pet> list = petDao.getAll(DataManager.getInstance().getUserId());
        adapter.setList(list);
        tvdalam.setText("Số Pet đã thêm: " + list.size());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        petDao.close();
    }
}