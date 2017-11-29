#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <vector>

#define _CRT_SECURE_NO_WARNINGS

#ifdef _WIN32
#include <time.h>
#else
#include <sys/time.h>
#endif

#include "percipio_camport.h"
#include <opencv2/opencv.hpp>
#include <stdio.h>
#include <stdlib.h>
#include "common.hpp"

#define LOG_TAG "rooxin_jni"
#include <android/log.h>
#include <string.h>
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, fmt, ##args)


extern "C" {
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_AbNativeUtils_ConvertTORGBA(JNIEnv* env, jobject obj, jobject src, jobject dst, jint w, jint h, jint strideInBytes);
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_AbNativeUtils_RGB888TORGBA(JNIEnv* env, jobject obj, jobject src, jobject dst, jint w, jint h, jint strideInBytes);
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_AbNativeUtils_rgb2mat(JNIEnv* env, jobject obj, jobject src, jlong rgbMatAddr, jint strideInBytes);
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_AbNativeUtils_depth2mat(JNIEnv* env, jobject obj, jobject src, jlong rgbMatAddr, jint strideInBytes);
}

typedef unsigned char uint8_t;
int* m_histogram;
enum { HISTSIZE = 0xFFFF, };
typedef unsigned char uint8_t;
typedef struct {
    uint8_t r;
    uint8_t g;
    uint8_t b;
}RGB888Pixel;


int ConventToRGBA(uint8_t* src, uint8_t* dst,  int w, int h, int strideInBytes){
    for (int y = 0; y < h; ++y)
    {
        uint8_t* pTexture = dst +  (y*w*4);
        const RGB888Pixel* pData = (const RGB888Pixel*)(src + y * strideInBytes);
        for (int x = 0; x < w; ++x, ++pData, pTexture += 4)
        {
            pTexture[0] = pData->r;
            pTexture[1] = pData->g;
            pTexture[2] = pData->b;
            pTexture[3] = 255;
        }

    }

    return 0;
}


jint Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_AbNativeUtils_RGB888TORGBA(JNIEnv* env, jobject obj, jobject src, jobject dst, jint w, jint h, jint strideInBytes){

    if(src == nullptr || dst == nullptr){
        return -1;
    }
    uint8_t* srcBuf = (uint8_t*)env->GetDirectBufferAddress(src);

    uint8_t* dstBuf = (uint8_t*)env->GetDirectBufferAddress(dst);

    ConventToRGBA(srcBuf, dstBuf, w, h, strideInBytes);

    return 0;
}

int ConventFromDepthToRGBA(short* src, int* dst,  int w, int h, int strideInBytes){

    // Calculate the accumulative histogram (the yellow display...)
    if (m_histogram == NULL) {
        m_histogram = new int[HISTSIZE];
    }
    memset(m_histogram, 0, HISTSIZE * sizeof(int));

    int nNumberOfPoints = 0;
    unsigned int value;
    int Size = w * h;
    for (int i = 0; i < Size; ++i) {
        value =src[i];
        if (value != 0) {
            m_histogram[value]++;
            nNumberOfPoints++;
        }
    }

    int nIndex;
    for (nIndex = 1; nIndex < HISTSIZE; nIndex++) {
        m_histogram[nIndex] += m_histogram[nIndex - 1];
    }

    if (nNumberOfPoints != 0) {
        for (nIndex = 1; nIndex < HISTSIZE; nIndex++) {
            m_histogram[nIndex] = (unsigned int)(256 * (1.0f - ((float)m_histogram[nIndex] / nNumberOfPoints)));
        }
    }

    for (int y = 0; y < h; ++y) {
        uint8_t* rgb = (uint8_t*) (dst + y * w);
        short* pView = src + y * w;
        for (int x = 0; x < w; ++x, rgb += 4, pView++) {
            value = m_histogram[*pView];
            rgb[0] =value;
            rgb[1] = value;
            rgb[2] = 0x00;
            rgb[3] = 0xff;
        }
    }
    return 0;
}

JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_AbNativeUtils_ConvertTORGBA(JNIEnv* env, jobject obj, jobject src, jobject dst, jint w, jint h, jint strideInBytes) {
    if(src == nullptr || dst == nullptr){
        return -1;
    }
    short * srcBuf = (short *)env->GetDirectBufferAddress(src);

    int* dstBuf = (int*)env->GetDirectBufferAddress(dst);

    ConventFromDepthToRGBA(srcBuf, dstBuf, w, h, strideInBytes);

    return 0;
   
}


JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_AbNativeUtils_rgb2mat(JNIEnv* env, jobject obj, jobject src, jlong rgbMatAddr, jint strideInBytes) {
    if(src == nullptr){
        return -1;
    }
    uint8_t * srcBuf = (uint8_t *)env->GetDirectBufferAddress(src);
    cv::Mat* rgb = (cv::Mat*)rgbMatAddr;

    int width = rgb->cols;
    int height = rgb->rows;

    LOGD("=== start trans %d,%d.%d", width, height, strideInBytes);
    for (int i = 0; i < height; i++) {
        const RGB888Pixel* pData = (const RGB888Pixel*)(srcBuf + i * strideInBytes);
        for (int j = 0; j < width; j++, ++pData) {
            cv::Vec3b &rgb_vec = rgb->at<cv::Vec3b>(i, j);
            // LOGD("=== trans data %d,%d, %d,%d,%d", i,j,pData->r,pData->g,pData->b);
            // LOGD("=== dstdata %d,%d,%d", rgb_vec[0], rgb_vec[1],rgb_vec[2]);
            rgb_vec[0] = pData->r;
            rgb_vec[1] = pData->g;
            rgb_vec[2] = pData->b;
            // LOGD("=== dstdata %d,%d,%d", rgb_vec[0], rgb_vec[1],rgb_vec[2]);

        }
    }

    return 0;

}

JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_AbNativeUtils_depth2mat(JNIEnv* env, jobject obj, jobject src, jlong depthMatAddr, jint strideInBytes) {
    if(src == nullptr){
        return -1;
    }
    short * srcBuf = (short *)env->GetDirectBufferAddress(src);
    cv::Mat* depth = (cv::Mat*)depthMatAddr;

    int width = depth->cols;
    int height = depth->rows;

    LOGD("=== depth start trans %d,%d.%d", width, height, strideInBytes);
    for (int i = 0; i < height; i++) {
        short *pView = srcBuf + i * width;
        for (int j = 0; j < width; j++, pView++) {
            //LOGD("=== trans data %d,%d, %d,%d,%d", i,j,pData->r,pData->g,pData->b);
            depth->at<short>(i, j) = *pView;
            //LOGD("=== dstdata %d,%d,%d", rgb_vec[0], rgb_vec[1],rgb_vec[2]);
        }
    }

    return 0;

}
