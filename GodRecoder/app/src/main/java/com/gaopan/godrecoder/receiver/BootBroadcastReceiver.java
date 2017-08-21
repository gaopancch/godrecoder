package com.gaopan.godrecoder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gaopan.godrecoder.service.GuardService;

/**
 * Created by gaopan on 2017/6/30.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //后边的XXX.class就是要启动的服务
        Intent service = new Intent(context, GuardService.class);
        context.startService(service);
        Log.i("GuardService", "开机自动服务自动启动.....");
//        //启动应用，参数为需要自动启动的应用的包名
//        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
//        context.startActivity(intent );
    }
}
