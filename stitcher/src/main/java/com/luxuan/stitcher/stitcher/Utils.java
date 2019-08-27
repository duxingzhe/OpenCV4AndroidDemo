package com.luxuan.stitcher.stitcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Utils {

    private Context mContext;

    private static int RESULT_LOAD_IMAGE=1;

    public Utils(Context context){
        mContext=context;
    }

    private Utils(){

    }

    public static Uri getUri(Context context, Bitmap bitmap){
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path= MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmap, "Title", null);
        return Uri.parse(path);
    }

    public static Bitmap getBitmap(Context context, Uri uri) throws IOException {
        Bitmap bitmap=MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        return bitmap;
    }
}
