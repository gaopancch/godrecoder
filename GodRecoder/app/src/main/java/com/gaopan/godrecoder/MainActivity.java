package com.gaopan.godrecoder;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gaopan.godrecoder.Utils.ConstantUtils;
import com.gaopan.godrecoder.listener.AmrDirectoryObserver;
import com.gaopan.godrecoder.listener.PermissionListener;
import com.gaopan.godrecoder.service.GuardService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import static java.security.AccessController.getContext;

/**
 * 要求：
 * 1、在手机接打电话时自动开启录音（手机已设置好相关权限）
 * 2、录音文件自动保存到手机上
 * 3、保存文件名字为通话对方的通讯录名称和时间
 * 4、软件需开机自启动，杀进程也没用，除非来软件内手动关闭录音功能
 */
public class MainActivity extends BaseActivity {
    private TextView showText;
    private Button start, end, play, goList;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private File iRecAudioFile;
    private File iRecAudioDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionRequests(Manifest.permission.RECORD_AUDIO, new PermissionListener() {
            @Override
            public void onClick(boolean bln) {
                if(bln){
                    Toast.makeText(getApplicationContext(),"权限通过",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"权限拒绝",Toast.LENGTH_SHORT).show();
                }
            }
        });
        permissionRequests(Manifest.permission.READ_CONTACTS, new PermissionListener() {
            @Override
            public void onClick(boolean bln) {
                if(bln){
                    Toast.makeText(getApplicationContext(),"权限通过",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"权限拒绝",Toast.LENGTH_SHORT).show();
                }
            }
        });

        permissionRequests(Manifest.permission.WRITE_CONTACTS, new PermissionListener() {
            @Override
            public void onClick(boolean bln) {
                if(bln){
                    Toast.makeText(getApplicationContext(),"权限通过",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"权限拒绝",Toast.LENGTH_SHORT).show();
                }
            }
        });

        permissionRequests(Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionListener() {
            @Override
            public void onClick(boolean bln) {
                if(bln){
                    Toast.makeText(getApplicationContext(),"权限通过",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"权限拒绝",Toast.LENGTH_SHORT).show();
                }
            }
        });
        initViews();
        mediaRecorder = new MediaRecorder();
        mediaPlayer = new MediaPlayer();
//        amrDirectoryObserver=new AmrDirectoryObserver(ConstantUtils.RECODER_FILE_SAVE_PATH);
//        amrDirectoryObserver.startWatching();
        Intent service = new Intent(MainActivity.this, GuardService.class);
        startService(service);
        showText.setText(getApplicationMetaValue("UMENG_CHANNEL"));
    }

    /**
     * 开始录制
     */
    private  int tempInt=1989;
    private void record() {
        /**
         * mediaRecorder.setAudioSource设置声音来源。
         * MediaRecorder.AudioSource这个内部类详细的介绍了声音来源。
         * 该类中有许多音频来源，不过最主要使用的还是手机上的麦克风，MediaRecorder.AudioSource.MIC
         */
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
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
        iRecAudioFile = new File(iRecAudioDir.getAbsolutePath()+File.separator+
                dateStr+"_"+getContactNameFromPhoneBook(getApplicationContext(),"18226276390")+".amr");
        if(!iRecAudioFile.exists()){
            try {
                iRecAudioFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mediaRecorder.setOutputFile(iRecAudioFile.getAbsolutePath());

        /**
         * 调用start开始录音之前，一定要调用prepare方法。
         */
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            ToastUtils.showMessage(getApplicationContext(), "mediaRecorder error IllegalStateException");
            e.printStackTrace();
        } catch (IOException e) {
            ToastUtils.showMessage(getApplicationContext(), "mediaRecorder error other");
            Log.e("MainActivity", "e=" + e);
            e.printStackTrace();
        }

    }

    /***
     * 此外，还有和MediaRecorder有关的几个参数与方法，我们一起来看一下：
     * sampleRateInHz :音频的采样频率，每秒钟能够采样的次数，采样率越高，音质越高。
     * 给出的实例是44100、22050、11025但不限于这几个参数。例如要采集低质量的音频就可以使用4000、8000等低采样率
     * <p>
     * channelConfig ：声道设置：android支持双声道立体声和单声道。MONO单声道，STEREO立体声
     * <p>
     * recorder.stop();停止录音
     * recorder.reset(); 重置录音 ，会重置到setAudioSource这一步
     * recorder.release(); 解除对录音资源的占用
     */

    private void initViews() {
        showText = (TextView) findViewById(R.id.show_text);
        start = (Button) findViewById(R.id.begin_button);
        end = (Button) findViewById(R.id.end_button);
        play = (Button) findViewById(R.id.play_button);
        goList = (Button) findViewById(R.id.go_list);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showText.setText("正在录音....");
                record();
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showText.setText("录音结束");
                mediaRecorder.stop();
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    mediaPlayer.reset();
//                    File file=new File(iRecAudioFile.getAbsolutePath());
//                    FileInputStream fileInputStream=new FileInputStream(file);
//                    mediaPlayer.setDataSource(fileInputStream.getFD());
//                    mediaPlayer.prepare();
//                } catch (IOException e) {
//                    ToastUtils.showMessage(getApplicationContext(), "play error");
//                    e.printStackTrace();
//                }
//                mediaPlayer.start();

//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, MoveViewActivity.class);
//                startActivity(intent);
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                toastView = inflater.inflate(R.layout.toastlayout, null);
                ToastManager.getInstance(getApplicationContext()).
                        makeToastSelfViewAnim(toastView,R.style.MyToast);

            }
        });
        goList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, RecoderListActivity.class);
                startActivity(intent);
            }
        });
    }
    private View toastView;

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
        if(TextUtils.isEmpty(contactName)){
            return phoneNum;
        }
        return contactName;
    }

    private void changeAppLauncherIcon(String name){
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(getComponentName(),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(getApplication(), name),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        //Intent 重启 Launcher 应用(魅族手机测试，无手动需重启)
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        List<ResolveInfo> resolves = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo res : resolves) {
            if (res.activityInfo != null) {
                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                am.killBackgroundProcesses(res.activityInfo.packageName);
            }
        }
    }

    private String  getApplicationMetaValue(String name) {
        String value= "";
        try {
            ApplicationInfo appInfo =getPackageManager()
                    .getApplicationInfo(getPackageName(),
                            PackageManager.GET_META_DATA);
            value = appInfo.metaData.getString(name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }
}
