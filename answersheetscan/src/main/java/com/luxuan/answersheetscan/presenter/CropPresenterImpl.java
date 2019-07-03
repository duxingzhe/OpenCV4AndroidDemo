package com.luxuan.answersheetscan.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Environment;

import com.luxuan.answersheetscan.utils.ThreadUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

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

    @Override

    public void analyzeSrcBitmap(final String filePath){
        ThreadUtils.runOnUIThread(new Runnable(){
            @Override
            public void run(){
                mActivity.onAnalyzeSrcBitmapBegin();
            }
        });

        ThreadUtils.runOnSubThread(new Runnable(){
            @Override
            public void run(){
                final Bitmap bitmap= BitmapFactory.decodeFile(fielPaht);
                int width=bitmap.getWidth();
                int height=bitmap.getHeight();
                final Point[] points=new Point[4];
                points[0]=new Point(width/5, height/5);
                points[1]=new Point(width/5*4, height/5);
                points[2]=new Point(width/5*4, height/5*4);
                points[3]=new Point(width/5, height/5*4);

                ThreadUtils.runOnUIThread(new Runnable(){
                    @Override
                    public void run(){
                        mActivity.onAnalyzeSrcBitmapComplete(bitmap, points);
                    }
                });
            }
        });
    }

    private String createCropFilePath(){
        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new java.util.Date());
        String imageFileName="IMG_"+timeStamp;
        return Environment.getExternalStorageDirectory()+File.separator+imageFileName+".PNG";
    }
}
