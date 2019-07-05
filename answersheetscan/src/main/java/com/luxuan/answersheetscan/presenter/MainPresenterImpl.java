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

import com.luxuan.answersheetscan.model.AnswerSheetModel;
import com.luxuan.answersheetscan.utils.ThreadUtils;
import com.luxuan.answersheetscan.view.MainActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;

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
}
