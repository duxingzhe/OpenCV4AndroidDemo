package com.luxuan.stitcher.stitcher;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luxuan.stitcher.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraScreenActivity extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPictureCallback;
    private boolean previewing=false;
    private LayoutInflater controlInflater=null;
    private ImageView setting1, single, setting2, single_batch_page;
    private Button camera_click, batch_click, front_camera_btn, batch;

    private Uri fileUri, outputFileUri;
    private TextView textView;
    protected String imageFilePath;
    private LinearLayout layout1, layout2, layout3;
    private LinearLayout camera_preview;
    public static int count=0;

    private ImageView pictureImageView;
    private View pictureCountBackgroundView;
    private TextView pictureCountTextView;
    private Camera.Parameters parameters;
    private boolean isFlashOn;
    private static String formattedDate;
    private boolean cameraFront=false;

    private LinearLayout setting_before, setting_after,camera_single, camera_batch, batch_before, batch_after, single_before, single_after, flashOn, flashOff, settingTest;
    private TextView modeTextView;
    private ImageView settingbefore, settingafter, single_mode, single_mode_again, batch_mode_before, batch_mode_after, settingtest;
    private Button camerasingle, cameraBatch;
    private LinearLayout extra_settings;
    private ImageView flash_on, flash_off;
    private Context mContext;
    private LinearLayout picturecount;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_camera_screen);
        mContext=this;

        extra_settings=(LinearLayout)findViewById(R.id.extra_settings);
        camera_preview=(LinearLayout)findViewById(R.id.camera_preview);

        flashOn=(LinearLayout)findViewById(R.id.flashon);
        flashOff=(LinearLayout)findViewById(R.id.flashoff);

        setting_before=(LinearLayout)findViewById(R.id.setting_bef);
        setting_after=(LinearLayout)findViewById(R.id.setting_aft);
        camera_single=(LinearLayout)findViewById(R.id.camera_single);
        camera_batch=(LinearLayout)findViewById(R.id.camera_batch);
        batch_before=(LinearLayout)findViewById(R.id.batch_bef);
        batch_after=(LinearLayout)findViewById(R.id.batch_aft);
        single_before=(LinearLayout)findViewById(R.id.single_bef);
        single_after=(LinearLayout)findViewById(R.id.single_afr);

        flash_off=(ImageView)findViewById(R.id.flash_off);
        flash_on=(ImageView)findViewById(R.id.flash_on);

        modeTextView=(TextView)findViewById(R.id.textView);
        picturecount=(LinearLayout)findViewById(R.id.picturecount);

        pictureImageView=(ImageView)findViewById(R.id.pictureIV);
        pictureCountBackgroundView=(View)findViewById(R.id.pictureCountBgV);
        pictureCountTextView=(TextView)findViewById(R.id.pictureCountTV);

        settingTest=(LinearLayout)findViewById(R.id.settingTest);
        settingtest=(ImageView)findViewById(R.id.settingtest);

        initialize();
    }

    private int findFrontFacingCamera(){
        int cameraId=-1;
        int numberOfCameras=Camera.getNumberOfCameras();
        for(int i=0;i<numberOfCameras;i++){
            Camera.CameraInfo info=new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if(info.facing==Camera.CameraInfo.CAMERA_FACING_FRONT){
                cameraId=i;
                cameraFront=true;
                break;
            }
        }

        return cameraId;
    }

    private int findBackFacingCamera(){
        int cameraId=-1;
        int numberOfCameras=Camera.getNumberOfCameras();
        for(int i=0;i<numberOfCameras;i++){
            Camera.CameraInfo info=new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if(info.facing==Camera.CameraInfo.CAMERA_FACING_BACK){
                cameraId=i;
                cameraFront=false;
                break;
            }
        }

        return cameraId;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!hasCamera(mContext)){
            Toast.makeText(mContext,
                    "Sorry, your phone does not have a camera!",
                    Toast.LENGTH_LONG).show();
            finish();
        }
        if(mCamera==null){
            if(findFrontFacingCamera()<0){
                Toast.makeText(this, "No front facing camera found.",
                        Toast.LENGTH_LONG).show();
                front_camera_btn.setVisibility(View.GONE);
            }
            mCamera=Camera.open(findBackFacingCamera());
            mPictureCallback=getPictureCallback();
            mPreview.refreshCamera(mCamera);
        }
    }

    public void initialize(){
        int PERMISSION_ALL=1;
        String[] PERMISSIONS={Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_SMS, Manifest.permission.CAMERA};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        camera_preview=(LinearLayout)findViewById(R.id.camera_preview);
        mPreview=new CameraPreview(mContext, mCamera);
        camera_preview.addView(mPreview);

        extra_settings=(LinearLayout)findViewById(R.id.extra_settings);
        picturecount=(LinearLayout)findViewById(R.id.picturecount);

        setting_before=(LinearLayout)findViewById(R.id.setting_bef);
        setting_before.setOnClickListener(setting_beforeListener);

        setting_after=(LinearLayout)findViewById(R.id.setting_aft);
        setting_after.setOnClickListener(setting_afterListener);

        camera_single=(LinearLayout)findViewById(R.id.camera_single);

        camera_batch=(LinearLayout)findViewById(R.id.camera_batch);
        camera_batch.setOnClickListener(camera_batchListener);

        batch_before=(LinearLayout)findViewById(R.id.batch_bef);
        batch_before.setOnClickListener(batch_beforeListener);

        batch_after=(LinearLayout)findViewById(R.id.batch_aft);
        batch_after.setOnClickListener(batch_afterListener);

        single_before=(LinearLayout)findViewById(R.id.single_bef);
        single_before.setOnClickListener(single_beforeListener);

        single_after=(LinearLayout)findViewById(R.id.single_afr);
        single_after.setOnClickListener(single_afterListener);

        settingbefore=(ImageView)findViewById(R.id.settingbefore);
        settingafter=(ImageView)findViewById(R.id.settingafter);

        flashOn=(LinearLayout)findViewById(R.id.flashon);
        flashOn.setOnClickListener(flashOnListener);

        flashOff=(LinearLayout)findViewById(R.id.flashoff);
        flashOff.setOnClickListener(flashOffListener);

        textView=(TextView)findViewById(R.id.textView);
        picturecount=(LinearLayout)findViewById(R.id.picturecount);

        pictureImageView=(ImageView)findViewById(R.id.pictureIV);
        pictureImageView.setOnClickListener(showPictureListener);

        settingTest=(LinearLayout)findViewById(R.id.settingTest);
        settingTest.setOnClickListener(testListener);

        settingtest=(ImageView)findViewById(R.id.settingtest);

        camerasingle=(Button)findViewById(R.id.camerasingle);
        camerasingle.setOnClickListener(captureListener);

        cameraBatch=(Button)findViewById(R.id.cameraBatch);
        cameraBatch.setOnClickListener(batchCameraListener);
    }

    private View.OnClickListener setting_beforeListener=new View.OnClickListener(){
        @Override
        public void onClick(View view){
            setting_before.setVisibility(View.GONE);
            setting_after.setVisibility(View.VISIBLE);
            extra_settings.setVisibility(View.VISIBLE);
            settingbefore.setVisibility(View.GONE);
            settingafter.setVisibility(View.VISIBLE);
        }
    };

    private View.OnClickListener setting_afterListener=new View.OnClickListener(){
        @Override
        public void onClick(View view){
            setting_before.setVisibility(View.VISIBLE);
            setting_after.setVisibility(View.GONE);
            extra_settings.setVisibility(View.GONE);
            settingbefore.setVisibility(View.VISIBLE);
            settingafter.setVisibility(View.GONE);
        }
    };

    private View.OnClickListener batch_beforeListener=new View.OnClickListener(){
        @Override
        public void onClick(View view){
            batch_before.setVisibility(View.GONE);
            batch_after.setVisibility(View.VISIBLE);
            camera_single.setVisibility(View.GONE);
            camera_batch.setVisibility(View.VISIBLE);
            picturecount.setVisibility(View.VISIBLE);
            textView.setText("Batch Mode");

            settingTest.setVisibility(View.GONE);
            settingtest.setVisibility(View.GONE);
        }
    };

    private View.OnClickListener batch_afterListener=new View.OnClickListener(){
        @Override
        public void onClick(View view){
            batch_before.setVisibility(View.VISIBLE);
            batch_after.setVisibility(View.GONE);
            camera_single.setVisibility(View.VISIBLE);
            camera_batch.setVisibility(View.GONE);
            picturecount.setVisibility(View.GONE);
            textView.setText("Single Mode");

            settingTest.setVisibility(View.VISIBLE);
            settingtest.setVisibility(View.VISIBLE);
        }
    };

    private View.OnClickListener single_beforeListener=new View.OnClickListener(){
        @Override
        public void onClick(View view){
            if(textView.equals("Batch Mode")){
                Log.i("Batch Mode", "Mode");
            }else{
                batch_before.setVisibility(View.VISIBLE);
                batch_after.setVisibility(View.GONE);
                camera_single.setVisibility(View.VISIBLE);
                camera_batch.setVisibility(View.GONE);
                picturecount.setVisibility(View.GONE);
                textView.setText("Single Mode");
            }
        }
    };

    private View.OnClickListener showPictureListener=new View.OnClickListener(){

        @Override
        public void onClick(View view){
            Log.i("test count", "picture count");
            Intent intent=new Intent(CameraScreenActivity.this, GridViewActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener single_afterListener=new View.OnClickListener(){

        @Override
        public void onClick(View view){
            Toast.makeText(CameraScreenActivity.this, "Single Mode", Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener camera_batchListener=new View.OnClickListener(){

        @Override
        public void onClick(View view){
            Toast.makeText(CameraScreenActivity.this, "Batch Mode", Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener flashOnListener=new View.OnClickListener(){
        @Override
        public void onClick(View view){
            turnOnFlash();
        }
    };

    private View.OnClickListener flashOffListener=new View.OnClickListener(){
        @Override
        public void onClick(View view){
            turnOffFlash();
        }
    };

    private View.OnClickListener testListener=new View.OnClickListener(){
        @Override
        public void onClick(View view){
            Log.i("test", "test");
        }
    };

    private View.OnClickListener batchCameraListener=new View.OnClickListener(){
        @Override
        public void onClick(View view){
            int camerasNumber=Camera.getNumberOfCameras();
            if(camerasNumber>1){
                count++;
                mCamera.startPreview();
                mCamera.takePicture(null, null, photoCallback_batch);
                String s=String.valueOf(count);
                pictureCountTextView.setText(s);
                Log.i("test count", String.valueOf(count));

                SharedPreferences pref=getApplicationContext().getSharedPreferences("CameraPref", MODE_PRIVATE);
                SharedPreferences.Editor editor=pref.edit();
                editor.putString("count", String.valueOf(count));
                editor.clear();
                editor.commit();
            }else{
                Toast.makeText(mContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG).show();
            }
        }
    };

    public void chooseCamera(){
        if(cameraFront){
            int cameraId=findBackFacingCamera();
            if(cameraId>=0){
                mCamera=Camera.open(cameraId);
                mPictureCallback=getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }else{
            int cameraId=findFrontFacingCamera();
            if(cameraId>=0) {
                mCamera=Camera.open(cameraId);
                mPictureCallback=getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        releaseCamera();
    }

    private boolean hasCamera(Context context){
        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        }else{
            return false;
        }
    }

    private Camera.PictureCallback getPictureCallback(){
        Camera.PictureCallback pictureCallback=new Camera.PictureCallback(){

            @Override
            public void onPictureTaken(byte[] data, Camera camera){
                File pictureFile=getOutputMediaFile();

                if(pictureFile==null){
                    return;
                }

                try{
                    FileOutputStream fos=new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();

                    BitmapFactory.Options options=new BitmapFactory.Options();
                    options.inPreferredConfig= Bitmap.Config.ARGB_8888;
                    Bitmap bitmap=BitmapFactory.decodeFile(String.valueOf(pictureFile), options);
                    if(bitmap!=null){
                        Uri uri=Utils.getUri(CameraScreenActivity.this, bitmap);
                        bitmap.recycle();

                        Log.i("test uri", uri.toString());
                        Intent intent=new Intent(CameraScreenActivity.this, PolygonViewScreenActivity.class);
                        intent.putExtra("imageTest", uri);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(CameraScreenActivity.this,"Failed to Capture the picture. kindly Try Again:", Toast.LENGTH_LONG).show();
                    }
                }catch(FileNotFoundException e){

                }catch(IOException e){

                }

                mPreview.refreshCamera(mCamera);
            }
        };

        return pictureCallback;
    }

    private Camera.PictureCallback photoCallback_batch=new Camera.PictureCallback(){

        @Override
        public void onPictureTaken(byte[] data, Camera camera){

            FileOutputStream outputStream=null;
            try{
                File mediaStorageDir=new File("/sdcard/", "Batch");
                if(!mediaStorageDir.exists()){
                    if(!mediaStorageDir.mkdirs()){
                        Log.d("image upload", "Oops! Failed create YOUR DIRECTORY NAME directory");
                    }
                }

                outputFileUri=Uri.fromFile(mediaStorageDir);

                String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

                int i=(int)(System.currentTimeMillis()/1000);
                System.out.println("Integer: "+i);
                String name=String.valueOf(i);
                System.out.println("Time: "+name);

                File mediaFile=new File(mediaStorageDir.getPath()+File.separator+"IMG_"+name+".jpg");
                System.out.println("mdeiaFile: "+mediaFile);
                Log.i("current imageFilePath", imageFilePath);

                outputStream=new FileOutputStream(mediaFile);
                outputStream.write(data);
                outputStream.close();

                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inPreferredConfig=Bitmap.Config.ARGB_8888;
                Bitmap bitmap=BitmapFactory.decodeFile(String.valueOf(mediaFile), options);

            }catch(FileNotFoundException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }finally{

            }

            mCamera.startPreview();
        }
    };

    private void turnOnFlash(){
        Camera.Parameters parameters=mCamera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

    private void turnOffFlash(){
        Camera.Parameters parameters=mCamera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

    private View.OnClickListener captureListener=new View.OnClickListener(){

        @Override
        public void onClick(View view){
            mCamera.takePicture(null, null, mPictureCallback);
        }
    };

    private static File getOutputMediaFile(){
        File mediaStorageDir=new File("/sdcard/", "Single");

        if(!mediaStorageDir.exists()){
            if(mediaStorageDir.mkdirs()){
                return null;
            }
        }

        int i=(int)(System.currentTimeMillis()/1000);
        System.out.println("Integer: "+i);
        String name=String.valueOf(i);
        System.out.println("Time: "+name);

        File mediaFile=new File(mediaStorageDir.getPath()+File.separator+"IMG_"+name+".jpg");
        System.out.println("mediaFile: "+ mediaFile);

        return mediaFile;
    }

    private void releaseCamera(){
        if(mCamera!=null){
            mCamera.release();
            mCamera=null;
        }
    }

    public static boolean hasPermissions(Context context, String... permissions){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && context!=null && permissions!=null){
            for(String permission: permissions){
                if(ActivityCompat.checkSelfPermission(context, permission)!=PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }
}
