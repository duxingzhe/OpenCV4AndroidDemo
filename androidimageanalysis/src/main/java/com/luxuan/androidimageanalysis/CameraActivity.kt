package com.luxuan.androidimageanalysis

import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.os.MessageQueue
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_camera.*
import org.opencv.android.CameraBridgeViewBase
import org.opencv.core.Mat
import java.util.concurrent.Executor
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadFactory

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
    private var classifier: Classifier?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        cameraView.setCameraIndex(0)
        cameraView.enableFpsMeter()
        cameraView.setCvCameraViewListener(this)
        cameraView.enableView()

        Looper.myQueue().addIdleHandler(idleHandler)
    }

    private var idleHandler: MessageQueue.IdleHandler=MessageQueue.IdleHandler{
        if(classifier==null){
            classifier=TensorFlowImageClassifier.create(this@CameraActivity.assets,
                    MODEL_FILE, LABEL_FILE, INPUT_SIZE, IMAGE_MEAN, IMAGE_STD, INPUT_NAME, OUTPUT_NAME)
        }

        executor= ScheduledThreadPoolExecutor(1, ThreadFactory{ r->
            val thread=Thread(r)
            thread.isDaemon=true
            thread.name="ThreadPool-ImageClassifier"
            thread
        })

        false
    }
}