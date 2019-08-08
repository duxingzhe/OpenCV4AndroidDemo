package com.luxuan.cameracalibration.Render;

import com.luxuan.cameracalibration.CameraCalibrator;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

public class CalibrationFrameRender extends FrameRender {

    public CalibrationFrameRender(CameraCalibrator calibrator){
        mCalibrator=calibrator;
    }

    @Override
    public Mat render(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        Mat rgbaFrame=inputFrame.rgba();
        Mat grayFrame=inputFrame.gray();
        mCalibrator.processFrame(grayFrame, rgbaFrame);

        return rgbaFrame;
    }
}
