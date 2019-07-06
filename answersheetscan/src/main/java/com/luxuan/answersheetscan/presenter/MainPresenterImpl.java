package com.luxuan.answersheetscan.presenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.luxuan.answersheetscan.config.AnswerSheetConfig;
import com.luxuan.answersheetscan.model.AnswerSheetModel;
import com.luxuan.answersheetscan.utils.ThreadUtils;
import com.luxuan.answersheetscan.view.MainActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainPresenterImpl implements MainPresenter {

    private static final String TAG="MainPresenter";
    private LoaderCallbackInterface mLoaderCallback;
    private boolean NEED_CROP=false;
    private final int GALLERY_REQUEST_CODE=1;
    private final int CAMERA_REQUEST_CODE=2;
    private final int CROP_REQUEST_CODE=3;
    private final int ADVANCE_CROP_REQUEST_CODE=4;
    private final MainActivity mActivity;

    private File mTempFileDir=new File(Environment.getExternalStorageDirectory().getPath()+"/opencvdemo/cache");
    private File mTempFile;
    private File mCropFile;
    private String mPhotoFileName;
    private String mCropImgPath;

    public MainPresenterImpl(MainActivity activity){
        mActivity=activity;
    }

    @Override
    public void initOpenCV(){
        mLoaderCallback=new BaseLoaderCallback(mActivity.getApplicationContext()){
            @Override
            public void onManagerConnected(int status){
                switch(status){
                    case LoaderCallbackInterface.SUCCESS:
                        Log.e(TAG, "OpenCV loaded successfully");
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }

            @Override
            public void onPackageInstall(int operation, InstallCallbackInterface callback){

            }
        };

        if(!OpenCVLoader.initDebug()){
            Log.e(TAG,"Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, mActivity.getApplicationContext(),mLoaderCallback);
        }else{
            Log.e(TAG,"OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void handleActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case GALLERY_REQUEST_CODE:
                if(data!=null){
                    Uri uri=data.getData();
                    if(uri!=null){
                        if(NEED_CROP){
                            crop(uri);
                        }else{
                            String path=getPicPathInGallery(uri);
                            if(TextUtils.isEmpty(path)){
                                mTempFile=null;
                            }else{
                                mTempFile=new File(path);
                                mActivity.onPhotoGet(mTempFile);
                            }
                        }
                    }
                }
                break;
            case CAMERA_REQUEST_CODE:
                if(hasSdCard()){
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                        mTempFile=new File(Environment.getExternalStorageDirectory(), mPhotoFileName);
                        if(mTempFile.exists()&&mTempFile.length()>0){
                            if(NEED_CROP){
                                crop(getImageContentUri(mActivity, mTempFile));
                            }else{
                                mActivity.onPhotoGet(mTempFile);
                            }
                        }
                    }else{
                        mTempFile=new File(Environment.getExternalStorageDirectory(), mPhotoFileName);
                        if(mTempFile.exists()&&mTempFile.length()>0){
                            if(NEED_CROP){
                                crop(Uri.fromFile(mTempFile));
                            }else{
                                mActivity.onPhotoGet(mTempFile);
                            }
                        }
                    }
                }
                break;
            case CROP_REQUEST_CODE:
                if(resultCode== Activity.RESULT_OK){
                    File file=new File(mCropImgPath);
                    mActivity.onPhotoGet(file);
                }
                break;
            case ADVANCE_CROP_REQUEST_CODE:
                if(resultCode==Activity.RESULT_OK &&data!=null){
                    String cropPath=data.getStringExtra("cropPath");
                    mActivity.onPhotoGet(new File(cropPath), "原图");
                }
                break;
        }
    }

    @Override
    public void takePhoto(){
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(mActivity.getPackageManager())!=null){
            File photoFile=null;
            try{
                photoFile=creatImageFile();
            }catch(Exception e){
                e.printStackTrace();
            }
            if(photoFile!=null){
                Uri photoURI= FileProvider.getUriForFile(mActivity, "com.luxuan.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                mActivity.startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        }
    }

    @Override
    public void viewAlbum(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        mActivity.startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void cropPhoto(File file){
        Intent intent=new Intent(mActivity, CropActivity.class);
        intent.putExtra("filePath", file.getAbsolutePath());
        mActivity.startActivityForResult(intent, ADVANCE_CROP_REQUEST_CODE);
    }

    @Override
    public void dealWithPhoto(final File file){
        final int total=5;

        ThreadUtils.runOnSubThread(new Runnable(){
            @Override
            public void run(){
                Bitmap srcBitmap= BitmapFactory.decodeFile(file.getAbsolutePath());

                if(srcBitmap==null){
                    return;
                }

                Mat srcMat=new Mat();
                Utils.bitmapToMat(srcBitmap, srcMat);

                AnswerSheetModel answerSheet=new AnswerSheetModel(srcMat.width(), srcMat.height());

                Mat grayMat=doGray(srcBitmap, srcMat, 1, total);

                Mat blurMat=doBlur(srcBitmap, grayMat, 2, total);

                Mat binaryMat=doBinary(srcBitmap, blurMat, 3, total);

                Mat measureMat=doMeasure(srcBitmap, binaryMat, answerSheet, 4, total);

                checkAnswer(measureMat, answerSheet, 5, total);

                srcMat.release();
                grayMat.release();
                blurMat.release();
                binaryMat.release();
                measureMat.release();
            }
        });
    }

    private void checkAnswer(Mat preMat, AnswerSheetModel answerSheet, int current, int total){
        final String stepName="识别填涂位置";
        stepDealStart(current, total, stepName);
        try{
            for(int i=0;i<answerSheet.answerRows.size();i++){
                AnswerSheetModel.AnswerRowModel answerRow=answerSheet.answerRows.get(i);
                for(int j=0;j<answerRow.answers.size();j++){
                    AnswerSheetModel.AnswerSheetItemModel answerItem=answerRow.answers.get(j);
                    for(int k=0;k<answerItem.points.length;k++) {
                        if(k%2==0&&k<answerItem.points.length-1){
                            Point tlPoint=answerItem.points[k];
                            Point brPoint=answerItem.points[k+1];
                            float offsetX=answerSheet.answerWidth* AnswerSheetConfig.OPTION_SHRINK_FACTOR;
                            float offsetY=answerSheet.answerHeight* AnswerSheetConfig.OPTION_SHRINK_FACTOR;
                            Point targetTlPoint=new Point(tlPoint.x+offsetX, tlPoint.y+offsetY);
                            Point targetBrPoint=new Point(brPoint.x-offsetX, brPoint.y-offsetY);
                            Mat roiMat=preMat.submat(new Rect(targetTlPoint, targetBrPoint));
                            MatOfDouble meanMat=new MatOfDouble();
                            MatOfDouble stdDevMat=new MatOfDouble();
                            double[] mean=new double[1];
                            double[] stdDev=new double[1];
                            Core.meanStdDev(roiMat, meanMat, stdDevMat);
                            meanMat.get(0, 0, mean);
                            stdDevMat.get(0,0, stdDev);
                            float factor=formatDouble(mean[0]0);
                            switch(k/2){
                                case 0:
                                    answerItem.factorA=factor;
                                    break;
                                case 1:
                                    answerItem.factorB=factor;
                                    break;
                                case 2:
                                    answerItem.factorC=factor;
                                    break;
                                case 3:
                                    answerItem.factorD=factor;
                                    break;
                            }
                            meanMat.release();
                            stdDevMat.release();
                            roiMat.release();
                        }
                    }
                }
            }
            List<AnswerSheetModel.AnswerSheetItemModel> answers=new ArrayList<>();
            List<Float> factors=new ArrayList<>();
            for(AnswerSheetModel.AnswerRowModel answerRow: answerSheet.answerRows){
                for(AnswerSheetModel.AnswerSheetItemModel answer: answerRow.answers){
                    factors.add(answer.factorA);
                    factors.add(answer.factorB);
                    factors.add(answer.factorC);
                    factors.add(answer.factorD);
                }
            }

            Collections.sort(factors);
            float maxFactor=factors.get(factors.size()-1)>255F*AnswerSheetConfig.LIMIT_ACCEPT_MAX_FACTOR?255F*AnswerSheetConfig.LIMIT_ACCEPT_MAX_FACTOR:factors.get(factors.size()-1);
            float minFactor=factors.get(0);
            float limitMaxFactor=maxFactor*AnswerSheetConfig.LIMIT_ACCEPT_MAX_FACTOR;
            boolean limitMaxFactorIsValid=(maxFactor-minFactor)>255F*AnswerSheetConfig.LIMIT_ACCEPT_MIN_FACTOR;
            for(AnswerSheetModel.AnswerRowModel answerRow : answerSheet.answerRows){
                for(AnswerSheetModel.AnswerSheetItemModel answer: answerRow.answers){
                    answer.checkA=answer.factorA>limitMaxFactor&&limitMaxFactorIsValid;
                    answer.checkB=answer.factorB>limitMaxFactor&&limitMaxFactorIsValid;
                    answer.checkC=answer.factorC>limitMaxFactor&&limitMaxFactorIsValid;
                    answer.checkD=answer.factorD>limitMaxFactor&&limitMaxFactorIsValid;
                    if(!answer.checkA&&!answer.checkB&&!answer.checkC&&!answer.checkD){
                        float totalFactors=answer.factorA+answer.factorB+answer.factorC+answer.factorD;
                        answer.checkA=answer.factorA>totalFactors*AnswerSheetConfig.LIMIT_ACCEPT_TOTAL_PERCENT_FACTOR&&answer.factorB>maxFactor *AnswerSheetConfig.LIMIT_RECHECK_MIN_FACTOR&&limitMaxFactorIsValid;
                        answer.checkB=answer.factorB>totalFactors*AnswerSheetConfig.LIMIT_ACCEPT_TOTAL_PERCENT_FACTOR&&answer.factorB>maxFactor *AnswerSheetConfig.LIMIT_RECHECK_MIN_FACTOR&&limitMaxFactorIsValid;
                        answer.checkC=answer.factorC>totalFactors*AnswerSheetConfig.LIMIT_ACCEPT_TOTAL_PERCENT_FACTOR&&answer.factorB>maxFactor *AnswerSheetConfig.LIMIT_RECHECK_MIN_FACTOR&&limitMaxFactorIsValid;
                        answer.checkD=answer.factorD>totalFactors*AnswerSheetConfig.LIMIT_ACCEPT_TOTAL_PERCENT_FACTOR&&answer.factorB>maxFactor *AnswerSheetConfig.LIMIT_RECHECK_MIN_FACTOR&&limitMaxFactorIsValid;
                    }
                    answers.add(answer);
                }
            }
            stepDealComplete(current,total, stepName, null, answers, true);
        }catch(Exception e){
            stepDealComplete(current, total, stepName+"失败： "+e.getMessage());
        }
    }
}
