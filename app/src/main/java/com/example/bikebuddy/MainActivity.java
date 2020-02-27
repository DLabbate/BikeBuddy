package com.example.bikebuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.bikebuddy.Utils.MainPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    MainPagerAdapter mainPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
    }

    private void setupUI()
    {
        viewPager = findViewById(R.id.view_pager_main);
        tabLayout = findViewById(R.id.tab_layout_main);

        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mainPagerAdapter.addFragment(new LogFragment());
        mainPagerAdapter.addFragment(new GPSFragment());
        mainPagerAdapter.addFragment(new FitnessFragment());
        viewPager.setAdapter(mainPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Logs");
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_list);
        tabLayout.getTabAt(1).setText("GPS");
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_navigation);
        tabLayout.getTabAt(2).setText("Fitness");
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_bike);

    }
}
