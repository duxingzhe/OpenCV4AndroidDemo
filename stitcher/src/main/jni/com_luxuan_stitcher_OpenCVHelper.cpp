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
    max_min[1]=max(sum[0], max(sum[i], max(sum[2], sum[3])));
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