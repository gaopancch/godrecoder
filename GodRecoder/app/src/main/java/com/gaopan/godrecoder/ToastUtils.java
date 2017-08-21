package com.gaopan.godrecoder;

import android.content.Context;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by gaopan on 2017/5/25.
 */

public class ToastUtils {
    private static boolean isShow = false;
    private static Timer timer = new Timer();
    private static Toast toast = null;

    /**
     * msg is StringUtils type
     *
     * @param act
     * @param msg
     */
    public static void showMessage(final Context act, String msg) {
        showMessage(act, msg, Toast.LENGTH_SHORT);
    }

    /**
     * msg is resId type
     *
     * @param act
     * @param msg
     */
    public static void showMessage(final Context act, final int msg) {
        showMessage(act, msg, Toast.LENGTH_SHORT);
    }

    public static void showMessage(final Context act, String msg, int gravity,
                                   int xOffset, int yOffset) {
        showMessage(act, msg, Toast.LENGTH_SHORT, gravity, xOffset, yOffset);
    }

    private static void showMessage(final Context act, final String msg,
                                    final int len) {
        if (toast != null) {
            toast.setText(msg);
            toast.setDuration(len);
        } else {
            toast = Toast.makeText(act, msg, len);
        }
        toast.show();
    }

    private static void showMessage(final Context act, final int msg,
                                    final int len) {
        if (toast != null) {
            toast.setText(msg);
            toast.setDuration(len);
        } else {
            toast = Toast.makeText(act, msg, len);
        }
        toast.show();
    }

    private static void showMessage(final Context act, String msg, int len,
                                    int gravity, int xOffset, int yOffset) {
        if (isShow) {
            return;
        } else {
            isShow = true;
            Toast toast = Toast.makeText(act, msg, len);
            toast.setGravity(gravity, xOffset, yOffset);
            toast.show();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isShow = false;
                }
            }, 2000);
        }
    }
}


