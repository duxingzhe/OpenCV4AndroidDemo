package com.luxuan.stitcher.stitcher;

import android.content.Context;
import android.hardware.Camera;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CameraScreenActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPictureCallback;
    private boolean previewing=false;
    private LayoutInflater controlInflater=null;
    private ImageView setting1, single, setting2, single_batch_page;
    private Button camera_click, batch_click, front_camrea_btn, batch;

    private Uri fileUri, outputFileUri;
    private TextView textView;
    protected String imageFilePath;
    private LinearLayout layout1, layout2, layout3;
    private LinearLayout camera_preview;
    public static int count=0;

    private ImageView pictureImageView;
    private View pictureCountBackgroundView;
    private TextView pictureCountBackgroundTextView;
    private Camera.Parameters parameters;
    private boolean isFlashOn;
    private static String formattedDate;
    private boolean cameraFront=false;

    private LinearLayout setting_before, setting_after,camera_single, camera_batch, batch_before, batch_after, single_before, single_after, flashOn, flashOff, settingTest;
    private TextView modeTextView;
    private ImageView settingbefore, settingafter, single_mode, single_mode_again, batch_mode_before, batch_mode_after, settingtest;
    private Button camerasingle, cameraBatch;
    private LinearLayout extra_settings;
    private ImageView flash_on, flash_off;
    private Context mContext;
    private LinearLayout picturecount;

}
