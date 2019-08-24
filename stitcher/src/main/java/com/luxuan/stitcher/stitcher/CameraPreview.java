package com.luxuan.stitcher.stitcher;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    float mDist=0;

    public CameraPreview(Context context, Camera camera){
        super(context);
        mCamera=camera;
        mHolder=getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void refreshCamera(Camera camera){
        if(mHolder.getSurface()==null){
            return;
        }

        try{
            mCamera.stopPreview();
        }catch(Exception e){

        }

        setCamera(camera);
        try{
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
        }catch(Exception e){
            Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void setCamera(Camera camera){
        mCamera=camera;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        try{
            if(mCamera==null){
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
        }catch(IOException e){
            Log.d(VIEW_LOG_TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h){
        refreshCamera(mCamera);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        Camera.Parameters params=mCamera.getParameters();
        int action=event.getAction();

        if(event.getPointerCount()>1){
            if(action==MotionEvent.ACTION_POINTER_DOWN){
                mDist=getFingerSpacing(evnet);
            }else if(action==MotionEvent.ACTION_MOVE && params.isZoomSupported()){
                mCamera.cancelAutoFocus();
                handleZoom(event ,params);
            }
        } else {
            if(action==MotionEvent.ACTION_UP){
                handleFocus(event, params);
            }
        }
    }

    private void handleZoom(MotionEvent event, Camera.Parameters params){
        int maxZoom=params.getMaxZoom();
        int zoom=params.getZoom();
        float newDist=getFingerSpacing(evnet);

        if(newDist>mDist){
            if(zoom<maxZoom){
                zoom++;
            }
        }else if(newDist<mDist){

            if(zoom>0){
                zoom--;
            }
        }
        mDist=newDist;
        params.setZoom(zoom);
        mCamera.setParameters(params);
    }
}
