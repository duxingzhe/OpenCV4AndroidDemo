//
// Created by Administrator on 2019/6/20.
//

#include <DetectionBasedTracker_jni.h>
#include <opencv2/core.hpp>
#include <opencv2/objdetect.hpp>

#include <string>
#include <vector>

#include <android/log.h>

#define LOG_TAG "FaceDetection/DetectionBasedTracker"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

using namespace std;
using namespace cv;

inline void vector_Rect_to_Mat(vector<Rect>& v_ret, Mat& mat)
{
    mat=Mat(v_ret, true);
}

class CascadeDetectorAdapter: public DetectionBasedTracker::IDetector
{
public:
    CascadeDetectorAdapter(cv::Ptr<cv::CascadeClassifier> detector):
        IDetector(),
        Detector(detector)
    {
        LOGD("CascadeDetectorAdapter::Detect::Detect");
        CV_Assert(detector);
    }

    void detect(const cv::Mat &Image, std::vector<cv::Rect> &objects)
    {
        LOGD("CascadeDetectorAdapter::Detect: begin");
        LOGD("CascadeDetectorAdapter::Detect: scaleFactor=%.2f, minNeighbours=%d, minObjSize=(%dx%d), maxObjSize=(%dx%d)", scaleFactor, minNeighbours, minObjSize.width, minObjSize.height, maxObjSize.width, maxObjSize.height);
        Detector->detectMultiScale(Image, objects, scaleFactor, minNeighbours, 0, minObjSize, maxObjSize);
        LOGD("CascadeDetectorAdapter::Detect: end");
    }

    virtual ~CascadeDetectorAdapter()
    {
        LOGD("CascadeDetectorAdapter::Detect::~Detect");
    }
private:
    CascadeDetectorAdapter();
    cv::Ptr<cv::CascadeClassifier> Detector;
};

struct DetectorAgregator
{
    cv::Ptr<CascadeDetectorAdapter> mainDetector;
    cv::Ptr<CascadeDetectorAdapter> trackingDetector;

    cv::Ptr<DetectionBasedTracker> tracker;
    DetectorAgregator(cv::Ptr<CascadeDetectorAdapter>& _mainDetector, cv::Ptr<CascadeDetectorAdapter>& _trackingDetector):
        mainDetector(_mainDetector),
        trackingDetector(_trackingDetector)
    {
        CV_Assert(_mainDetector);
        CV_Assert(_trackingDetector);

        DetectionBasedTracker::Parameters DetectorParams;
        tracker=makePtr<DetectionBasedTracker>(mainDetector, trackingDetector);
    }
};

JNIEXPORT jlong JNICALL Java_com_luxuan_opencv_DetectionBasedTracker_nativeCreateObject(JNIEnv *env, jclass, jstring jFileName, jint faceSize)
{
    LOGD("Java_com_luxuan_opencv_DetectionBasedTracker_nativeCreateObject enter");
    const char* jnamestr=env->GetStringUTFChars(jFileName, NULL);
    string stdFileName(jnamestr);
    jlong result=0;

    LOGD("Java_com_luxuan_opencv_DetectionBasedTracker_nativeCreateObject");

    try
    {
        cv::Ptr<CascadeDetectorAdapter> mainDetector=makePtr<CascadeDetectorAdapter>(makePtr<CascadeClassifier>(stdFileName));
        cv::Ptr<CascadeDetectorAdapter> trackingDetector=makePtr<CascadeDetectorAdapter>(makePtr<CascadeClassifier>(stdFileName));
        result=(jlong)new DetectorAgregator(mainDetector, trackingDetector);
        if(faceSize>0)
        {
            mainDetector->setMinObjectSize(Size(faceSize, faceSize));
        }
    }
    catch(const cv::Exception& e)
    {
        LOGD("nativeCreateObject caught cv::Exception: %s", e.what());
        jclass jexception=env->FindClass("org/opencv/core/CvException");
        if(!jexception)
            jexception=env->FindClass("java/lang/Exception");
        env->ThrowNew(jexception, e.what());
    }
    catch(...)
    {
        LOGD("nativeCreateObject caught unknown Exception");
        jclass jexception=env->FindClass("java/lang/Exception");
        env->ThrowNew(jexception, "Unknown exception in JNI code of DetectionBasedTracker.nativeCreateObject()");
        return 0;
    }

    LOGD("Java_com_luxuan_opencv_DetectionBasedTracker_nativeCreateObject exit");
    return result;
}

JNIEXPORT void JNICALL Java_com_luxuan_opencv_DetectionBasedTracker_nativeDestroyObject(JNIEnv *env, jclass clazz, jlong thiz)
{
    LOGD("Java_com_luxuan_opencv_DetectionBasedTracker_nativeDestroyObject start");

    try
    {
        if(thiz!=0)
        {
            ((DetectorAgregator*)thiz)->tracker->stop();
            delete (DetectorAgregator*)thiz;
        }
    }
    catch(const cv::Exception& e)
    {
        LOGD("nativeDestroyObject caught cv::Exception: %s", e.what());
        jclass jexception=env->FindClass("org/opencv/core/CvException");
        if(!jexception)
            jexception=env->FindClass("java/lang/Exception");
        env->ThrowNew(jexception, e.what());
    }
    catch(...)
    {
        LOGD("nativeDestroyObject caught unknown exception");
        jclass jexception=env->FindClass("java/lang/Exception");
        env->ThrowNew(jexception, "Unknown exception in JNI code of DetectionBasedTracker.nativeDestroyObject()");
    }
    LOGD("Java_com_luxuan_opencv_DetectionBasedTracker_nativeDestroyObject exit");
}

JNIEXPORT void JNICALL Java_com_luxuan_opencv_DetectionBasedTracker_nativeStart(JNIEnv *env, jclass, jlong thiz)
{
    LOGD("Java_com_luxuan_opencv_DetectionBasedTracker_nativeStart");

    try
    {
        ((DetectorAgregator *)thiz)->tracker->run();
    }
    catch(const cv::Exception& e)
    {
        LOGD("nativeStart caught cv::Exception: %s", e.what());
        jclass jexception=env->FindClass("org/opencv/core/CvException");
        if(!jexception)
            jexception=env->FindClass("java/lang/Exception");
        env->ThrowNew(jexception, e.what());
    }
    catch(...)
    {
        LOGD("nativeStart cuahgt unknown exception");
        jclass jexception=env->FindClass("java/lang/Exception");
        env->ThrowNew(jexception, "Unknown in JNI code of DetectionBasedTracker.nativeStart");
    }
    LOGD("Java_com_luxuan_opencv_DetectionBasedTracker_nativeStart exit");
}