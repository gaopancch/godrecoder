package com.gaopan.godrecoder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.gaopan.godrecoder.view.CircleAnimView;

public class AnimationLauncherActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private RelativeLayout.LayoutParams computeAnimationIconLayoutParams(View icon, Rect rect) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) icon.getLayoutParams();
        layoutParams.topMargin = rect.top - this.getStatusBarHeight();
        layoutParams.leftMargin = rect.left;
        layoutParams.width = rect.right - rect.left;
        layoutParams.height = layoutParams.width;
        return layoutParams;
    }

    private int getStatusBarHeight() {
        int height;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);//134217728
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//67108864
            height = 0;
        } else {
            int identifier = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (identifier > 0) {
                height = this.getResources().getDimensionPixelSize(identifier);
            } else {
                height = 0;
            }
        }
        return height;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        if (intent != null && (intent.getAction().equals("android.intent.action.MAIN"))) {
            this.overridePendingTransition(0, 0);
            this.setContentView(R.layout.activity_animation_launcher);
            CircleAnimView rocketView = (CircleAnimView) this.findViewById(R.id.rocket_view);
            Rect rect = intent.getSourceBounds();//获取icon坐标信息

            ((RelativeLayout) this.findViewById(R.id.root)).updateViewLayout(
                    rocketView, this.computeAnimationIconLayoutParams(
                            rocketView, rect));
            rocketView.startAnimation(360, new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    Intent intent=new Intent();
                    intent.setClass(AnimationLauncherActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();//动画结束,自己退出
                    overridePendingTransition(0, 0);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationStart(Animation animation) {
                }
            });
        } else {
            finish();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            this.overridePendingTransition(0, 0);
        }

        return super.onKeyDown(keyCode, event);
    }
}
