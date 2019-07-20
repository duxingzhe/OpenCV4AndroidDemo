package com.luxuan.androidimageanalysis.tensorflow;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.Vector;

public class TensorFlowImageClassifier implements Classifier {

    private static final String TAG="tensorflow";

    private static final int MAX_RESULTS=3;
    private static final float THRESHOLD=0.1f;

    private String inputName;
    private String outputName;
    private int inputSize;
    private int imageMean;
    private float imageStd;

    private Vector<String> labels=new Vector<>();
    private int[] intValues;
    private float[] floatValues;
    private float[] outputs;
    private String[] outputNames;

    private boolean logStates=false;

    private TensorFlowInferenceInterface inferenceInterface;

    private TensorFlowImageClassifier(){

    }


}
