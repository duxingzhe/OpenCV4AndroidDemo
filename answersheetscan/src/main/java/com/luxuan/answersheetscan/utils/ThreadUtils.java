package com.luxuan.answersheetscan.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThreadUtils {

    private ThreadUtils(){

    }

    private static Handler sHandler=new Handler(Looper.getMainLooper());

    private static Executor sExecutors= Executors.newCachedThreadPool();

    public static void runOnSubThread(Runnable runnable){
        sExecutors.execute(runnable);
    }

    public static void runOnUIThread(Runnable runnable){
        sHandler.post(runnable);
    }
}
