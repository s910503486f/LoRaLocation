package com.example.huyuxuan.lora2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    //用來存程式關掉之後還要存在的資料、狀態
    private SharedPreferences sharedPreferences;


    protected Button btnLogin;
    protected Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("data" , MODE_PRIVATE);
        Log.d("MainActivity","sharedpreference isLogin="+sharedPreferences.getString("isLogin",""));
        Log.d("MainActivity","sharedpreference isFirstTimeOpen="+sharedPreferences.getString("isFirstTimeOpen",""));

        //是否第一次開app，若沒有值預設是第一次(true)
        if((sharedPreferences.getString(getString(R.string.isFirstTimeOpen),"").compareTo("false")==0) &&
                sharedPreferences.getString(getString(R.string.isLogin),"").compareTo("true")==0){
            //不是第一次開app且已登入
            Log.d("MainActivity","不是第一次開且已登入");
            //跳到主畫面
            Intent intent = new Intent();
            intent.setClass(MainActivity.this,NavigationActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
            Log.d("MainActivity","跳到主畫面");

        }else{
            //是第一次開app或是還沒登入
            //顯示介紹
            setContentView(R.layout.activity_main);
            Log.d("MainActivity","第一次開app或還沒登入");
            btnLogin = (Button)findViewById(R.id.btnToLogin);
            btnSignUp = (Button)findViewById(R.id.btnMaintoSign);
            sharedPreferences.edit().putString(getString(R.string.isFirstTimeOpen),"false").apply();
            Log.d("MainActivity","sharedpreference isFirstTimeOpen="+sharedPreferences.getString("isFirstTimeOpen",""));

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳到登入畫面
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                    Log.d("MainActivity","跳到登入畫面");
                }
            });

            btnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳到註冊畫面
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this,SignUpActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                    Log.d("MainActivity","跳到註冊畫面");

                }
            });
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        //sharedPreferences = getSharedPreferences("data" , MODE_PRIVATE);
    }
}
