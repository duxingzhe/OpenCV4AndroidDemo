package com.luxuan.cameracalibration.Render;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

public class PreviewFrameRender extends FrameRender {

    @Override
    public Mat render(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        return inputFrame.rgba();
    }
}
