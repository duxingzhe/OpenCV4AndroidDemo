//
// Created by Luxuan on 2019/8/18.
//

#include <jni.h>

#ifndef OPENCV4ANDROIDDEMO_LX_COM_LUXUAN_STITCHER_OPENCVHELPER_H
#define OPENCV4ANDROIDDEMO_LX_COM_LUXUAN_STITCHER_OPENCVHELPER_H

#ifdef __cplusplus
extern "C"
{
#endif
JNIEXPORT jintArray JNICALL Java_com_luxuan_opencv4android_OpenCVHelper_gray
(JNIEnv *, jclass, jintArray, jint, jint);

JNIEXPORT jintArray JNICALL Java_com_luxuan_opencv4android_OpenCVHelper_detectFeatures
(JNIEnv *, jclass, jintArray, jint, jint);

#ifdef __cplusplus
}
#endif

#endif //OPENCV4ANDROIDDEMO_LX_COM_LUXUAN_STITCHER_OPENCVHELPER_H
