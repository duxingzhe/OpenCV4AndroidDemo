package com.luxuan.opencv4demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class BitmapUtils {

    public static Bitmap changeChannels(Bitmap bitmap){
        int width=bitmap.getWidth();
        int height=bitmap.getHeight();
        int[] pixels=new int[width*height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width,height);

        int index=0;
        int channel1;
        int channel2;
        int channel3;
        int channel4;

        for(int row=0;row<height;row++){
            for(int col=0;col<width;col++){
                int pixel=pixels[index];
                channel1=(pixel>>24)&0xff;
                channel2=(pixel>>16)&0xff;
                channel3=(pixel>>8)&0xff;
                channel4=pixel&0xff;
                pixel=((channel1&0xff)<<24|(channel4&0xff)<<16|(channel3&0xff)<<8|(channel2&0xff));
                pixels[index]=pixel;
                index++;
            }
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width,height);
        return bitmap;
    }
}
