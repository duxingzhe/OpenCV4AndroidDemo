package com.luxuan.opencv;

import android.app.Activity;
import android.os.Bundle;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

public class FaceDetectorActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private Mat mRgba;
    private Mat mGray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onCameraViewStarted(int width, int height)
    {
        mGray=new Mat();
        mRgba=new Mat();
    }
}
