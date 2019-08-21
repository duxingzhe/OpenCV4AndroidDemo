LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OpenCV_INSTALL_MODULES := on
OpenCV_CAMERA_MODULES := off

OPENCV_LIB_TYPE :=STATIC

include $(LOCAL_PATH)/../../../../OpenCVLibrary3/src/main/jni/OpenCV.mk

LOCAL_MODULE := Stitcher

LOCAL_SRC_FILES := native-lib.cpp \
            stitcher.cpp

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../../../OpenCVLibrary3/src/main/jni/include

LOCAL_LDLIBS    += -lm -llog -landroid
LOCAL_LDFLAGS += -ljnigraphics

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := OpenCVHelper

LOCAL_C_INCLUDES := $(LOCAL_PATH)/../../../../OpenCVLibrary3/src/main/jni/include

LOCAL_SRC_FILES := com_luxuan_stitcher_OpenCVHelper.cpp \
                    extra_functions.cpp \
                    myconnect.cpp \
                    mydilate.cpp \
                    myerode.cpp \
                    mysmooth.cpp

LOCAL_SRC_FILES += extra_functions.cpp
LOCAL_LDLIBS    += -lm -llog -landroid
LOCAL_LDFLAGS += -ljnigraphics

include $(BUILD_SHARED_LIBRARY)