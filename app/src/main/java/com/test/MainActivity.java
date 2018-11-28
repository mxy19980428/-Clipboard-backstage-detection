package com.test;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.test.Receiver.MyReceiver;
import com.test.service.ClipboardService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ttm.";
    private boolean isService = false;
    private List<ActivityManager.RunningServiceInfo> serviceList;
    private EditText editText;
    private Intent intent;
    private List<String> datalist = new ArrayList();
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd E a HH:mm:ss");
    private MyReceiver myReceiver;
    private Button button, un;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder Ibinder) {
            ClipboardService.Binder1 binder = (ClipboardService.Binder1) Ibinder;
            binder.getService().setCallback(new ClipboardService.Callback() {
                @Override
                public void onData(String data) {
                    datalist.add(data);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.ex);
        intent = new Intent(MainActivity.this, ClipboardService.class);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ttm.", "onClick: " + v.getId());
                isService = true;
                Toast.makeText(MainActivity.this, "开启了剪切板监听模式！", Toast.LENGTH_SHORT).show();
                bindService(intent, connection, BIND_AUTO_CREATE);
                button.setEnabled(false);
                un.setEnabled(true);
             /*   myReceiver = new MyReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("archermind");
                registerReceiver(myReceiver, intentFilter);

                Intent intent = new Intent();
                intent.setAction("lpl");
                intent.putExtra("is", "archermind");
                sendBroadcast(intent);*/
            }
        });
        un = (Button) findViewById(R.id.un);
        un.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getisRun()) {
                    isService = false;
                    unbindService(connection);
                    Log.d(TAG, "停止了监听！");
                    stopService(intent);
                    //    unregisterReceiver(myReceiver);
                    button.setEnabled(true);
                    un.setEnabled(false);
                }
            }
        });
        getisRun();
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int arg = datalist.size();
            if (arg > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("" + format.format(new Date()));
                builder.setMessage("你这次复制了" + arg + "次").show();
            }
            List<String> datalist1 = (List<String>) msg.obj;
            for (String data : datalist1) {
                String temp = editText.getText().toString().trim();
                editText.setText(temp + "\n" + data + "\n");
            }
        }
    };

    private boolean getisRun() {
        ActivityManager activityManager = (ActivityManager) getSystemService(getApplicationContext().ACTIVITY_SERVICE);
        serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (serviceList.size() > 0) {
            button.setEnabled(false);
            un.setEnabled(true);
            return true;
        }
        button.setEnabled(true);
        un.setEnabled(false);
        return false;
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ttm.", "onStop");
        if (isService) {
            Toast.makeText(MainActivity.this, "开启了剪切板监听模式！", Toast.LENGTH_SHORT).show();
            startService(intent);
            datalist = new ArrayList<>();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ttm.", "onResume");
        if (getisRun()) {
            stopService(intent);
            Log.d(TAG, "停止了监听！");
            Toast.makeText(MainActivity.this, "停止了监听！", Toast.LENGTH_SHORT).show();
            Message message = new Message();
            message.obj = datalist;
            mHandler.sendMessage(message);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        stopService(intent);
        Log.d(TAG, "程序退出，停止了监听！");
        //    unregisterReceiver(myReceiver);
    }
}
