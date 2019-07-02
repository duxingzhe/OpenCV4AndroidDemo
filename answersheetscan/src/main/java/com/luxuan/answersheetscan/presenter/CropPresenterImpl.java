package com.luxuan.answersheetscan.presenter;

import android.graphics.Bitmap;

import com.luxuan.answersheetscan.utils.ThreadUtils;

import java.io.File;
import java.io.FileOutputStream;

import me.pqpo.smartcropperlib.view.CropImageView;

public class CropPresenterImpl implements CropPresenter {

    private final CropActivity mActivity;

    public CropPresenterImpl(CropActivity cropActivity){
        mActivity=cropActivity;
    }

    @Override
    public void crop(final CropImageView imageView){
        ThreadUtils.runOnUIThread(new Runnable(){
            @Override
            public void run(){
                mActivity.onCropBegin();
            }
        });
        ThreadUtils.runOnSubThread(new Runnable(){
            @Override
            public void run(){
                try{
                    Bitmap bitmap=imageView.crop();
                    if(bitmap==null){
                        throw new RuntimeException("图片裁剪失败");
                    }else{
                        final String cropPath=createCropFilePath();
                        FileOutputStream fos=new FileOutputStream(new File(cropPath));
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.flush();
                        fos.close();
                        ThreadUtils.runOnUIThread(new Runnable(){
                            @Override
                            public void run(){
                                mActivity.onCropComplete(cropPath);
                            }
                        });
                    }
                }catch(Exception e){
                    mActivity.onCropError(e);
                    e.printStackTrace();
                }
            }
        });
    }
}
