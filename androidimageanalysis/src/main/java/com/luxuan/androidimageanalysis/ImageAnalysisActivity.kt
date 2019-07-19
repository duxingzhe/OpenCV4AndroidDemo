package com.luxuan.androidimageanalysis

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.view.View
import java.util.concurrent.Executor

class ImageAnalysisActivity: AppCompatActivity(), View.OnClickListener{

    private val TAKE_PHOTO_REQUEST_CODE=120
    private val PICTURE_REQUEST_CODE=911

    private val CURRENT_REQUEST_CODE="currentTakePhotoUri"

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

}