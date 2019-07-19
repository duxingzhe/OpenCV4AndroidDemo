package com.luxuan.androidimageanalysis

import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.view.View
import java.util.concurrent.Executor

class ImageAnalysisActivity: AppCompatActivity(), View.OnClickListener{

    private val TAKE_PHOTO_REQUEST_CODE=120
    private val PICTURE_REQUEST_CODE=911

    private val CURRENT_TAKE_PHOTO_URI="currentTakePhotoUri"

    private val INPUT_SIZE=224
    private val IMAGE_MEAN=117
    private val IMAGE_STD=1f
    private val INPUT_NAME="input"
    private val OUTPUT_NAME="output"
    private val MODEL_FILE="file:///android_asset/model/tensorflow_pb"
    private val LABEL_FILE="file:///android_asset/model/imagenet_comp_label_strings.txt"

    private var executor: Executor?=null
    private var currentTakePhotoUri: Uri?=null
    private var classifer: Classifier?=null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_analysis)

        ivChoose.setOnClickListener(this)
        ivTakePhoto.setOnClickListener(this)
        Looper.myQueue().addIdleHandler(idleHandler)
    }

    override fun onSaveInstanceState(outState: Bundle?){
        outState?.putParcelable(CURRENT_TAKE_PHOTO_URI, currentTakePhotoUri)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?){
        super.onRestoreInstanceState(savedInstanceState)
        if(savedInstanceState!=null){
            currentTakePhotoUri=savedInstanceState.getParcelable(CURRENT_TAKE_PHOTO_URI)
        }
    }

}