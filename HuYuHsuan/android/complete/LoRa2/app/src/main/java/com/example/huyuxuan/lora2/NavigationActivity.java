package com.example.huyuxuan.lora2;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.IBinder;
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
import android.widget.Toast;

import com.example.huyuxuan.lora2.Background.MyAlarmReceiver;
import com.example.huyuxuan.lora2.Fragment.AccountFragment;
import com.example.huyuxuan.lora2.Fragment.HomeFragment;
import com.example.huyuxuan.lora2.Fragment.NewOrderFragment;
import com.example.huyuxuan.lora2.Fragment.RecvHistoryFragment;
import com.example.huyuxuan.lora2.Fragment.SendHistoryFragment;

import java.io.File;


/**
 * Created by huyuxuan on 2017/4/27.
 */

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public Fragment myFragment;
    static ConnectService mBoundService;
    private static final String ACTION_RECV_MSG = "com.example.huyuxuan.lora.intent.action.RECEIVE_MESSAGE";
    private static boolean isBind;
    private ConnectServiceReceiver receiver;
    public HomeFragment firstFragment;
    public NewOrderFragment newOrderFragment;
    public SendHistoryFragment sendHistoryFragment;
    public RecvHistoryFragment recvHistoryFragment;
    public AccountFragment accountFragment = new AccountFragment();

    MyAlarmReceiver alarm = new MyAlarmReceiver();
    private SharedPreferences sharedPreferences;
    Bundle tmp;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        tmp = savedInstanceState;
        Log.d("NavigationActivity","onCreate");
        sharedPreferences = getSharedPreferences("data" , MODE_PRIVATE);
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

        alarm.setAlarm(this);

        Log.d("NavigationActivity","BGLogin="+sharedPreferences.getString("BGLogin",""));
        if(sharedPreferences.getString("BGLogin","").equals("true")){
            //須偷偷登入才能載入HomeFragment
            Intent intent = new Intent(NavigationActivity.this,ConnectService.class);
            intent.putExtra(getString(R.string.activity),"NavigationActivity");
            intent.putExtra(getString(R.string.id),"3");
            intent.putExtra(getString(R.string.account),sharedPreferences.getString(getString(R.string.account),""));
            intent.putExtra(getString(R.string.password),sharedPreferences.getString(getString(R.string.password),""));
            Log.d("NavigationActivity","偷偷登入");

            if(!isBind){
                getApplicationContext().bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
                isBind=true;
                Log.d("NavigationActivity:", "bind");
            }
            else{
                mBoundService.sendToServer(intent);
                Log.d("NavigationActivity:", "sendToService");
            }
            setReceiver();
        }else{
            //不用偷偷登入即可載入HomeFragment
            loadHome(savedInstanceState);
        }

    }

    public void loadHome(Bundle tmp2){
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (tmp2 != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            //這裡要放主畫面
            if(firstFragment==null){
                firstFragment = new HomeFragment();
            }
            myFragment = firstFragment;
            MyBoundedService.fragmentID = 0;
            MyBoundedService.curFragment=firstFragment;
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,firstFragment).commit();
        }
    }

    @Override
    protected void onStop() {
        sharedPreferences.edit().putString("BGLogin","true").apply();
        sharedPreferences.edit().putString("hasStop","true").apply();
        if(sharedPreferences.getInt("BGServiceCount",0)==1){
            sharedPreferences.edit().putInt("BGServiceCount",0).apply();
        }
        Log.d("NavigationActivity","onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("NavigationActivity","onDestroy");
        sharedPreferences.edit().putString("BGLogin","true")
                .putInt("BGServiceCount",0)
                .apply();

        try{
            if(mBoundService!=null){
                mBoundService.disconnect();
                unregisterReceiver(receiver);
                getApplicationContext().unbindService(mConnection);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    protected void onStart() {
        Log.d("NavigationActivity","onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d("NavigationActivity","onResume");
        sharedPreferences.edit().putString("hasStop","false").apply();
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("NavigationActivity","onPause");
        super.onPause();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fm = this.getFragmentManager();

            if (fm.getBackStackEntryCount() == 0) {

                int curFragmentId = MyBoundedService.fragmentID;
                Log.d("NavigationActivity","onBack backstackcount==0 curID="+curFragmentId);
                if(curFragmentId==6 || curFragmentId == 5 || curFragmentId == 1 || curFragmentId == 2 ){
                    //這些畫面返回時要跳回主畫面
                    Log.d("NavigationActivity","cur == ... Go to Home");

                    Fragment curfragment=MyBoundedService.curFragment;
                    if(curfragment!=null){
                        getSupportFragmentManager().beginTransaction().remove(curfragment).commit();
                        curfragment.onDestroy();
                    }
                    HomeFragment firstFragment = new HomeFragment();
                    myFragment = firstFragment;
                    MyBoundedService.fragmentID = 0;
                    MyBoundedService.curFragment=firstFragment;
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container,firstFragment).commit();
                }
                else if(curFragmentId==0){
                    Log.d("NavigationActivity","在主畫面按返回");

                    try{
                        getApplicationContext().unbindService(mConnection);
                        getApplicationContext().unregisterReceiver(receiver);
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    //主畫面按返回要離開程式
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else{
                    super.onBackPressed();
                }
            } else {
                Log.d("NavigationActivity","onBack backstackcount!=0");
                fm.popBackStack();
            }
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //handle
        int itemId = item.getItemId();
        if(itemId!=R.id.nav_logOut){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        switch (itemId){
            case R.id.nav_home:
                Fragment curfragment=MyBoundedService.curFragment;
                if(curfragment!=null){
                    getSupportFragmentManager().beginTransaction().remove(curfragment).commit();
                    curfragment.onDestroy();
                }
                HomeFragment firstFragment = new HomeFragment();
                myFragment = firstFragment;
                MyBoundedService.fragmentID = 0;
                MyBoundedService.curFragment=firstFragment;
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container,firstFragment).commit();
                break;
            case R.id.nav_register:
                curfragment=MyBoundedService.curFragment;
                if(curfragment!=null){
                    getSupportFragmentManager().beginTransaction().remove(curfragment).commit();
                    curfragment.onDestroy();
                }
                newOrderFragment = new NewOrderFragment();
                myFragment = newOrderFragment;
                MyBoundedService.fragmentID = 2;
                MyBoundedService.curFragment=newOrderFragment;
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container,newOrderFragment).commit();
                break;
            case R.id.nav_send_history:
                curfragment=MyBoundedService.curFragment;
                if(curfragment!=null){
                    getSupportFragmentManager().beginTransaction().remove(curfragment).commit();
                    curfragment.onDestroy();
                }
                sendHistoryFragment = new SendHistoryFragment();
                myFragment = sendHistoryFragment;
                MyBoundedService.fragmentID = 5;
                MyBoundedService.curFragment=sendHistoryFragment;
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container,sendHistoryFragment).commit();
                break;
            case R.id.nav_recv_history:
                curfragment=MyBoundedService.curFragment;
                if(curfragment!=null){
                    getSupportFragmentManager().beginTransaction().remove(curfragment).commit();
                    curfragment.onDestroy();
                }
                recvHistoryFragment = new RecvHistoryFragment();
                myFragment = recvHistoryFragment;
                MyBoundedService.fragmentID = 6;
                MyBoundedService.curFragment=recvHistoryFragment;
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container,recvHistoryFragment).commit();
                break;
            case R.id.nav_profile:
                curfragment=MyBoundedService.curFragment;
                if(curfragment!=null){
                    getSupportFragmentManager().beginTransaction().remove(curfragment).commit();
                    curfragment.onDestroy();
                }
                accountFragment = new AccountFragment();
                myFragment = accountFragment;
                MyBoundedService.fragmentID = 1;
                MyBoundedService.curFragment=accountFragment;
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container,accountFragment).commit();
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
                        MyBoundedService.callingActivity=2;
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
        Log.d("NavigationActivity","clearData");
        SharedPreferences sharedPreferences = getSharedPreferences("data" , MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(getString(R.string.account),"")
                .putString(getString(R.string.password),"")
                .putString(getString(R.string.name),"")
                .putString(getString(R.string.email),"")
                .putString(getString(R.string.isLogin),"false")
                .putString("BGLogin","false")
                .apply();

        //刪除照片
        String sd = Environment.getExternalStorageDirectory().toString();
        File file = new File(sd+"/mypic.png");
        boolean delete=file.delete();
        Log.d("LogOut","file delete"+String.valueOf(delete));

        //service的socket斷線
        Intent intent = new Intent(NavigationActivity.this,ConnectService.class);
        intent.putExtra(getString(R.string.activity),"NavigationActivity");
        intent.putExtra(getString(R.string.id),"15");//要登出
        Log.d("NavigationActivity","告訴service斷線");

        if(!isBind){
            getApplicationContext().bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
            isBind=true;
            Log.d("NavigationActivity:", "bind");
        }
        else{
            mBoundService.sendToServer(intent);
            Log.d("NavigationActivity:", "sendToService");
        }
        setReceiver();


        alarm.cancelAlarm(NavigationActivity.this);
        if(MyBoundedService.myBGService!=null){
            MyBoundedService.myBGService.disconnect();
        }


    }



    private static ServiceConnection mConnection = new ServiceConnection() {
        //EDITED PART
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            mBoundService = ((ConnectService.LocalBinder)service).getService();
            isBind=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            Log.d("NavigationActivity","onServiceDisconnected");
            mBoundService = null;
            isBind=false;
        }

    };

    //接收广播类
    public class ConnectServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("activity").equals("NavigationActivity")){
                if(intent.getStringExtra("result").equals("true")){
                    try{
                        getApplicationContext().unbindService(mConnection);
                        unregisterReceiver(receiver);
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    Bundle bundle = intent.getExtras();
                    String id=bundle.getString(getString(R.string.id));
                    switch(id){
                        case "3":
                            String type = bundle.getString(getString(R.string.type));
                            if(type.compareTo("1")==0){
                                //偷偷登入成功，可以載入HomeFragment要資料了
                                loadHome(tmp);
                            }else{
                                String errorMsg=bundle.getString(getString(R.string.errorMsg));
                                Log.e("NavigationActivity",errorMsg);
                            }
                            break;
                        case "15":
                            Log.d("NavigationActivity","登出斷線成功");
                            break;
                        default:
                            Log.d("NavigationActivity","onReceive到其他id");
                            break;
                    }

                }
                else{
                    //連線有問題
                    //Toast.makeText(NavigationActivity.this,"伺服器維護中,請稍候再試",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void setReceiver(){
        //动态注册receiver
        IntentFilter filter = new IntentFilter(ACTION_RECV_MSG);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ConnectServiceReceiver();
        registerReceiver(receiver, filter);
        Log.d("NavigationActivity:","register receiver");
    }

}
