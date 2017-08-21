package com.gaopan.godrecoder.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by gaopan on 2017/5/31.
 */

public class PreferenceUtil {
    private static SharedPreferences getDefault(final Context context) {
        return context.getSharedPreferences("app_setting", Context.MODE_PRIVATE);
    }
    private static void save(final SharedPreferences.Editor editor) {
        if (android.os.Build.VERSION.SDK_INT >= 9) {
            editor.apply();
        } else {
            editor.commit();
        }
    }
    public static int getInt(final String key, final int defaultValue,
                             Context context) {
        final SharedPreferences prefs = getDefault(context);
        return prefs.getInt(key, defaultValue);
    }

    public static void putInt(final String key, final int value, Context context) {
        final SharedPreferences prefs = getDefault(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        save(editor);
    }

    public static long getLong(final String key, final long defaultValue,
                               Context context) {
        try {
            final SharedPreferences prefs = getDefault(context);
            return prefs.getLong(key, defaultValue);
        }catch (Exception e){
            return 0;
        }
    }

    public static void putLong(final String key, final long value,
                               Context context) {
        try {
            final SharedPreferences prefs = getDefault(context);
            final SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(key, value);
            save(editor);
        }catch (Exception e){}
    }

    public static String getString(final String key, final String defaultValue,
                                   Context context) {
        final SharedPreferences prefs = getDefault(context);
        return prefs.getString(key, defaultValue);
    }

    public static void putString(final String key, final String value,
                                 Context context) {
        final SharedPreferences prefs = getDefault(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        save(editor);
    }

    public static boolean getBoolean(final String key,
                                     final boolean defaultValue, Context context) {
        final SharedPreferences prefs = getDefault(context);
        return prefs.getBoolean(key, defaultValue);
    }

    public static void putBoolean(final String key, final boolean value,
                                  Context context) {
        final SharedPreferences prefs = getDefault(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        save(editor);
    }

    public static float getFloat(final String key, final float defaultValue,
                                 Context context) {
        final SharedPreferences prefs = getDefault(context);
        return prefs.getFloat(key, defaultValue);
    }

    public static void putFloat(final String key, final float value,
                                Context context) {
        final SharedPreferences prefs = getDefault(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);
        save(editor);
    }

    public static void makrData(Context context,String data){
        int markListSize=getInt("markListSize",0,context);
        putString(ConstantUtils.MARK_ITEM+markListSize,data,context);
        putInt("markListSize",++markListSize,context);
    }

    public static void deleteMakrData(Context context,String data){
        ArrayList<String> markList=new ArrayList<String>();
        int markListSize=getInt("markListSize",0,context);
        boolean hasData=false;
        //将除去要删除的数据以外的其他数据，全部存入list
        for(int i=0;i<markListSize;i++){
            String getData=getString(ConstantUtils.MARK_ITEM+i,"null",context);
            if(!data.equals(getData)){
                markList.add(getString(ConstantUtils.MARK_ITEM+i,"null",context));
            }else{
                hasData=true;
            }
        }
        if(hasData) {
            //将list的中的数据重新存储
            for (int i = 0; i < markList.size(); i++) {
                putString(ConstantUtils.MARK_ITEM + i, markList.get(i), context);
            }
            //更新已经存储的数据总数
            putInt("markListSize", --markListSize, context);
            //此时重新存储的数据已经去掉要删除的数据，同时，数据变为最新
        }
    }

    public static ArrayList<String> getMakrList(Context context){
        int markListSize=getInt("markListSize",0,context);
        ArrayList<String> markList=new ArrayList<String>();
        for(int i=0;i<markListSize;i++){
            markList.add(getString(ConstantUtils.MARK_ITEM+i,"null",context));
        }
        return  markList;
    }
}

