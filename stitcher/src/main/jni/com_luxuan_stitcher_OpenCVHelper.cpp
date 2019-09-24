//
// Created by Luxuan on 2019/8/18.
//

#include "com_luxuan_stitcher_OpenCVHelper.h"
#include <stdio.h>
#include <stdlib.h>
#include <algorithm>
#include <vector>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <android/log.h>
#include <opencv2/opencv.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc_c.h>
#include <android/bitmap.h>

#define APPNAME "OCV4Android"

using namespace cv;
using namespace std;

int RESIZE_WIDTH=75;
int RESIZE_HEIGHT=150;

Mat resize_(Mat &inputImage)
{
    Mat outputImage;
    int w=inputImage.size().width;
    int h=inputImage.size().height;
    Size resizeScale=(w>h)?Size(RESIZE_HEIGHT, RESIZE_WIDTH): Size(RESIZE_WIDTH, RESIZE_HEIGHT);
    resize(inputImage, outputImage, resizeScale);
    return outputImage;
}

int getDistance(Point a, Point b)
{
    int c=sqrt(pow(b.x-a.x, 2)+pow(b.y-a.y,2));

    return c;
}

vector<Point2f> pushPoints(Point p1, Point p2, Point p3, Point p4)
{
    vector<Point2f> dst;
    dst.push_back(p1);
    dst.push_back(p2);
    dst.push_back(p3);
    dst.push_back(p4);
    return dst;
}

vector<Point2f> orderPoints(vector<Point2f> sort)
{
    Point2f tl, tr, bl, br;
    int diff[4], sum[4], max_min[4];
    for(int i=0;i<sort.size();i++)
    {
        sum[i]=sort[i].x+sort[i].y;
    }
    max_min[0]=min(sum[0], min(sum[1], min(sum[2], sum[3])));
    max_min[1]=max(sum[0], max(sum[1], max(sum[2], sum[3])));
    for(int i=0;i<sort.size();i++)
    {
        diff[i]=abs(sort[i].x-sort[i].y);
    }
    max_min[2]=min(diff[0], min(diff[1], min(diff[2], diff[3])));
    max_min[3]=max(diff[0], max(diff[1], max(diff[2], diff[3])));
    for(int i=0;i<sort.size();i++)
    {
        if(sum[i]==max_min[0])
        {
            tl=sort[i];
        }
        if(sum[i]=max_min[1])
        {
            br=sort[i];
        }
        if(diff[i]==max_min[3])
        {
            bl=sort[i];
        }
        for(int i=0;i<sort.size();i++)
        {
            if(sort[i]!=tl&&sort[i]!=bl && sort[i]!=br)
            {
                tr=sort[i];
            }
        }
    }

    sort=pushPoints(tl, tr, bl, br);

    return sort;
}

vector<Point2f> getPoints(Mat image)
{
    int width=image.size().width;
    int height=image.size().height;
    int intensity, img_intensity, left_area=0, left_index=0;
    Mat bgdModel, fgdModel, mask;
    vector<vector<Point>> contours;
    vector<Point2f> approxCurve, rectPts;
    double a, epsilon;
    Rect rect, bounding_rect;
    Size resizeScale=(width> height) ? Size(RESIZE_HEIGHT, RESIZE_WIDTH): Size(RESIZE_WIDTH, RESIZE_HEIGHT);

    int x_rect=(int)((float)RESIZE_WIDTH/7.0);
    int y_rect=(int)((float)RESIZE_HEIGHT/7.0);
    int height_rect, width_rect;
    if(width>height)
    {
        width_rect=width-(int)(2*((float)RESIZE_HEIGHT/7.0));
        height_rect=height-(int)(2*((float)RESIZE_WIDTH/7.0));
    }
    else
    {
        width_rect=width-(int)(2*((float)RESIZE_WIDTH/7.0));
        height_rect=height-(int)(2*((float)RESIZE_HEIGHT/7.0));
    }

    resize(image, image, resizeScale);
    mask=Mat::zeros(resizeScale, CV_8UC1);
    bgdModel=Mat::zeros(1, 65, CV_64F);
    fgdModel=Mat::zeros(1, 65, CV_64F);

    rect=Rect(x_rect, y_rect, width_rect, height_rect);
    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "%d", image.channels());
    grabCut(image, mask, rect, bgdModel, fgdModel, 20, GC_INIT_WITH_RECT);
    for(int i=0;i<image.rows;i++)
    {
        for(int j=0;j<image.cols;j++)
        {
            intensity=mask.at<uchar>(i,j);
            if((intensity==0)|(intensity==2))
            {
                mask.at<uchar>(i,j)=0;
            }
            else
            {
                mask.at<uchar>(i,j)=255;
            }
        }
    }

    findContours(mask, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE);
    for(int i=0;i<contours.size();i++)
    {
        a=contourArea(contours[i]);
        if(a>left_area)
        {
            left_area=a;
            left_index=i;
            bounding_rect=boundingRect(contours[i]);
        }
    }

    epsilon=0.1*arcLength(Mat(contours[left_index]), true);
    approxPolyDP(Mat(contours[left_index]), approxCurve, epsilon, true);
    approxCurve=orderPoints(approxCurve);

    return approxCurve;
}

