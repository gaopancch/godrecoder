package com.gaopan.godrecoder.listener;

import android.os.FileObserver;
import android.util.Log;

/**
 * Created by gaopan on 2017/7/4.
 */

public class AmrDirectoryObserver extends FileObserver {

    public AmrDirectoryObserver(String path) {
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
                Log.i("AmrDirectoryObserver", "path:"+ path);
                break;
            case FileObserver.CLOSE_WRITE:
                Log.i("AmrDirectoryObserver", "path:"+ path);
                break;
            case FileObserver.OPEN:
                Log.i("AmrDirectoryObserver", "path:"+ path);
                break;
            case FileObserver.ALL_EVENTS:
                Log.i("AmrDirectoryObserver", "path:"+ path);
                break;
            case FileObserver.CREATE:
                Log.i("AmrDirectoryObserver", "CREATE path:"+ path);
                break;
        }
    }
}
