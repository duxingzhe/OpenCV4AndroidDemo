package com.luxuan.stitcher.stitcher.Activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.luxuan.stitcher.R;
import com.luxuan.stitcher.stitcher.widget.PolygonView;

import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private ImageView iv_show_img;
    private PolygonView polygonView;
    private String imgDecodeStr;
    private Button scanButton;
    private FrameLayout sourceFrameLayout;
    private Uri imageUri;
    private Bitmap bitmap, op, op2;
    private int status=0;
    private FileOutputStream fos;
    private float perWidth, perHieght;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
