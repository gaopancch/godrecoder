package com.gaopan.godrecoder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.gaopan.godrecoder.Adapter.ShowListAdapter;
import com.gaopan.godrecoder.Utils.ConstantUtils;
import com.gaopan.godrecoder.Utils.Utils;
import com.gaopan.godrecoder.view.VoicePlayingAnimationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class RecoderListActivity extends Activity {
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ShowListAdapter showListAdapter;
//    private File iRecAudioDir;// 需要被播放的录音文件所在的文件夹
    private ArrayList<String> iRecordFiles;//所有的录音文件列表（只是文件名，不包含路径）
    private MediaPlayer mediaPlayer;
    private VoicePlayingAnimationView voicePlayingAnimationView;
    private Context context;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recoder_list);
        context=this;
        relativeLayout=(RelativeLayout)findViewById(R.id.activity_recoder_list) ;
        getRecordFiles();//获取所有的录音数据
        initListView();//显示所有的录音数据
        initMediaPlayer();//初始化播放器
    }

    private int markedNumber=0;
    private void getRecordFiles() {
//        iRecAudioDir = new File(ConstantUtils.RECODER_FILE_SAVE_PATH);
        File iRecAudioDirMarked= new File(ConstantUtils.RECODER_FILE_SAVE_MARKED_PATH);
        if (!iRecAudioDirMarked.exists()) {
            iRecAudioDirMarked.mkdirs();
        }
        iRecordFiles = new ArrayList<String>();
        File filesMarked[] = Utils.orderByDate(ConstantUtils.RECODER_FILE_SAVE_MARKED_PATH);
        if (filesMarked != null) {
            for (int i = 0; i < filesMarked.length; i++) {
                if (filesMarked[i].getName().indexOf(".") >= 0) {
                    String fileS = filesMarked[i].getName().substring(
                            filesMarked[i].getName().indexOf("."));
                    if (fileS.toLowerCase().equals(".amr")) {
                        iRecordFiles.add(filesMarked[i].getName());
                    }
                }
            }
        }
        markedNumber=iRecordFiles.size();
        File files[] = Utils.orderByDate(ConstantUtils.RECODER_FILE_SAVE_PATH);
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().indexOf(".") >= 0) {
                    String fileS = files[i].getName().substring(
                            files[i].getName().indexOf("."));
                    if (i >= ConstantUtils.SAVE_ARM_NUMBER) {
                        Utils.deleteAmrFile(ConstantUtils.RECODER_FILE_SAVE_PATH +
                                File.separator + files[i].getName());
                    } else {
                        if (fileS.toLowerCase().equals(".amr")) {
                            iRecordFiles.add(files[i].getName());
                        }
                    }
                }
            }
        }
    }


    private void initListView() {
        listView = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, iRecordFiles);
        showListAdapter = new ShowListAdapter(this, iRecordFiles);
        listView.setAdapter(showListAdapter);
        listView.setOnItemClickListener(new ListViewItemClick());
    }

    private class ListViewItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
            try {
                File file =null;
                if(position>=markedNumber){
                    file = new File(ConstantUtils.RECODER_FILE_SAVE_PATH +
                            File.separator + iRecordFiles.get(position));
                }else {
                    file = new File(ConstantUtils.RECODER_FILE_SAVE_MARKED_PATH +
                            File.separator + iRecordFiles.get(position));
                }
                FileInputStream fileInputStream = new FileInputStream(file);
                mediaPlayer.setDataSource(fileInputStream.getFD());
                mediaPlayer.prepare();
            } catch (IOException e) {
                ToastUtils.showMessage(getApplicationContext(), "play error  退出该页面重新进入试试~");
                e.printStackTrace();
            }
            mediaPlayer.start();
            if(voicePlayingAnimationView==null){
                voicePlayingAnimationView=new VoicePlayingAnimationView(context,iRecordFiles.get(position));
            }else{
                voicePlayingAnimationView.setArmName(iRecordFiles.get(position));
            }
            relativeLayout.removeView(voicePlayingAnimationView);
            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            relativeLayout.addView(voicePlayingAnimationView,params );
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            voicePlayingAnimationView.show();
        }
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.reset();
                if(voicePlayingAnimationView!=null) {
                    voicePlayingAnimationView.hide();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        if(voicePlayingAnimationView!=null) {
            voicePlayingAnimationView.hide();
        }
    }
}
