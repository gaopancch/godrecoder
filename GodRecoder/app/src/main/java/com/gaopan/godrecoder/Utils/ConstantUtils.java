package com.gaopan.godrecoder.Utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by gaopan on 2017/6/29.
 */

public class ConstantUtils {
    /**sdk卡目录下 godrecoder文件夹 ，用于保存所有的录音文件*/
    public static String RECODER_FILE_SAVE_PATH=
            Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"godrecoder";

    public static String RECODER_FILE_SAVE_MARKED_PATH=RECODER_FILE_SAVE_PATH+File.separator+"markedrecoder";

    public static String MARK_ITEM="mark_item_";

    public static int SAVE_ARM_NUMBER=50;

    public static int DELETE_AMR_INTERVIAL=3000000;//1000 ->1s
}
