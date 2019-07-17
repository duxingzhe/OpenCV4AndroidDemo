package com.luxuan.androidimageanalysis

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface

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

}