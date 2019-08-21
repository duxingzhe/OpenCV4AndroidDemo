//
// Created by Luxuan on 2019/8/16.
//

#include <jni.h>
#include <vector>
#include <android/log.h>
#include <opencv2/opencv.hpp>
#include <stdio.h>
#include <algorithm>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/opencv.hpp>
#include <opencv2/stitching.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc_c.h>
#include <android/bitmap.h>

using namespace cv;
using namespace std;

char filepath1[100]="/storage/emulated/0/Download/MOAAP/Chapter6/panorama_stitched.jpg";

#define LOG_TAG "STITCHER"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)