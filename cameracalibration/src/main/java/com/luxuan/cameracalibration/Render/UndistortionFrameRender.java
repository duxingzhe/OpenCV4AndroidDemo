package com.luxuan.cameracalibration.Render;

import com.luxuan.cameracalibration.CameraCalibrator;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;

public class UndistortionFrameRender extends FrameRender {

    public UndistortionFrameRender(CameraCalibrator calibrator){
        mCalibrator=calibrator;
    }

    @Override
    public Mat render(CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        Mat renderedFrame=new Mat(inputFrame.rgba().size(), inputFrame.rgba().type());
        Calib3d.undistort(inputFrame.rgba(), renderedFrame, mCalibrator.getCameraMatrix(), mCalibrator.getDistortionCoefficients());

        return renderedFrame;
    }
}
