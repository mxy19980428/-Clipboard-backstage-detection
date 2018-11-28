package com.test.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String ar = intent.getExtras().getString("is");
        Log.d("ttm", "onReceive: " + ar);
        Toast.makeText(context, intent.getStringExtra("is"), Toast.LENGTH_SHORT).show();
    }
}
