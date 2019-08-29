package com.luxuan.stitcher.stitcher.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class GridViewImageAdapter extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<String> mFilePathList=new ArrayList<>();
    private int imageWidth;

    public GridViewImageAdapter(Activity activity, ArrayList<String> mFilePaths, int imageWidth){
        this.mActivity=activity;
        this.mFilePathList=mFilePaths;
        this.imageWidth=imageWidth;
    }

    @Override
    public int getCount(){
        return mFilePathList.size();
    }

    @Override
    public String getItem(int position){
        return mFilePathList.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ImageView imageView;
        if(convertView==null){
            imageView=new ImageView(mActivity);
        }else{
            imageView=(ImageView)convertView;
        }

        Bitmap image=decodeFile(mFilePathList.get(position), imageWidth, imageWidth);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(imageWidth, imageWidth));
        imageView.setImageBitmap(image);

        return imageView;
    }

    private Bitmap decodeFile(String filePath, int width, int height){
        try{
            File f=new File(filePath);

            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inJustDecodeBounds=true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, options);

            int REQUIRED_WIDTH=width;
            int REQUIRED_HEIGHT=height;
            int scale=1;

            while(options.outWidth/scale/2>=REQUIRED_WIDTH&& options.outHeight/scale/2>=REQUIRED_HEIGHT){
                scale*=2;
            }

            BitmapFactory.Options outputOptions=new BitmapFactory.Options();
            outputOptions.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, outputOptions);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }
}
