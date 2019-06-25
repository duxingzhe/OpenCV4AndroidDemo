//
// Created by Administrator on 2019/6/20.
//

#include <jni.h>

#ifndef _DETECTIONBASEDTRACKER_JNI_H
#define _DETECTIONBASEDTRACKER_JNI_H

#ifdef __cplusplus
extern "C"
{
#endif

JNIEXPORT jlong JNICALL Java_com_luxuan_opencv_DetectionBasedTracker_nativeCreateObject(JNIEnv *, jclass, jstring, jint);
JNIEXPORT void JNICALL Java_com_luxuan_opencv_DetectionBasedTracker_nativeDestroyObject(JNIEnv *, jclass, jlong);
JNIEXPORT void JNICALL Java_com_luxuan_opencv_DetectionBasedTracker_nativeStart(JNIEnv *, jclass, jlong);
JNIEXPORT void JNICALL Java_com_luxuan_opencv_DetectionBasedTracker_nativeStop(JNIEnv *, jclass, jlong);
JNIEXPORT void JNICALL Java_com_luxuan_opencv_DetectionBasedTracker_nativeSetFaceSize(JNIEnv *, jclass, jlong, jint);
JNIEXPORT void JNICALL Java_com_luxuan_opencv_DetectionBasedTracker_nativeDetect(JNIEnv *, jclass, jlong, jlong, jlong);

};
#endif //_DETECTIONBASEDTRACKER_JNI_H
