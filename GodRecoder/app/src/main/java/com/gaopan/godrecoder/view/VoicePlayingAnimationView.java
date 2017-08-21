package com.gaopan.godrecoder.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by gaopan on 2017/7/7.
 */

public class VoicePlayingAnimationView extends LinearLayout{

    private Context mContext;
    private TextView titleTextView;
    private TextView armNameTextView;
    private TextView pointTextView;
    private Handler pointChangeHandler;
    private Runnable pointChangeRunnable;
    private String point="。";
    private String armName="未知声音源";

    public VoicePlayingAnimationView(Context context,String armName) {
        super(context);
//        setVisibility(View.GONE);
        mContext=context;
        this.armName=armName;
        init();
    }

    public void setArmName(String armName){
        this.armName=armName;
    }

    public void show(){
        setVisibility(View.VISIBLE);
        armNameTextView.setText(armName);
        pointChangeHandler.removeCallbacksAndMessages(null);
        pointChangeHandler.postDelayed(pointChangeRunnable,1000);
    }

    public void hide(){
        setVisibility(View.GONE);
        point="。";
        pointChangeHandler.removeCallbacksAndMessages(null);
    }

    private void init(){
        setOrientation(LinearLayout.VERTICAL);
        initView();
        initHandler();
        setGravity(Gravity.CENTER);
        setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void initView(){
        titleTextView=new TextView(mContext);
        titleTextView.setText("正在播放");
        titleTextView.setTextSize(20);
        armNameTextView=new TextView(mContext);
        armNameTextView.setTextSize(12);
        armNameTextView.setText(armName);
        pointTextView=new TextView(mContext);
        pointTextView.setTextSize(12);
        addView(titleTextView);
        addView(armNameTextView);
        addView(pointTextView);
        setBackgroundColor(Color.CYAN);
    }

    private void initHandler(){
        pointChangeHandler=new Handler();
        pointChangeRunnable=new Runnable() {
            @Override
            public void run() {
                pointTextView.setText(point);
                if(point.length()>9){
                    point="";
                }
                point+="。";
                pointChangeHandler.postDelayed(pointChangeRunnable,500);
            }
        };
    }
}
