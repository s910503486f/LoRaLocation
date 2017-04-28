package com.example.huyuxuan.lora2;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by huyuxuan on 2017/4/26.
 */

public class LoginActivity extends AppCompatActivity {
    protected EditText editTextAccount;
    protected EditText editTextPassword;
    protected Button btnLogin;
    protected Button btnSignUp;

    protected String account;
    protected String password;

    static boolean isBind;

    static ConnectService mBoundService;
    private ConnectServiceReceiver receiver;
    private static final String ACTION_RECV_MSG = "com.example.huyuxuan.lora.intent.action.RECEIVE_MESSAGE";

    //用來存程式關掉之後還要存在的資料、狀態
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextAccount = (EditText)findViewById(R.id.editTextAccount);
        editTextPassword = (EditText)findViewById(R.id.editTextPassword);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnSignUp = (Button)findViewById(R.id.btnToSign);

        isBind=false;

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                account = editTextAccount.getText().toString();
                password = editTextPassword.getText().toString();

                Intent intent = new Intent(LoginActivity.this,ConnectService.class);
                intent.putExtra(getString(R.string.activity),"LoginActivity");
                intent.putExtra(getString(R.string.id),"3");
                intent.putExtra(getString(R.string.account),account);
                intent.putExtra(getString(R.string.password),password);
                Log.d("LoginActivity","account="+account+"password="+password);

                if(!isBind){
                    getApplicationContext().bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
                    isBind=true;
                    Log.d("LoginActivity:", "login->bind");
                }
                else{
                    mBoundService.sendToServer(intent);
                    Log.d("LoginActivity:", "login->sendToService");
                }
                setReceiver();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳到註冊畫面
            }
        });

    }

    //接收广播类
    public class ConnectServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra("activity").equals("LoginActivity")){
                unregisterReceiver(receiver);
                Bundle bundle = intent.getExtras();
                String type = bundle.getString(getString(R.string.type));
                if(type.compareTo("1")==0){
                    User mUser=(User)context.getApplicationContext();
                    mUser.UserAccount=bundle.getString(getString(R.string.account));
                    mUser.UserPassword=bundle.getString(getString(R.string.password));
                    mUser.UserName=bundle.getString(getString(R.string.name));
                    mUser.UserEmail=bundle.getString(getString(R.string.email));
                    sharedPreferences.edit()
                            .putString(getString(R.string.account),mUser.UserAccount)
                            .putString(getString(R.string.password),mUser.UserPassword)
                            .putString(getString(R.string.name),mUser.UserName)
                            .putString(getString(R.string.email),mUser.UserEmail)
                            .putString(getString(R.string.isLogin),"true")
                            .apply();
                    Log.d("LoginActivity:", "account:"+mUser.UserAccount+"password:"+mUser.UserPassword+"name:"+mUser.UserName);

                    //跳到主畫面
                    Intent intentToMain = new Intent();
                    intentToMain.setClass(LoginActivity.this,NavigationActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                    Log.d("LoginActivity","跳到主畫面");

                }else{
                    //登入失敗
                    String errorMsg=bundle.getString(getString(R.string.errorMsg));
                    Toast.makeText(LoginActivity.this,errorMsg, Toast.LENGTH_LONG).show();
                }
            }
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
            Log.d("LoginActivity","onServiceDisconnected");
            //mBoundService = null;
            isBind=false;
        }

    };

    private void setReceiver(){
        //动态注册receiver
        IntentFilter filter = new IntentFilter(ACTION_RECV_MSG);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ConnectServiceReceiver();
        registerReceiver(receiver, filter);
        Log.d("LoginActivity:","register receiver");
    }
}
