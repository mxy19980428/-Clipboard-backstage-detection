package com.test.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.test.MainActivity;
import com.test.R;


public class ClipboardService extends Service {

    private static ClipboardManager clipboardManager;
    private String ss;
    //回调接口的集合
    private Callback callback;

    public interface Callback {
        void onData(String data);
    }


    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public class Binder1 extends Binder {
        public ClipboardService getService() {
            return ClipboardService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent1) {
        return new Binder1();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        System.out.print("onCreate");
   
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                ClipData.Item itemAt = clipboardManager.getPrimaryClip().getItemAt(0);
                ss = itemAt.getText().toString();
                Log.d("ttm.", "监听到剪切板中的内容:" + ss);
                callback.onData(ss);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent1, int flags, int startId) {
        return super.onStartCommand(intent1, flags, startId);
    }

}
