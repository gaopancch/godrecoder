package com.gaopan.godrecoder.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.gaopan.godrecoder.Utils.ConstantUtils;
import com.gaopan.godrecoder.Utils.Utils;

import java.io.File;

/**
 * Created by gaopan on 2017/6/30.
 */

public class GuardService2 extends Service{
    enum ArmState{
        RECODING,
        IDEL
    }
    private AmrDirectoryObservers amrDirectoryObserver;
    private ArmState armState=ArmState.IDEL;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    startService1();
                    break;

                default:
                    break;
            }

        }
    };

    /**
     * 使用aidl 启动Service1
     */
    private MyFirstAIDL startS1 = new MyFirstAIDL.Stub() {

        @Override
        public void stopService() throws RemoteException {
            Intent i = new Intent(getBaseContext(), GuardService.class);
            getBaseContext().stopService(i);
        }

        @Override
        public void startService() throws RemoteException {
            Intent i = new Intent(getBaseContext(), GuardService.class);
            getBaseContext().startService(i);
        }
    };

    /**
     * 在内存紧张的时候，系统回收内存时，会回调OnTrimMemory， 重写onTrimMemory当系统清理内存时从新启动Service1
     */
    @Override
    public void onTrimMemory(int level) {
        startService1();

        Log.i("GuardService", " onTrimMemory2");
    }

    @SuppressLint("NewApi")
    public void onCreate() {

        Toast.makeText(GuardService2.this, "Service2 启动中...", Toast.LENGTH_SHORT)
                .show();
        startService1();
        /*
         * 此线程用监听Service的状态
         */
        new Thread() {
            public void run() {
                while (true) {
                    boolean isRun = Utils.isServiceWork(GuardService2.this,
                            "com.gaopan.godrecoder.service.GuardService");
                    if (!isRun) {
                        Message msg = Message.obtain();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i("AmrDirectoryObserver", "armState:"+ armState);
                if(armState==ArmState.IDEL) {
                    deleteTooManyRecordFiles();
                    handler.postDelayed(this,ConstantUtils.DELETE_AMR_INTERVIAL);
                }else{
                    handler.postDelayed(this,6000);
                }
            }
        },ConstantUtils.DELETE_AMR_INTERVIAL);
        amrDirectoryObserver=new AmrDirectoryObservers(ConstantUtils.RECODER_FILE_SAVE_PATH);
        amrDirectoryObserver.startWatching();
    }

    /**
     * 判断Service1是否还在运行，如果不是则启动Service1
     */
    private void startService1() {
        boolean isRun = Utils.isServiceWork(GuardService2.this,
                "com.gaopan.godrecoder.service.GuardService");
        if (isRun == false) {
            try {
                startS1.startService();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteTooManyRecordFiles() {
        File files[] = Utils.orderByDate(ConstantUtils.RECODER_FILE_SAVE_PATH);
        if (files != null) {
            if(files.length>ConstantUtils.SAVE_ARM_NUMBER){
                for (int i = ConstantUtils.SAVE_ARM_NUMBER; i < files.length; i++) {
                    if (files[i].getName().indexOf(".") >= 0) {
                        if(i>=ConstantUtils.SAVE_ARM_NUMBER){
                            Utils.deleteAmrFile(ConstantUtils.RECODER_FILE_SAVE_PATH +
                                    File.separator + files[i].getName());
                        }
                    }
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return (IBinder) startS1;
    }

    private class AmrDirectoryObservers extends FileObserver {

        public AmrDirectoryObservers(String path) {
            /**
             * 这种构造方法是默认监听所有事件的,如果使用 super(String,int)这种构造方法，
             * 则int参数是要监听的事件类型.
             */
            super(path);
        }

        @Override
        public void onEvent(int event, String path) {
            Log.i("AmrDirectoryObserver", "event:"+ event);
            switch(event) {
                case FileObserver.MODIFY:
                    armState=ArmState.RECODING;
                    break;
                case FileObserver.CLOSE_WRITE:
                    armState=ArmState.IDEL;
                    break;
                case FileObserver.OPEN:
                    armState=ArmState.RECODING;
                    break;
                case FileObserver.ALL_EVENTS:
                    Log.i("AmrDirectoryObserver", "path:"+ path);
                    break;
                case FileObserver.CREATE:
                    Log.i("AmrDirectoryObserver", "CREATE path:"+ path);
                    break;
            }
            Log.i("AmrDirectoryObserver", "armState:"+ armState);
        }
    }

}
