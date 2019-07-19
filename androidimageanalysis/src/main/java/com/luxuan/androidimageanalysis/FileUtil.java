package com.luxuan.androidimageanalysis;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

public class FileUtil {

    public static void notifyScanMediaFile(Context context, String filePath){
        if(context==null|| TextUtils.isEmpty(filePath)){
            Log.e("FileUtil", "notifyScanMediaFile context is null or filePath is empty.");
        }

        MediaScannerConnection.scanFile(context, new String[]{filePath}, null, new MediaScannerConnection.OnScanCompletedListener(){

                @Override
                public void onScanCompleted(String path, Uri uri){
                    Log.i("FileUtil", "onScanCompleted");
                }
            }
        );
    }

    public static File getPhotoCacheFolder(){
        File cacheFolder=new File(Environment.getExternalStorageDirectory(), "TensorFlowPhotos");
        if(!cacheFolder.exists()){
            cacheFolder.mkdirs();
        }
        return cacheFolder;
    }
}
