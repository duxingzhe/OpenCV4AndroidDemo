package com.luxuan.androidimageanalysis.tensorflow;

import android.content.res.AssetManager;
import android.util.Log;

import org.tensorflow.Operation;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public static Classifier create(
            AssetManager assetManager,
            String modelFilename,
            String labelFilename,
            int inputSize,
            int imageMean,
            float imageStd,
            String inputName,
            String outputName){
        TensorFlowImageClassifier c=new TensorFlowImageClassifier();
        c.inputName=inputName;
        c.outputName=outputName;

        String actualFilename=labelFilename.split("file:///android_asset/")[1];
        Log.i(TAG, "Reading labels from: "+actualFilename);
        BufferedReader br=null;
        try{
            br=new BufferedReader(new InputStreamReader(assetManager.open(actualFilename)));
            String line;
            while((line=br.readLine())!=null){
                c.labels.add(line);
            }
            br.close();
        }catch(IOException e){
            throw new RuntimeException("Problem reading label file!", e);
        }

        c.inferenceInterface=new TensorFlowInferenceInterface(assetManager, modelFilename);

        final Operation operation =c.inferenceInterface.graphOperation(outputName);
        final int numClasses=(int)operation.output(0).shape().size(1);
        Log.i(TAG, "Read "+c.labels.size()+" labels, output layer size is "+numClasses);

        c.inputSize=inputSize;
        c.imageMean=imageMean;
        c.imageStd=imageStd;

        c.outputNames=new String[]{outputName};
        c.intValues=new int[inputSize*inputSize];
        c.floatValues=new float[inputSize*inputSize*3];
        c.outputs=new float[numClasses];

        return c;
    }

}
