package com.example.huyuxuan.lora2;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Exchanger;

/**
 * Created by huyuxuan on 2017/4/26.
 */

public class ConnectService extends Service {
    static Socket mSocket;
    InetAddress serverAddr;
    SocketAddress sc_add;
    static BufferedReader in;
    static BufferedWriter out;

    String id; //識別碼
    String msg;
    String activityName; //哪個activity傳來的請求

    String rcvMessage;
    Boolean flag = false;

    private final IBinder binder=new LocalBinder();

    private static final String ACTION_RECV_MSG = "com.example.huyuxuan.lora.intent.action.RECEIVE_MESSAGE";
    private SharedPreferences sharedPreferences;
    private boolean isReSend=false;
    Intent reSendIntent;


    @Override
    public void onCreate(){
        super.onCreate();
        Log.i("ConnectService:","onCreate");
        sharedPreferences = getSharedPreferences("data" , MODE_PRIVATE);
        Log.d("ConnectService","isConnect="+MyBoundedService.isConnect);
        if(!MyBoundedService.isConnect){
            Log.d("ConnectService","in create if");
            new AsyncTask<String,String,String>() {
                @Override
                protected String doInBackground(String... strings) {
                    connectToServer();
                    return null;
                }
            }.execute();
        }


    }

    @Override
    public IBinder onBind(final Intent intent) {
        Log.i("Service:","onBind called from "+intent.getExtras().getString("activity"));

        new AsyncTask<String,String,String>(){
            @Override
            protected String doInBackground(String... strings) {
                Log.d("ConnectService","onBind doinBGcalled");
                String tmp = intent.getExtras().getString(getString(R.string.id));
                if(!tmp.equals("15")){
                    reSendIntent=intent;
                    sendToServer(intent);
                    rcvMessage=null;
                }else{
                   disconnect();
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


        return binder;
    }

    public class LocalBinder extends Binder {
        public ConnectService getService() {
            System.out.println("I am in Localbinder ");
            return ConnectService.this;
        }
    }

    public void sendToServer(final Intent intent){
        Log.d("ConnectService","sendToServer called");
        new AsyncTask<String,String,String>() {
            @Override
            protected String doInBackground(String... strings) {

                id = intent.getExtras().getString("id");

                if(!id.equals("15")){//不是要斷線，就傳給server
                    activityName = intent.getExtras().getString("activity");
                    if(activityName.compareTo("")!=0 ){
                        Log.i("Service:","send to server from "+activityName);
                    }
                    else{
                        isReSend=true;
                    }
                    Bundle bundle = new Bundle();
                    Log.i("Service", "id = " + id);
                    if(mSocket.isConnected()){

                        switch (id) {
                            case "2"://註冊
                                String account = intent.getStringExtra("account");
                                String password = intent.getStringExtra("password");
                                String name = intent.getStringExtra("name");
                                String email = intent.getStringExtra("email");
                                msg = id +","+ account + "," + password + "," + name + "," + email + ",";
                                break;
                            case "3"://登入
                                account = intent.getStringExtra("account");
                                password = intent.getStringExtra("password");
                                msg = id+","+ account + "," + password + ",";
                                break;
                            case "4"://登記寄件
                                String time = intent.getStringExtra(getString(R.string.requireTime));
                                String sender = intent.getStringExtra(getString(R.string.sender));
                                String receiver = intent.getStringExtra(getString(R.string.receiver));
                                String StartId = intent.getStringExtra(getString(R.string.startLocation));
                                String destinationId = intent.getStringExtra(getString(R.string.desLocation));
                                String note = intent.getStringExtra(getString(R.string.note));
                                msg = id +","+sender+","+receiver+","+time+","+StartId+","+destinationId+","+note+",";
                                break;
                            case "5"://詢問車子有空時間
                            case "7":
                            case "8":
                            case "9":
                                Log.d("ConnectService","requireTime="+intent.getStringExtra(getString(R.string.requireTime)));
                                time = intent.getStringExtra(getString(R.string.requireTime));
                                msg = id+","+ time + ",";
                                break;
                            case "10":
                                name = intent.getStringExtra(getString(R.string.name));
                                msg = id +","+ name + ",";
                                break;
                            case "11"://要大樓資訊
                                msg = id+",";
                                break;
                            case "12":
                                password = intent.getStringExtra(getString(R.string.password));
                                email = intent.getStringExtra(getString(R.string.email));
                                msg = id+","+password+","+email+",";
                                break;
                        }

                        if (!mSocket.isOutputShutdown() && msg.length() > 0 && !mSocket.isInputShutdown()) {
                            try {
                                //ensureConnected();
                                if (out != null) {//傳送給server，接收server回應
                                    rcvMessage="";
                                    out.write(msg);
                                    out.flush();
                                    Log.d("Service", "write " + msg + " to server");
                                    rcvMessage = in.readLine();
                                    rcvMessage.concat("\0");    //*****後面一定要加\0不然會是亂碼
                                    Log.d("Service", "receive " + rcvMessage + " from server");
                                    bundle = Analyze(rcvMessage);
                                    if(isReSend){
                                        isReSend=false;
                                        sendToServer(reSendIntent);
                                        Log.d("ConnectService","isReSend");
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                //嘗試重新連線
                                int sta=connectToServer();
                                if(sta==1){
                                    Intent intent = new Intent();
                                    intent.putExtra(getString(R.string.activity),"");
                                    intent.putExtra(getString(R.string.id),"3");
                                    intent.putExtra(getString(R.string.account),sharedPreferences.getString("account",""));
                                    intent.putExtra(getString(R.string.password),sharedPreferences.getString("password",""));
                                    Log.d("斷線後重連，偷偷登入","account="+sharedPreferences.getString("account","")+"password="+sharedPreferences.getString("password",""));
                                    sendToServer(intent);
                                    //sendToServer(reSendIntent);
                                }
                                else{
                                    //重新連線也失敗
                                    Intent broadcastIntent = new Intent();
                                    broadcastIntent.setAction(ACTION_RECV_MSG);
                                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                    broadcastIntent.putExtra("result","false");
                                    broadcastIntent.putExtra("activity",activityName);//決定要傳給哪個activity
                                    broadcastIntent.putExtras(bundle);
                                    sendBroadcast(broadcastIntent);
                                    Log.i("Service:","重新連線失敗sendbroadcast to  "+activityName);
                                    ConnectService.this.stopSelf();
                                }
                            }
                        }
                        if(activityName.compareTo("")!=0 && bundle != null){
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(ACTION_RECV_MSG);
                            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                            broadcastIntent.putExtra("result","true");
                            broadcastIntent.putExtra("activity",activityName);//決定要傳給哪個activity
                            broadcastIntent.putExtras(bundle);
                            sendBroadcast(broadcastIntent);
                            Log.i("Service:","sendbroadcast to  "+activityName +"with result true");
                        }
                    }
                    else{
                        //沒有連上Server
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(ACTION_RECV_MSG);
                        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        broadcastIntent.putExtra("result","false");
                        broadcastIntent.putExtra("activity",activityName);//決定要傳給哪個activity
                        broadcastIntent.putExtras(bundle);
                        sendBroadcast(broadcastIntent);
                        Log.i("Service:","sendbroadcast to  "+activityName +"with result false");

                        //嘗試重新連線
                        int sta=connectToServer();
                        Log.d("Service","connect to Server");
                        if(sta==1){
                            sendToServer(reSendIntent);
                            Log.d("Service","connect success,resend intent");
                        }

                    }
                    rcvMessage="";
                }
                else{
                    //純斷線的broadcast
                    Bundle tmp = new Bundle();
                    tmp.putString(getString(R.string.id),"15");
                    activityName = intent.getExtras().getString("activity");
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(ACTION_RECV_MSG);
                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    broadcastIntent.putExtra("result","true");
                    broadcastIntent.putExtra("activity",activityName);//決定要傳給哪個activity
                    broadcastIntent.putExtras(tmp);
                    sendBroadcast(broadcastIntent);
                    Log.i("Service:","sendbroadcast to  "+activityName);
                }

                return  null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public Bundle Analyze(String mes){
        Bundle dataBundle = new Bundle();
        int commaIndex = mes.indexOf(',');
        String RcvId = mes.substring(0,commaIndex);
        dataBundle.putString("id",RcvId);
        Log.d("ConnectService","receive id="+RcvId);
        switch (RcvId){

            case "2":   //註冊是否成功
                String type=mes.substring(commaIndex+1,3);
                Log.d("ana:","id="+id+"type="+type);
                dataBundle.putString(getString(R.string.type),type);
                break;
            case "3":   //登入是否成功
                type=mes.substring(commaIndex+1,3);
                if(type.compareTo("1")==0){
                    String name = mes.substring(3,mes.indexOf('^'));
                    String email = mes.substring(mes.indexOf('^')+1);
                    dataBundle.putString(getString(R.string.type),type);
                    dataBundle.putString(getString(R.string.name),name);
                    dataBundle.putString(getString(R.string.email),email);
                    Log.d("ana:","id="+id+"type="+type+"name="+name+"email="+email);
                    sharedPreferences.edit().putString("BGLogin","false").apply();

                }
                else{
                    Log.d("Service","login fail");
                }
                dataBundle.putString("type",type);
                break;
            case "4":   //登記寄件是否成功
                type=mes.substring(commaIndex+1,3);
                if(type.compareTo("0")==0){
                    String error = mes.substring(3);
                    dataBundle.putString(getString(R.string.errorMsg),error);
                    Log.d("ana:","id="+id+"type="+type+"errorMsg="+error);
                }
                Log.d("ana:","id="+id+"type="+type);
                dataBundle.putString(getString(R.string.type),type);
                break;
            case "5":   //車子有空時段
                dataBundle.putString("message",mes.substring(commaIndex+1));
                Log.d("ana:","id="+id+"msg="+mes.substring(commaIndex+1));
                break;
            case "6":   //使用者資料
                String name = mes.substring(commaIndex+1,mes.indexOf('^'));
                String email = mes.substring(mes.indexOf('^')+1,mes.indexOf('*'));
                dataBundle.putString(getString(R.string.name),name);
                dataBundle.putString(getString(R.string.email),email);
                Log.d("ana:","id="+id+"name="+name+"email="+email);
                break;
            case "7":   //寄件資料
                String numStr = mes.substring(commaIndex+1,mes.lastIndexOf(','));//抓資料數量
                int num = Integer.valueOf(numStr);
                ArrayList<Order> orderArrayList = new ArrayList<Order>();
                String[] mesArray = mes.split("\\*");//把每筆用＊分開的資料分別抓出來存進array
                for(int i = 0; i < num ; i++){
                    String curStr = mesArray[i]; //抓每筆資料
                    String receiver;
                    if(i==0){
                        receiver=curStr.substring(mes.lastIndexOf(',')+1,curStr.indexOf('~'));
                    }
                    else{
                        receiver=curStr.substring(0,curStr.indexOf('~'));
                    }
                    Order tmp = new Order(curStr.substring(curStr.indexOf(';')+1,curStr.indexOf('/')),curStr.substring(curStr.indexOf('/')+1,curStr.indexOf('!'))
                            ,receiver,sharedPreferences.getString(getString(R.string.name),""),curStr.substring(curStr.indexOf('~')+1,curStr.indexOf('^'))
                            ,curStr.substring(curStr.indexOf('^')+1,curStr.indexOf(';')),curStr.substring(curStr.indexOf('!')+1,curStr.indexOf('#'))
                            ,curStr.substring(curStr.indexOf('#')+1,curStr.indexOf('$')),curStr.substring(curStr.indexOf('$')+1));
                    orderArrayList.add(tmp);
                }
                dataBundle.putSerializable("arrayList",orderArrayList);
                break;
            case "8":   //收件資料
                numStr = mes.substring(commaIndex+1,mes.lastIndexOf(','));//抓資料數量
                num = Integer.valueOf(numStr);
                mesArray = mes.split("\\*");//把每筆用＊分開的資料分別抓出來存進array
                orderArrayList = new ArrayList<Order>();
                for(int i = 0; i < num ; i++){
                    String curStr = mesArray[i]; //抓每筆資料
                    Log.d("ana","curStr="+curStr);
                    String sender;
                    if(i==0){
                       sender=curStr.substring(mes.lastIndexOf(',')+1,curStr.indexOf('~'));
                    }
                    else{
                       sender=curStr.substring(0,curStr.indexOf('~'));
                    }

                    Order tmp = new Order(curStr.substring(curStr.indexOf(';')+1,curStr.indexOf('/')),curStr.substring(curStr.indexOf('/')+1,curStr.indexOf('!'))
                            ,sharedPreferences.getString(getString(R.string.name),""),sender,curStr.substring(curStr.indexOf('~')+1,curStr.indexOf('^'))
                            ,curStr.substring(curStr.indexOf('^')+1,curStr.indexOf(';')),curStr.substring(curStr.indexOf('!')+1,curStr.indexOf('#'))
                            ,curStr.substring(curStr.indexOf('#')+1,curStr.indexOf('$')),curStr.substring(curStr.indexOf('$')+1));
                    orderArrayList.add(tmp);
                }
                dataBundle.putSerializable("arrayList",orderArrayList);
                break;
            case "9":
                numStr = mes.substring(commaIndex+1,mes.lastIndexOf(','));//抓資料數量
                num = Integer.valueOf(numStr);
                mesArray = mes.split("\\*");//把每筆用＊分開的資料分別抓出來存進array
                orderArrayList = new ArrayList<Order>();
                for(int i = 0; i < num ; i++){
                    String curStr = mesArray[i]; //抓每筆資料
                    String sender;
                    if(i==0){
                        sender=curStr.substring(mes.lastIndexOf(',')+1,curStr.indexOf('~'));
                    }
                    else{
                        sender=curStr.substring(0,curStr.indexOf('~'));
                    }

                    Order tmp = new Order(curStr.substring(curStr.indexOf('/')+1,curStr.indexOf('!'))
                            ,curStr.substring(curStr.indexOf('!')+1,curStr.indexOf('#')),curStr.substring(curStr.indexOf('~')+1,curStr.indexOf('^'))
                            ,sender,curStr.substring(curStr.indexOf('^')+1,curStr.indexOf(';')),curStr.substring(curStr.indexOf(';')+1,curStr.indexOf('/'))
                            ,curStr.substring(curStr.indexOf('#')+1,curStr.indexOf('$')),curStr.substring(curStr.indexOf('$')+1,curStr.indexOf('%'))
                            ,curStr.substring(curStr.indexOf('%')+1));
                    orderArrayList.add(tmp);
                }
                dataBundle.putSerializable("arrayList",orderArrayList);
                break;
            case "10":
                numStr = mes.substring(commaIndex+1,mes.indexOf('^'));//抓資料數量
                num=Integer.parseInt(numStr);
                mesArray = mes.split("\\*");//把每筆用＊分開的資料分別抓出來存進array
                ArrayList<HashMap<String,String>> receiverList = new ArrayList<HashMap<String,String>>();

                for(int i=0;i<num;i++){
                    HashMap<String,String> tmp = new HashMap<String,String>();
                    String curStr = mesArray[i]; //抓每筆資料
                    String account;
                    if(i==0){
                        account=curStr.substring(mes.indexOf('^')+1,curStr.indexOf('~'));
                    }
                    else{
                        account=curStr.substring(0,curStr.indexOf('~'));
                    }
                    name=curStr.substring(curStr.indexOf('~')+1);
                    tmp.put(account,name);
                    receiverList.add(tmp);
                }
               dataBundle.putSerializable("nameList",receiverList);
                break;
            case "11":
                mes = mes.substring(mes.indexOf('^')+1);
                mesArray = mes.split("\\*");//把每筆用＊分開的資料分別抓出來存進array
                dataBundle.putStringArray(getString(R.string.buildingArray),mesArray);
                Log.d("ana:","第一筆"+mesArray[0]);
                break;
            case "12":
                type=mes.substring(commaIndex+1,4);
                if(type.equals("0")){
                    String error = mes.substring(4);
                    dataBundle.putString(getString(R.string.errorMsg),error);
                    Log.d("ana:","id="+id+"type="+type+"errorMsg="+error);
                }
                dataBundle.putString(getString(R.string.type),type);
                Log.d("ana:","id="+id+"type="+type);
                break;
            case "20":
                Intent intent = new Intent();
                intent.putExtra(getString(R.string.activity),"");
                intent.putExtra(getString(R.string.id),"3");
                intent.putExtra(getString(R.string.account),sharedPreferences.getString("account",""));
                intent.putExtra(getString(R.string.password),sharedPreferences.getString("password",""));
                Log.d("接收到20偷偷登入","account="+sharedPreferences.getString("account","")+"password="+sharedPreferences.getString("password",""));
                sendToServer(intent);
                dataBundle=null;

        }
        return  dataBundle;
    }

    public void disconnect(){
        try {
            if(mSocket!=null && mSocket.isConnected()){
                Log.d("ConnectService","Socket close");
                mSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("ConnectService","onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d("ConnectService","onDestroy");
        super.onDestroy();
    }

    public int connectToServer(){
        try {
            Log.e("Service", "connectToServer");
            serverAddr = InetAddress.getByName(getString(R.string.ip));
            mSocket = new Socket();
            sc_add = new InetSocketAddress(serverAddr, Integer.parseInt(getString(R.string.port)));
            if (mSocket.isConnected()) {
                Log.i("Service", "Socket Connected");
            } else {
                mSocket.connect(sc_add, 2000);

            }
            MyBoundedService.isConnect=true;
            in = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), "utf8"));
            out = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(), "utf8"));
            Log.i("Service", "BufferedReader and PrintWriter ready.");
            out.write("1");
            out.flush();
            Log.d("Service", "write 1 to server");
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        catch (Exception e1){
            Log.d("ConnectService", "do in bg catch ");
            return -1;
        }
    }

    private void ensureConnected(){
        Log.e("ConnectService","ensureConnected");
        try {
            /*
            if(mSocket==null){
                mSocket=new Socket();
            }
            */
            if (mSocket.isConnected()) {
                Log.i("ConnectService", "Socket Connected");
            }else if(mSocket.isClosed()){
                Log.i("ConnectService", "Socket closed reconnect");
                connectToServer();
            }else{
                Log.e("ConnectService","ensure connected:not connected and not closed");
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}
