package com.luxuan.cameracalibration;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.opencv.core.Mat;

public abstract class CalibrationResult {

    private static final String TAG="OCVSample::CalibrationResult";

    private static final int CAMERA_MATRIX_ROWS=3;
    private static final int CAMERA_MATRIX_COLS=5;
    private static final int DISTORTION_COEFFICIENTS_SIZE=5;

    public static void save(Activity activity, Mat cameraMatrix, Mat distortionCoefficients){
        SharedPreferences sharedPref=activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPref.edit();

        double[] cameraMatrixArray=new double[CAMERA_MATRIX_ROWS*CAMERA_MATRIX_COLS];
        cameraMatrix.get(0,0,cameraMatrixArray);
        for(int i=0;i<CAMERA_MATRIX_ROWS;i++){
            for(int j=0;j<CAMERA_MATRIX_COLS;j++){
                Integer id=i*CAMERA_MATRIX_ROWS+j;
                editor.putFloat(id.toString(), (float)cameraMatrixArray[id]);
            }
        }

        double[] distortionCoefficientsArray=new double[DISTORTION_COEFFICIENTS_SIZE];
        distortionCoefficients.get(0,0, distortionCoefficientsArray);
        int shift=CAMERA_MATRIX_ROWS*CAMERA_MATRIX_COLS;
        for(Integer i=shift;i<DISTORTION_COEFFICIENTS_SIZE+shift;i++){
            editor.putFloat(i.toString(), (float)distortionCoefficientsArray[i-shift]);
        }

        editor.commit();
        Log.i(TAG, "Saved camera matrix: "+cameraMatrix.dump());
        Log.i(TAG, "Saved distortion coefficients: "+ distortionCoefficients.dump());
    }
}
