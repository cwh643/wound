LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := usb-1.0
LOCAL_SRC_FILES := lib/libusb1.0.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := usb-1.0ir
LOCAL_SRC_FILES := lib/libusb1.0ir.so
include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE := tycam
LOCAL_SRC_FILES := lib/libtycamm.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

OpenCV_INSTALL_MODULES := on
OpenCV_CAMERA_MODULES := off
OPENCV_LIB_TYPE :=SHARED
#include src/main/jni/opencv_sdk/native/jni/OpenCV.mk
include opencv_sdk_249/native/jni/OpenCV.mk
LOCAL_C_INCLUDES += $(LOCAL_PATH)/src/include

LOCAL_SHARED_LIBRARIES := tycam usb-1.0 usb-1.0ir

LOCAL_MODULE    := rooxin_camm
LOCAL_SRC_FILES := src/camera_ab.cpp src/camera_ty.cpp
LOCAL_LDLIBS +=  -llog

include $(BUILD_SHARED_LIBRARY)



