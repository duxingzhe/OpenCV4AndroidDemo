//
// Created by Luxuan on 2019/8/16.
//

#include <jni.h>
#include <opencv2/opencv.hpp>
#include <opencv2/core/base.hpp>
#include <opencv2/stitching.hpp>
#include <opencv2/imgcodecs.hpp>

#define BORDER_GRAY_LEVEL 0

#include <android/log.h>
#include <android/bitmap.h>

#define LOG_TAG "StitchingLog-jni"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL, LOG_TAG, __VA_ARGS__)
using namespace cv;
using namespace std;
char filepath1[100]="/storage/emulated/0/panorama_stitched.jpg";

cv::Mat finalMat;

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_luxuan_stitcher_tracker_ImageStitchUtil_stitchImages(JNIEnv *env, jclass type, jobjectArray paths)
{
    jstring jstr;
    jsize len=env->GetArrayLength(paths);
    std::vector<cv::Mat> mats;
    for(int i=0;i<len;i++)
    {
        jstr=(jstring)env->GetObjectArrayElement(paths, i);
        const char *path=(char *)env->GetStringUTFChars(jstr, 0);
        LOGI("path %s", path);
        cv::Mat mat=cv::imread(path);

        mats.push_back(mat);
    }

    LOGI("开始拼接......");
    cv::Stitcher stitcher=cv::Stitcher::createDefault();


    detail::BestOf2NearestMatcher *matcher=new detail::BestOf2NearestMatcher(false, 0.5f);
    stitcher.setFeaturesMatcher(matcher);
    stitcher.setBundleAdjuster(new detail::BundleAdjusterRay());
    stitcher.setSeamFinder(new detail::NoSeamFinder);
    stitcher.setExposureCompensator(new detail::NoExposureCompensator());
    stitcher.setBlender(new detail::FeatherBlender());

    Stitcher::Status state=stitcher.stitch(mats, finalMat);

    LOGI("拼接结果：%d", state);

    jintArray jint_arr=env->NewIntArray(3);
    jint *elems=env->GetIntArrayElements(jint_arr, NULL);
    elems[0]=state;
    elems[1]=finalMat.cols;
    elems[2]=finalMat.rows;

    if(state==cv::Stitcher::OK)
    {
        LOGI("拼接成功：OK");
    }
    else
    {
        LOGI("拼接失败：fail code %d", state);
    }

    env->ReleaseIntArrayElements(jint_arr,elems,0);

    return jint_arr;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_luxuan_stitcher_tracker_ImageStitchUtil_getMat(JNIEnv *env, jclass type, jlong mat)
{
    LOGI("开始获取mat...");
    Mat *res=(Mat *)mat;
    res->create(finalMat.rows, finalMat.cols, finalMat.type());
    memcpy(res->data, finalMat.data, finalMat.rows* finalMat.step);
    LOGI("获取成功");
}

void MatToBitmap(JNIEnv *env, Mat &mat, jobject &bitmap, jboolean needPremultiplayAlpha)
{
    AndroidBitmapInfo info;
    void *pixels=0;
    Mat &src=mat;

    try
    {
        LOGD("nMatToBitmap");
        CV_Assert(AndroidBitmap_getInfo(env, bitmap, &info)>=0);
        LOGD("nMatToBitmap1");

        CV_Assert(info.format==ANDROID_BITMAP_FORMAT_RGBA_8888 ||
                    info.format==ANDROID_BITMAP_FORMAT_RGB_565);
        LOGD("nMatToBitmap2 :%d :%d :%d", src.dims, src.rows, src.cols);

        CV_Assert(src.dims==2&&info.height==(uint32_t)src.rows &&
                    info.width==(uint32_t)src.cols);
        LOGD("nMatToBitmap3");
        CV_Assert(src.type()==CV_8UC1|| src.type()==CV_8UC3||src.type()==CV_8UC4);
        LOGD("nMatToBitmap4");
        CV_Assert(AndroidBitmap_lockPixels(env, bitmap, &pixels)>=0);
        LOGD("nMatToBitmap5");
        CV_Assert(pixels);
        LOGD("nMatToBitmap6");

        if(info.format==ANDROID_BITMAP_FORMAT_RGBA_8888)
        {
            Mat tmp(info.height, info.width, CV_8UC4, pixels);
            if(src.type()==CV_8UC1)
            {
                LOGD("nMatToBitmap: CV_8UC1 ->RGBA_8888");
            }
            else if(src.type()==CV_8UC3)
            {
                LOGD("nMatToBitmap: CV_8UC# -> RGBA_8888");

                cvtColor(src, tmp, COLOR_BGR2RGBA);
            }
            else if(src.type()==CV_8UC4)
            {
                LOGD("nMatToBitmap: CV_8UC4 -> RGBA_8888");
                if(needPremultiplayAlpha)
                {
                    cvtColor(src, tmp, COLOR_BGR2mRGBA);
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
                LOGD("nMatToBitmap: CV_8UC1 -> RGB_565");
                cvtColor(src, tmp, COLOR_GRAY2BGR565);
            }
            else if(src.type()==CV_8UC3)
            {
                LOGD("nMatToBitmap: CV_8UC3 -> RGB_565");
                cvtColor(src, tmp, COLOR_RGB2BGR565);
            }
            else if(src.type()==CV_8UC4)
            {
                LOGD("nMatToBitmap: CV_8UC4 -> RGB_565");
                cvtColor(src, tmp, COLOR_RGBA2BGR565);
            }
        }
        AndroidBitmap_unlockPixels(env, bitmap);
        return;
    }
    catch(const cv::Exception &e)
    {
        AndroidBitmap_unlockPixels(env, bitmap);
        LOGE("nMatToBitmap caught cv::Exception: %s", e.what());
        jclass je=env->FindClass("org/opencv/core/CvException");
        if(!je)
            je=env->FindClass("java/lang/Exception");
        env->ThrowNew(je, e.what());
        return;
    }
    catch(...)
    {
        AndroidBitmap_unlockPixels(env, bitmap);
        LOGE("nMatToBitmap caught unknown exception (...)");
        jclass je=env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception in JNI code {nMatToBitmap}");
        return;
    }
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_luxuan_stitcher_tracker_ImageStitchUtil_getBitmap(JNIEnv *env, jclass type, jobject bitmap)
{
    if(finalMat.dims!=2)
    {
        return -1;
    }

    MatToBitmap(env, finalMat, bitmap, false);

    return 0;
}