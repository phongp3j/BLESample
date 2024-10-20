package com.example.mypets.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mypets.Model.Pet;
import com.example.mypets.R;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.HomeViewHolder> {

    private Context context;
    private List<Pet> list;
    private ItemListener itemListener;

    public RecycleViewAdapter(Context context) {
        list = new ArrayList<>();
        this.context = context;
    }

    public void setList(List<Pet> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setItemListener(ItemListener itemListener) {
        this.itemListener = itemListener;
    }

    public Pet getItem(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Pet pet = list.get(position);

        if (pet.getImagePath() != null) {
            holder.ivPetImage.setImageURI(Uri.parse(pet.getImagePath()));
            holder.ivPetImage.setPadding(0, 0, 0, 0);
        }
        holder.tvPetName.setText(pet.getName());
        holder.tvPetAge.setText(context.getResources().getString(R.string.age_info, pet.getAge()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface ItemListener {
        void onItemClick(View view, int position);
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView ivPetImage;
        private final TextView tvPetName, tvPetAge;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPetImage = itemView.findViewById(R.id.iv_pet_image);
            tvPetName = itemView.findViewById(R.id.tv_pet_name);
            tvPetAge = itemView.findViewById(R.id.tv_pet_age);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (itemListener != null) {
                itemListener.onItemClick(v, getAdapterPosition());
            }
        }
    }
}
