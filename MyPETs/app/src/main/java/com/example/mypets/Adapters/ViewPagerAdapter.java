package com.example.mypets.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.mypets.Fragments.FragmentAddPet;
import com.example.mypets.Fragments.FragmentMyPETs;
import com.example.mypets.Fragments.FragmentUserInfo;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new FragmentMyPETs();
            case 1: return new FragmentAddPet();
            case 2: return new FragmentUserInfo();
            default:return new FragmentMyPETs();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}