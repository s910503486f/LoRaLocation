package com.example.huyuxuan.lora2;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.huyuxuan.lora2.Fragment.HistoryFragment;
import com.example.huyuxuan.lora2.Fragment.HomeFragment;
import com.example.huyuxuan.lora2.Fragment.RegisterFragment;
import com.example.huyuxuan.lora2.Fragment.SettingFragment;

/**
 * Created by huyuxuan on 2017/4/27.
 */

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public Fragment myFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);



        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            //這裡要放主畫面

            HomeFragment firstFragment = new HomeFragment();
            myFragment = firstFragment;
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,firstFragment).commit();

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //handle
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.nav_home:
                HomeFragment firstFragment = new HomeFragment();
                myFragment = firstFragment;
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,firstFragment).commit();
                break;
            case R.id.nav_register:
                RegisterFragment registerFragment = new RegisterFragment();
                myFragment = registerFragment;
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,registerFragment).commit();
                break;
            case R.id.nav_history:
                HistoryFragment historyFragment = new HistoryFragment();
                myFragment = historyFragment;
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,historyFragment).commit();
                break;
            case R.id.nav_setting:
                SettingFragment settingFragment = new SettingFragment();
                myFragment = settingFragment;
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,settingFragment).commit();
                break;
            case R.id.nav_logOut:
                logOutDialog();
                break;
        }
        return false;
    }

    private void logOutDialog(){
        new AlertDialog.Builder(this)
                .setTitle("登出")
                .setMessage("確定要登出嗎？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearData();
                        Intent intent = new Intent();
                        intent.setClass(NavigationActivity.this,LoginActivity.class);
                        startActivity(intent);
                        Log.d("NavigationActivity","跳到登入畫面");
                        NavigationActivity.this.finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void clearData(){
        SharedPreferences sharedPreferences = getSharedPreferences("data" , MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(getString(R.string.account),"")
                .putString(getString(R.string.password),"")
                .putString(getString(R.string.name),"")
                .putString(getString(R.string.email),"")
                .putString(getString(R.string.isLogin),"fasle")
                .apply();
        User mUser=(User)getApplicationContext();
        mUser.UserName="";
        mUser.UserPassword="";
        mUser.UserEmail="";
        mUser.UserAccount="";
    }
}