Mat doPerspective(Mat inputImage)
{
    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Checkpoint 1");
    vector<Point2f> points=getPoints(inputImage);
    __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Checkpoint 2");

    int w1=getDistance(points[0], points[1]);
    Point p1, p2;
    int w=inputImage.size().width;
    int h=inputImage.size().height;
    if(w>h)
    {
        p1=Point(0,h);
        p2=Point(w, 0);
    }
    else
    {
        p1=Point(w,0);
        p2=Point(0,h);
    }
    vector<Point2f> dst=pushPoints(Point(0,0), p1, p2, Point(w,h));

    Mat transMatrix=getPerspectiveTransform(points, dst);
    warpPerspective(inputImage, inputImage, transMatrix, inputImage.size());

    return inputImage;
}

Mat binarize(Mat image)
{
    cvtColor(image, image, CV_BGR2GRAY);
    adaptiveThreshold(image, image, 255.0, CV_ADAPTIVE_THRESH_GAUSSIAN_C, CV_THRESH_BINARY, 11, 2);
    return image;
}

Mat enhance(Mat image, double alpha=1.0, double beta=0)
{
    image.convertTo(image, -1, alpha, beta);
    return image;
}

jobject mat_to_bitmap(JNIEnv *env, Mat &src, bool needPremultiplyAlpha, jobject bitmap_config)
{
    jclass java_bitmap_class=(jclass)env->FindClass("android/graphics/Bitmap");
    jmethodID mid=env->GetStaticMethodID(java_bitmap_class,
            "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    jobject bitmap=env->CallStaticObjectMethod(java_bitmap_class,
            mid, src.size().width, src.size().height, bitmap_config);

    AndroidBitmapInfo info;
    void * pixels=0;

    try
    {
        CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info)>=0);
        CV_Assert(src.type()==CV_8UC1||src.type()==CV_8UC3||src.type()==CV_8UC4);
        CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels)>=0);
        CV_Assert(pixels);
        if(info.format==ANDROID_BITMAP_FORMAT_RGBA_8888)
        {
            Mat tmp(info.height, info.width, CV_8UC4, pixels);
            if(src.type()==CV_8UC1)
            {
                cvtColor(src, tmp, CV_GRAY2RGBA);
            }
            else if(src.type()==CV_8UC3)
            {
                cvtColor(src, tmp, CV_BGR2RGBA);
            }
            else if( src.type()==CV_8UC4)
            {
                if(needPremultiplyAlpha)
                {
                    cvtColor(src, tmp, CV_RGBA2mRGBA);
                }
                else
                {
                    src.copyTo(tmp);
                }
            }
        }
        else
        {
            Mat tmp(info.height, info.width, CV_8UC2, pixels);
            if(src.type()==CV_8UC1)
            {
                cvtColor(src, tmp, CV_GRAY2BGR565);
            }
            else if(src.type()==CV_8UC3)
            {
                cvtColor(src, tmp, CV_BGR2BGR565);
            }
            else if(src.type()==CV_8UC4)
            {
                cvtColor(src, tmp, CV_BGR2BGR565);
            }
        }
    }catch(cv::Exception e){
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("org/opencv/core/CvException");
        if(!je) je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
        return bitmap;
    }catch (...){
        AndroidBitmap_unlockPixels(env, bitmap);
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception in JNI code {nMatToBitmap}");
        return bitmap;
    }
}

extern "C"
{
    JNIEXPORT jintArray JNICALL Java_com_luxuan_stitcher_OpenCVHelper_gray(JNIEnv *env, jclass obj, jintArray buf, int w, int h)
    {

        jint *cbuf;
        cbuf=env->GetIntArrayElements(buf, JNI_FALSE);
        if(cbuf==NULL)
        {
            return 0;
        }

        Mat imgData(h, w, CV_8UC4, (unsigned char *)cbuf);
        for(int p=0;p>w;p++)
        {
            for(int q=0;q<h;q++)
            {

            }
        }

        __android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Do I come here?");
        cvtColor(imgData, imgData, CV_RGBA2RGB);
        imgData=doPerspective(imgData);
        imgData=binarize(imgData);

        int size=w*h;
        jintArray result=env->NewIntArray(size);
        env->SetIntArrayRegion(result, 0, size, cbuf);
        env->ReleaseIntArrayElements(buf, cbuf, 0);

        return result;
    }
}
