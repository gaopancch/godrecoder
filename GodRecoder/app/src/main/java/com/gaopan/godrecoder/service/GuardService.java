package com.gaopan.godrecoder.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.gaopan.godrecoder.ToastUtils;
import com.gaopan.godrecoder.Utils.ConstantUtils;
import com.gaopan.godrecoder.Utils.Utils;
import com.gaopan.godrecoder.listener.AmrDirectoryObserver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by gaopan on 2017/6/30.
 */

public class GuardService extends Service {

    // 管理电话
    private TelephonyManager tm;
    private MyPhoneStateListener listener;
    private MediaRecorder mediaRecorder;
    private File iRecAudioFile;
    private File iRecAudioDir;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    startService2();
                    break;
                default:
                    break;
            }

        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        startService2();
        Log.i("GuardService", " onCreate");
        // 实例化
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        // 监听电话的状态
        listener = new MyPhoneStateListener();
        // 注册监听者,监听电话的状态
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
                /*
         * 此线程用监听Service2的状态
         */
        new Thread() {
            public void run() {
                while (true) {
                    boolean isRun = Utils.isServiceWork(GuardService.this,
                            "com.gaopan.godrecoder.service.GuardService2");
                    if (!isRun) {
                        Message msg = Message.obtain();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };
        }.start();
    }

    private MyFirstAIDL myFirstAIDL = new MyFirstAIDL.Stub() {

        @Override
        public void stopService() throws RemoteException {
            Intent i = new Intent(getBaseContext(), GuardService2.class);
            getBaseContext().stopService(i);
        }

        @Override
        public void startService() throws RemoteException {
            Intent i = new Intent(getBaseContext(), GuardService2.class);
            getBaseContext().startService(i);
        }
    };


    /**
     * 在内存紧张的时候，系统回收内存时，会回调OnTrimMemory， 重写onTrimMemory当系统清理内存时从新启动Service2
     */
    @Override
    public void onTrimMemory(int level) {
        Log.i("GuardService", " onTrimMemory");
        /*
         * 启动service2
         */
        startService2();

    }

    /**
     * 判断Service2是否还在运行，如果不是则启动Service2
     */
    private void startService2() {
        boolean isRun = Utils.isServiceWork(GuardService.this,
                "com.gaopan.godrecoder.service.GuardService2");
        if (isRun == false) {
            try {
                myFirstAIDL.startService();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return (IBinder) myFirstAIDL;
    }


    /**
     * 开始录制
     */
    private void record(String incomingNumber) throws Exception {
        if(TextUtils.isEmpty(incomingNumber)){
            incomingNumber="未知号码";
        }else {
            try {
                incomingNumber= getContactNameFromPhoneBook(getApplicationContext(), incomingNumber);
            }catch (Exception e){
                incomingNumber="未知号码";
            }
        }
        /**
         * mediaRecorder.setAudioSource设置声音来源。
         * MediaRecorder.AudioSource这个内部类详细的介绍了声音来源。
         * 该类中有许多音频来源，不过最主要使用的还是手机上的麦克风，MediaRecorder.AudioSource.MIC
         */
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        /**
         * mediaRecorder.setOutputFormat代表输出文件的格式。该语句必须在setAudioSource之后，在prepare之前。
         * OutputFormat内部类，定义了音频输出的格式，主要包含MPEG_4、THREE_GPP、RAW_AMR……等。
         */
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        /**
         * mediaRecorder.setAddioEncoder()方法可以设置音频的编码
         * AudioEncoder内部类详细定义了两种编码：AudioEncoder.DEFAULT、AudioEncoder.AMR_NB
         */
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        /**
         * 设置录音之后，保存音频文件的位置
         */
        iRecAudioDir = new File(ConstantUtils.RECODER_FILE_SAVE_PATH);
        if (!iRecAudioDir.exists()) {
            iRecAudioDir.mkdirs();
        }
        String dateStr = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss").format(System.currentTimeMillis());
        /* 创建录音文件 */
        iRecAudioFile = new File(iRecAudioDir.getAbsolutePath() + File.separator + dateStr + "_"+incomingNumber + ".amr");
        mediaRecorder.setOutputFile(iRecAudioFile.getAbsolutePath());

        /**
         * 调用start开始录音之前，一定要调用prepare方法。
         */
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            Log.i("GuardService", "e=IllegalStateException"+e.getMessage());
            ToastUtils.showMessage(getApplicationContext(), "mediaRecorder error IllegalStateException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("GuardService", "e=IOException"+e.getMessage());
            ToastUtils.showMessage(getApplicationContext(), "mediaRecorder error other");
            e.printStackTrace();
        }

    }

    private String getContactNameFromPhoneBook(Context context, String phoneNum) {
        String contactName = "";
        ContentResolver cr = context.getContentResolver();
        Cursor pCur = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                new String[] { phoneNum }, null);
        if (pCur.moveToFirst()) {
            contactName = pCur
                    .getString(pCur
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            pCur.close();
        }
        return contactName;
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        String phoneNumber=null;
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            Log.i("GuardService", "onCallStateChanged incomingNumber="+incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: // 空闲状态
                    Log.i("GuardService", "TelephonyManager=CALL_STATE_IDLE");
                    if (mediaRecorder != null) {
                        mediaRecorder.stop();
                        mediaRecorder.reset(); //重设
                        mediaRecorder.release(); //刻录完成一定要释放资源
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK: // 摘机状态(通话状态)
                    Log.i("GuardService", "TelephonyManager=CALL_STATE_OFFHOOK incomingNumber="+incomingNumber);
                    mediaRecorder = new MediaRecorder();
                    try {
                        record(phoneNumber);
                    } catch (Exception e) {
                        Log.i("GuardService", "record IOException"+e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING: // 响铃状态
                    Log.i("GuardService", "TelephonyManager=CALL_STATE_RINGING incomingNumber="+incomingNumber);
                    phoneNumber=incomingNumber;
                    break;
            }
        }
    }
}
