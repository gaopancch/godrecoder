package com.gaopan.godrecoder;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.R.attr.name;

public class MoveViewActivity extends Activity {
    private ImageView moveImage;
    private RelativeLayout mRelativeLayout;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_view);
        moveImage = (ImageView) findViewById(R.id.move_image);
        textView = (TextView) findViewById(R.id.textView);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.activity_move_view);
        moveImage.setOnTouchListener(onTouchListener);
        textView.setOnTouchListener(onTouchListener);
    }

    /**
     * 是否移动过
     */
    private boolean isMove;
    private float mLastX;
    private float mLastY;
    private float mStartX;
    private float mStartY;
    private long mLastTime;
    private long mCurrentTime;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        int lastX;
        int lastY;
        int left;
        int top;
        int right;
        int bottom;
        int screenWidth;
        int screenHeight;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    isMove = false;
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    mStartX = event.getRawX();
                    mStartY = event.getRawY();
                    mLastTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;
                    screenWidth = mRelativeLayout.getWidth();
                    screenHeight = mRelativeLayout.getHeight();

                    if (dx != 0 || dy != 0) {
                        isMove = true;
                    }

                    left = view.getLeft() + dx;
                    top = view.getTop() + dy;
                    right = view.getRight() + dx;
                    bottom = view.getBottom() + dy;
                    if (left < 0) {
                        left = 0;
                        right = left + view.getWidth();
                    }
                    if (right > screenWidth) {
                        right = screenWidth;
                        left = right - view.getWidth();
                    }
                    if (top < 0) {
                        top = 0;
                        bottom = top + view.getHeight();
                    }
                    if (bottom > screenHeight) {
                        bottom = screenHeight;
                        top = bottom - view.getHeight();
                    }
                    view.layout(left, top, right, bottom);
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    mLastX = event.getRawX();
                    mLastY = event.getRawY();
                    mCurrentTime = System.currentTimeMillis();
                    if (mCurrentTime - mLastTime < 800) {//长按不起作用
                        if (Math.abs(mStartX - mLastX) < 10.0 && Math.abs(mStartY - mLastY) < 10.0) {//判断是否属于点击
                            Toast.makeText(MoveViewActivity.this, "可以执行点击任务", Toast.LENGTH_SHORT).show();
                        }
                        if (view.getId() == R.id.move_image) {
                            changeAppLauncherIcon("com.gaopan.godrecoder.newsLuncherActivity");
                        } else {
                            changeAppLauncherIcon("com.gaopan.godrecoder.MainActivity");
                        }
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private void changeAppLauncherIcon(String name) {
//        PackageManager pm = getPackageManager();
//        pm.setComponentEnabledSetting(getComponentName(),
//                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
//        pm.setComponentEnabledSetting(new ComponentName(getApplication(), name),
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        //Intent 重启 Launcher 应用
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        List<ResolveInfo> resolves = pm.queryIntentActivities(intent, 0);
//        for (ResolveInfo res : resolves) {
//            if (res.activityInfo != null) {
//                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//                am.killBackgroundProcesses(res.activityInfo.packageName);
//            }
//        }
    }
}
