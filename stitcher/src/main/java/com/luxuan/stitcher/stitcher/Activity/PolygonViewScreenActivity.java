package com.luxuan.stitcher.stitcher.Activity;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luxuan.stitcher.stitcher.widget.PolygonView;

import java.io.FileOutputStream;
import java.util.Map;

public class PolygonViewScreenActivity extends AppCompatActivity {

    private ImageView imageView;
    private Uri uri;
    private PolygonView polygonView;
    private Uri imageUriFromGallery, imageUriFromCamera, test_uri, imageUrifromBatch;
    private Bitmap bitmap, op, op2, tempBitmap;
    private int status=0;
    private FileOutputStream fos;
    private float persWidth, persHeight;
    private Bitmap original, scaledBitmap;
    private FrameLayout sourceFrame;
    private LinearLayout graLayout, bwLayout, enhancedLayout, lightenLayout, normalLayout, colorLayout;
    private ImageView gray, bw, enhanced, lighten, normal;
    private TextView tGray, tBW, tEnhanced, tLighten, tNormal;
    private static String test;
    private Uri test1;
    private Matrix matrix;
    private Map<Integer, PointF> pointFs;
    private RelativeLayout grievanceProgressBar;

}
