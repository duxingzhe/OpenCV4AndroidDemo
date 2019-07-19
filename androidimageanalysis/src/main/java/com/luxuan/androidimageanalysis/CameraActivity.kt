package com.luxuan.androidimageanalysis

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.Mat
import java.util.concurrent.Executor

class CameraActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener {

    private var grayscaleImage: Mat?=null
    private var absoluteFaceSize: Int =0

    private val INPUT_SIZE=224
    private val IMAGE_MEAN=117
    private val IMAGE_STD=1f
    private val INPUT_NAME="input"
    private val OUTPUT_NAME="output"
    private val MODEL_FILE="file:///android_asset/model/tensorflow_inception_graph.pb"
    private val LABEL_FILE="file:///android_asset/model/imagenet_comp_label_strings.txt"

    private var executor: Executor?=null
    private var currentTakePhotoUri: Uri?=null
    private var classifer: Classifier?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
    }
}