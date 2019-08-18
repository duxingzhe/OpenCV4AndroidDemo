//
// Created by Luxuan on 2019/8/18.
//

#include "com_luxuan_stitcher_OpenCVHelper.h"
#include <stdio.h>
#include <stdlib.h>
#include <algorithm>
#include <vector>
#include <iostream>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/opencv.hpp>
#include <opencv/cv.h>
#include <opencv/highgui.h>
#include <android/log.h>
#include <android/bitmap.h>

#define APPNAME "Stitcher"

using namespace cv;
using namespace std;

Mat get_modified_gray(Mat img)
{
    Mat gray;
    cv::cvtColor(img, gray, CV_BGR2GRAY);
    cv::cvtColor(gray,gray, CV_GRAY2BGR);
    return gray;
}

Mat get_modified(Mat img)
{
    vector<Mat> channels;
    Mat img_hist_equalized;
    cvtColor(img, img_hist_equalized, CV_BGR2YCrCb);
    split(img_hist_equalized, channels);
    equalizeHist(channels[0], channels[0]);
    merge(channels, img_hist_equalized);
    cvtColor(img_hist_equalized, img_hist_equalized, CV_YCrCb2BGR);
    return img_hist_equalized;
}

Mat get_modified_bw(Mat img)
{
    Mat bw_output, gray;
    cv::cvtColor(img, gray, CV_BGR2GRAY);
    adaptiveThreshold(gray, bw_output, 255, ADAPTIVE_THRESH_GAUSSIAN_C, CV_THRESH_BINARY, 15,5);
    cv::cvtColor(bw_output, bw_output, CV_GRAY2BGR);
    return bw_output;
}

Mat get_modified_enhance(Mat img)
{
    Mat lab_image;
    cvtColor(img, lab_image, CV_BGR2Lab);
    std::vector<cv::Mat> lab_planes(3);
    cv::split(lab_image, lab_planes);
    cv::Ptr<cv::CLAHE> clahe=cv::createCLAHE();
    clahe->setClipLimit(2);
    cv::Mat dst;
    clahe->apply(lab_planes[0], dst);
    dst.copyTo(lab_planes[0]);
    cv::merge(lab_planes, lab_image);
    cv::Mat image_clahe;
    cv::cvtColor(lab_image, image_clahe, CV_Lab2BGR);
    return image_clahe;
}

Mat get_modified_lighten(Mat img)
{
    cv::Mat brightened_image=img*1.3;
    cv::addWeighted(brightened_image, 0.9, img, 0.1, 3, brightened_image);
    return brightened_image;
}