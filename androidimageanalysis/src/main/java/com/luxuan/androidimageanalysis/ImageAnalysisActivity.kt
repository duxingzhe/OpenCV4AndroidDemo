package com.luxuan.androidimageanalysis

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.MessageQueue
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.luxuan.androidimageanalysis.tensorflow.Classifier
import com.luxuan.androidimageanalysis.tensorflow.TensorFlowImageClassifier
import kotlinx.android.synthetic.main.activity_image_analysis.*
import java.io.File
import java.io.IOException
import java.util.concurrent.Executor
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadFactory

class ImageAnalysisActivity: AppCompatActivity(), View.OnClickListener{

    private val TAKE_PHOTO_REQUEST_CODE=120
    private val PICTURE_REQUEST_CODE=911

    private val CURRENT_TAKE_PHOTO_URI="currentTakePhotoUri"

    private val INPUT_SIZE=224
    private val IMAGE_MEAN=117
    private val IMAGE_STD=1f
    private val INPUT_NAME="input"
    private val OUTPUT_NAME="output"
    private val MODEL_FILE="file:///android_asset/model/tensorflow_inception_graph.pb"
    private val LABEL_FILE="file:///android_asset/model/imagenet_comp_label_strings.txt"

    private var executor: Executor?=null
    private var currentTakePhotoUri: Uri?=null
    private var classifier: Classifier?=null

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

    private var idleHandler: MessageQueue.IdleHandler= MessageQueue.IdleHandler{
        if(classifier==null){
            classifier= TensorFlowImageClassifier.create(this@ImageAnalysisActivity.assets, MODEL_FILE, LABEL_FILE,
                    INPUT_SIZE, IMAGE_MEAN, IMAGE_STD, INPUT_NAME, OUTPUT_NAME)
        }

        executor= ScheduledThreadPoolExecutor(1, ThreadFactory{ r->
            var thread=Thread(r)
            thread.isDaemon=true
            thread.name="ThreadPool-ImageClassifier"
            thread
        })

        false
    }

    override fun onClick(view: View){
        when(view.id){
            R.id.ivChoose->choosePicture()
            R.id.ivTakePhoto->takePhoto()
            else->{

            }
        }
    }

    private fun choosePicture(){
        val intent=Intent(Intent.ACTION_GET_CONTENT)
        intent.type="image/*"
        startActivityForResult(intent, PICTURE_REQUEST_CODE)
    }

    private fun takePhoto(){
        openSystemCamera()
    }

    private fun openSystemCamera(){
        val takePhotoIntent= Intent()
        takePhotoIntent.action= MediaStore.ACTION_IMAGE_CAPTURE

        if(takePhotoIntent.resolveActivity(packageManager)==null){
            Toast.makeText(this, "当前系统没有可用的相机应用", Toast.LENGTH_SHORT).show()
            return
        }

        val fileName="TF_"+System.currentTimeMillis()+".jpg"
        val photoFile= File(FileUtil.getPhotoCacheFolder(), fileName)

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
            currentTakePhotoUri= FileProvider.getUriForFile(this, "com.luxuan.androidimageanalysis.file", photoFile)
            takePhotoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }else{
            currentTakePhotoUri=Uri.fromFile(photoFile)
        }

        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentTakePhotoUri)
        startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST_CODE)
    }

    private fun handleInputPhoto(imageUri: Uri?){
        Glide.with(this@ImageAnalysisActivity).asBitmap().listener(object: RequestListener<Bitmap> {

            override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Bitmap>, isFirstResource: Boolean): Boolean{
                Toast.makeText(this@ImageAnalysisActivity, "图片加载失败", Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onResourceReady(resource: Bitmap, model: Any, target: Target<Bitmap>, dataSource: DataSource, isFirstResource: Boolean): Boolean{
                startImageClassifier(resource)
                return false
            }
        }).load(imageUri).into(ivPicture)
        tvInfo.text="Processing..."
    }

    private fun startImageClassifier(bitmap : Bitmap){
        executor?.execute{
            try{
                Log.i("111", Thread.currentThread().name+" startImageClassifier")
                val croppedBitmap=getScaledBitmap(bitmap, INPUT_SIZE)

                val results=classifier?.recognizeImage(croppedBitmap)
                Log.i("111", "startImageClassifier results: $results")
                runOnUiThread{
                    tvInfo.text=String.format("results: %s", results)
                }
            }catch(e: IOException){
                Log.e("111", "startImageClassifier getScaleBitmap "+ e.message)
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    private fun getScaledBitmap(bitmap: Bitmap, size: Int): Bitmap{
        val width=bitmap.width
        val height=bitmap.height
        val scaleWidth=size.toFloat()/width
        val scaleHeight=size.toFloat()/height
        val matrix= Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

}