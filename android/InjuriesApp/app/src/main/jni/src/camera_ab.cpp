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
            cv::Vec3b &rgb_vec = rgb->at<cv::Vec3b>(i, width - j - 1);
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
    jfieldID  nameFieldId ;  
    jclass cls = env->GetObjectClass(obj);  //获得Java层该对象实例的类引用，即HelloJNI类引用  
    // lx
    nameFieldId = env->GetFieldID(cls , "deep_lx" , "I"); //获得属性句柄 
    jint jlx = env->GetIntField(obj , nameFieldId);
    //LOGD("     get lx  %d", (int)lx);
    // ly
    nameFieldId = env->GetFieldID(cls , "deep_ly" , "I"); //获得属性句柄 
    jint jly = env->GetIntField(obj , nameFieldId);
    //LOGD("     get ly  %d", (int)ly);
    // rx
    nameFieldId = env->GetFieldID(cls , "deep_rx" , "I"); //获得属性句柄 
    jint jrx = env->GetIntField(obj , nameFieldId);
    //LOGD("     get rx  %d", (int)rx);
    // ry
    nameFieldId = env->GetFieldID(cls , "deep_ry" , "I"); //获得属性句柄 
    jint jry = env->GetIntField(obj , nameFieldId);
    //LOGD("     get ry  %d", (int)ry);
    // near
    nameFieldId = env->GetFieldID(cls , "deep_near" , "I"); //获得属性句柄 
    jint near = env->GetIntField(obj , nameFieldId);
    // far
    nameFieldId = env->GetFieldID(cls , "deep_far" , "I"); //获得属性句柄 
    jint far = env->GetIntField(obj , nameFieldId);
    // deep_x_diff
    nameFieldId = env->GetFieldID(cls , "deep_x_diff" , "I"); //获得属性句柄 
    jint x_diff = env->GetIntField(obj , nameFieldId);
    // deep_y_diff
    nameFieldId = env->GetFieldID(cls , "deep_y_diff" , "I"); //获得属性句柄 
    jint y_diff = env->GetIntField(obj , nameFieldId);

    nameFieldId = env->GetFieldID(cls , "deep_center_dis" , "I"); //获得属性句柄 
    jint center_dis = env->GetIntField(obj , nameFieldId);
 
    short * srcBuf = (short *)env->GetDirectBufferAddress(src);
    cv::Mat* depth = (cv::Mat*)depthMatAddr;
    
    int width = depth->cols;
    int height = depth->rows;

    int lx = jlx + x_diff;
    int rx = jrx + x_diff;
    int ly = jly + y_diff;
    int ry = jry + y_diff;
    int valid_width = rx - lx;
    int valid_height = ry - ly;
    LOGD("=== depth start trans %d,%d,%d,%d,%d", width, height, strideInBytes,lx,ly);
    // LOGD("=== depth param  %d,%d,%d,%d", lx, rx, ly, ry);
    for (int i = ly; i < ry; i++) {
        short *pView = srcBuf + i * width + lx;
        for (int j = lx; j < rx; j++, pView++) {
            int fi = i - y_diff;
            int fj = j - x_diff;
            // LOGD("=== trans data %d,%d,%d,%d,%d", i,j,fi,fj, (int)*pView);
            short value = *pView;
            if (value < near || value > far) {
                depth->at<short>(fi, width - fj - 1) = 0;
                continue;
            }
            depth->at<short>(fi, width - fj - 1) = *pView;
            //LOGD("=== dstdata %d,%d,%d", rgb_vec[0], rgb_vec[1],rgb_vec[2]);
        }
    }
     
    int center_lx = valid_width / 2 - center_dis + lx;
    int center_rx = center_lx + 2 * center_dis;
    int center_ly = valid_height / 2 - center_dis + ly;
    int center_ry = center_ly + 2 * center_dis;
    LOGD("=== calc center deep %d,%d,%d,%d", center_lx, center_ly, center_rx, center_ry);
    int total_deep = 0;
    int count = 0;
    for (int i = center_lx; i < center_rx; i++) {
        for (int j = center_ly; j < center_ry; j++) {
            short &deep = depth->at<short>(j, i);
            // LOGD("=== center data %d,%d,%d", i,j,deep);
            if (deep != 0) {
                total_deep += deep;
                count++;
            }
        }
    }
    int center_deep = 0;
    if (count != 0) {
       center_deep = total_deep / count;
    }

    LOGD("     get center deep %d, %d, %d", center_deep, total_deep, count);
    nameFieldId = env->GetFieldID(cls , "deep_center_deep" , "I"); //获得属性句>    柄
    env->SetIntField(obj, nameFieldId, center_deep);

    return 0;

}
