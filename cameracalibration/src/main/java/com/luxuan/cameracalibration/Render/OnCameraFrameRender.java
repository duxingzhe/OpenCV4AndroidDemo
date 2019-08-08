package com.luxuan.cameracalibration.Render;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

public class OnCameraFrameRender {

    private FrameRender mFrameRender;

    public OnCameraFrameRender(FrameRender frameRender){
        mFrameRender=frameRender;
    }

    public Mat render(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        return mFrameRender.render(inputFrame);
    }
}
