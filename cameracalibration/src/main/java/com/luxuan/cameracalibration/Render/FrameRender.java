package com.luxuan.cameracalibration.Render;

import com.luxuan.cameracalibration.CameraCalibrator;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

public abstract class FrameRender {

    protected CameraCalibrator mCalibrator;

    public abstract Mat render(CameraBridgeViewBase.CvCameraViewFrame inputFrame);
}
