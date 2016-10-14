package com.startandroid.tabs;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import com.startandroid.tabs.Adapters.PagerAdapter;
import com.startandroid.tabs.Events.PlaceSelectedEvent;

import de.greenrobot.event.EventBus;
import permission.auron.com.marshmallowpermissionhelper.ActivityManagePermission;
import permission.auron.com.marshmallowpermissionhelper.PermissionResult;
import permission.auron.com.marshmallowpermissionhelper.PermissionUtils;

public class MainActivity extends ActivityManagePermission {


    ViewPager viewPager = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askPermission();
    }


    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    public void onEvent(PlaceSelectedEvent messagePlace){

        viewPager.setCurrentItem(0);
    }

    private void prepareToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Карта"));
        tabLayout.addTab(tabLayout.newTab().setText("Локации"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void askPermission()
    {
        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        askCompactPermission(PermissionUtils.Manifest_ACCESS_COARSE_LOCATION, new PermissionResult() {
            @Override
            public void permissionGranted() {
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    startActivity(new Intent(
                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
                prepareToolbar();
            }

            @Override
            public void permissionDenied() {
                prepareToolbar();
                Toast.makeText(getApplicationContext(), "Карта", Toast.LENGTH_LONG).show();

            }
            @Override
            public void permissionForeverDienid() {
                prepareToolbar();
                Toast.makeText(getApplicationContext(), "Локации", Toast.LENGTH_LONG).show();
            }
        });

    }
}