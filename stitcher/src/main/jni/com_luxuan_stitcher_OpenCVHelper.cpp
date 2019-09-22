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
    Size resize=(w>h)?Size(RESIZE_HEIGHT, RESIZE_WIDTH): Size(RESIZE_WIDTH, RESIZE_HEIGHT);
    resize(inputImage, outputImage, resize);
    return outputImage;
}

int getDistance(Point a, Point b)
{
    int c=sqrt(pow(b.x-a.x, 2));

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