package com.example.mypets;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mypets.Adapters.ViewPagerAdapter;
import com.example.mypets.Data.DataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private String userLoginned;
    private TextView tvUsername;
    private BottomNavigationView navigationView;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        userLoginned = intent.getStringExtra("username");
        DataManager.getInstance().setData(userLoginned);
        initview();
        tvUsername.setText("Xin Chào " + userLoginned);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        navigationView.getMenu().findItem(R.id.mMyPets).setChecked(true);
                        break;
                    case 1:
                        navigationView.getMenu().findItem(R.id.mAddPet).setChecked(true);
                        break;
                    case 2:
                        navigationView.getMenu().findItem(R.id.mUserInfo).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.mMyPets) {
                    viewPager.setCurrentItem(0);
                }
                if (menuItem.getItemId() == R.id.mAddPet) {
                    viewPager.setCurrentItem(1);
                }
                if (menuItem.getItemId() == R.id.mUserInfo) {
                    viewPager.setCurrentItem(2);
                }
                return true;
            }
        });
    }

    public void initview() {
        tvUsername = findViewById(R.id.tvUsername);
        navigationView = findViewById(R.id.bottom_nav);
        viewPager = findViewById(R.id.viewPager);
    }
}