package com.luxuan.stitcher.stitcher.Activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileOutputStream;

public class ColorImageActivity extends AppCompatActivity {

    private FrameLayout sourceFrame;
    private LinearLayout graLayout, bwLayout, ehancedLayout, lightenLayout, normalLayout, colorLayout;
    private ImageView grayImage, bwImage, enhancedImage, lightenImage, normalImage, actalImage;
    private TextView tvGray, tvBW, tvEnhaneced, tvLighten, tvNormal;
    private Uri uri;

    private Bitmap original, bitmap;
    private FileOutputStream fos;
    private FloatingActionButton fab;

}
