package com.luxuan.stitcher.stitcher.Util;

import android.graphics.Bitmap;

public class OpenCVHelper {

    static {
        System.loadLibrary("OpenCV");
    }

    public static native int[] gray(int[] buf, int w, int h);
    public static native int[] getBoxPoints(int[] buf, int w, int h);
    public static native int[] perspective(int[] buf, int[] points, int w, int h);
    public static native Bitmap getMagicColorBitmap(Bitmap bitmap);
    public static native Bitmap getGrayBitmap(Bitmap bitmap);
    public static native Bitmap getMagicBitmap(Bitmap bitmap);
    public static native Bitmap getGrayBitmapP(Bitmap bitmap);
    public static native Bitmap getLightedBitmap(Bitmap bitmap);
}
