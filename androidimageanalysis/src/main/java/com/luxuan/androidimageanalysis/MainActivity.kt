package com.luxuan.androidimageanalysis

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader

class MainActivity : AppCompatActivity() {

    private val CAMERA_REQUEST=0x110
    private val PICK_IMAGE_REQUEST=1

    private var selectbp: Bitmap? = null
    private val mOpenCVCallBack=object: BaseLoaderCallback(this){

        override fun onManagerConnected(status: Int){
            when(status) {
                LoaderCallbackInterface.SUCCESS -> {
                    Toast.makeText(this@MainActivity, "OpenCV loaded successfully.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    super.onManagerConnected(status)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPermission()
        staticLoadCVLibraries()

        btnSelect.setOnClickListener{
            selectImg()
        }

        btnProcess.setOnClickListener{
            convertGray()
        }

        btnCamera.setOnClickListener{
            startActivity(Intent(this, CameraActivity::class.java))
        }

        btnImgAnalysis.setOnClickListener{
            startActivity(Intent(this, ImageAnalysisActivity::class.java))
        }
    }

    override fun onResume(){
        super.onResume()
        if(!OpenCVLoader.initDebug()){
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mOpenCVCallBack)
        }else{
            mOpenCVCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    private fun initPermission(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            var permissionCheck=0
            permissionCheck+=this.checkSelfPermission(Manifest.permission.CAMERA)
            permissionCheck+=this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
                this.requestPermissions(arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE), CAMERA_REQUEST)
            }else{
                return
            }
        }
    }

}