package com.luxuan.colorblob;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.List;

public class ColorBlobDetector {

    private Scalar mLowerBound=new Scalar(0);
    private Scalar mUpperBound=new Scalar(0);

    private static double mMinContourArea=0.1;

    private Scalar mColorRadius=new Scalar(25,50,50,0);
    private Mat mSpectrum =new Mat();
    private List<MatOfPoint> mContours=new ArrayList<>();

    Mat mPyrDownMat=new Mat();
    Mat mHsvMat=new Mat();
    Mat mMask=new Mat();
    Mat mDilatedMask=new Mat();
    Mat mHierarchy=new Mat();
}
