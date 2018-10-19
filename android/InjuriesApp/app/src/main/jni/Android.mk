LOCAL_PATH := $(call my-dir)

ifeq ($(TARGET_ARCH_ABI), arm64-v8a)
	THIRD_LIB_PATH := lib/arm64-v8a
endif

ifeq ($(TARGET_ARCH_ABI), armeabi)
	THIRD_LIB_PATH := lib/armeabi
endif

ifeq ($(TARGET_ARCH_ABI), armeabi-v7a)
	THIRD_LIB_PATH := lib/armeabi-v7a
endif

#$(warning $(TARGET_ARCH_ABI))
#$(warning $(THIRD_LIB_PATH))

include $(CLEAR_VARS)
LOCAL_MODULE := usb-1.0
LOCAL_SRC_FILES := $(THIRD_LIB_PATH)/libusb.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := tycam
LOCAL_SRC_FILES := $(THIRD_LIB_PATH)/libtycam.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

OpenCV_INSTALL_MODULES := on
OpenCV_CAMERA_MODULES := off
OPENCV_LIB_TYPE :=STATIC
include opencv_sdk_300/native/jni/OpenCV.mk
LOCAL_C_INCLUDES += $(LOCAL_PATH)/src/include

LOCAL_SHARED_LIBRARIES := tycam usb-1.0

LOCAL_MODULE    := rooxin_camm
LOCAL_SRC_FILES := src/camera_ab.cpp src/camera_ty.cpp
#LOCAL_SRC_FILES := src/jni_part_v2.cpp 
LOCAL_LDLIBS +=  -llog

include $(BUILD_SHARED_LIBRARY)



