//
// Created by Luxuan on 2019/8/16.
//

#include <jni.h>
#include <opencv2/oepncv.hpp>
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

        mats.push_back(mat);
    }

    LOGI("开始拼接......");
    cv::Stitcher stitcher=cv::Stiticher::createDefault(false);


    detail::BestOf2NearestMatcher *matcher=nwe detail::BeestOf2NearestMatcher(false, 0.5f);
    stitcher.setFeaturesMatcher(matcher);
    stitcher.setBundleAdjust(new detail::BundleAdjustRay());
    stitcher.setSeamFinder(new detail::NoSeamFinder);
    stitcher.setExposureCompensator(new detail::NoExposureCompensator());
    stitcher.setBlender(new detail::FeatherBlur());

    Stitcher:: status state=stitcher.stitch(mats, finalMat);

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
