package com.luxuan.androidimageanalysis

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

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

    private fun staticLoadCVLibraries(){
        val load =OpenCVLoader.initDebug()
        if(load){
            mOpenCVCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }else{
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mOpenCVCallBack)
        }
    }

    private fun selectImg() {
        val intent=Intent()
        intent.type="image/*"
        intent.action=Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "选择图像..."),PICK_IMAGE_REQUEST)
    }

    private fun hasAllPermissionGranted(grantResults: IntArray): Boolean{
        return !grantResults.contains(PackageManager.PERMISSION_DENIED)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
        when(requestCode){
            CAMERA_REQUEST -> {
                if(hasAllPermissionGranted(grantResults)){
                    Log.d("111", "onRequestPermissionsResult: OK")
                }else{
                    Toast.makeText(this, "请打开定位设置", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==PICK_IMAGE_REQUEST && resultCode== Activity.RESULT_OK && data!=null && data.data!=null) {
            val uri=data.data
            try{
                Log.d("image-tag", "start to decode selected image now...")
                val input=contentResolver.openInputStream(uri!!)
                val options= BitmapFactory.Options()
                options.inJustDecodeBounds=true
                BitmapFactory.decodeStream(input, null, options)
                val raw_width=options.outWidth
                val raw_height=options.outHeight
                val max=Math.max(raw_width, raw_height)
                var newWidth=raw_width
                var newHeight=raw_height
                var inSampleSize=1
                val max_size=1024.0
                if(max>max_size){
                    newWidth=raw_width/2
                    newHeight=raw_height/2
                    while(newWidth/inSampleSize>max_size||newHeight/inSampleSize>max_size){
                        inSampleSize*=2
                    }
                }

                options.inSampleSize=inSampleSize
                options.inJustDecodeBounds=false
                options.inPreferredConfig=Bitmap.Config.ARGB_8888
                selectbp=BitmapFactory.decodeStream(contentResolver.openInputStream(uri), null, options)

                sourcePic.setImageBitmap(selectbp)
            }catch(e: Exception){
                e.printStackTrace()
            }

        }
    }

    private fun convertGray(){
        val src=Mat()
        val temp=Mat()
        val dst= Mat()
        val newBitmap=selectbp?.copy(Bitmap.Config.ARGB_8888, true)
        Utils.bitmapToMat(newBitmap, src)
        Imgproc.cvtColor(src, temp, Imgproc.COLOR_BGRA2BGR)
        Log.i("CV", "image type:" + (temp.type()== CvType.CV_8UC3))
        Imgproc.cvtColor(temp, dst, Imgproc.COLOR_BGR2GRAY)
        Utils.matToBitmap(dst, newBitmap)
        resultPic.setImageBitmap(newBitmap)
    }
}