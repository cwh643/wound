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

struct CallbackData {
    int             index;
    TY_DEV_HANDLE   hDevice;
    DepthRender*    render;
};

static int n;
static char buffer[1024*1024];
static volatile bool exit_main;
static TY_DEV_HANDLE hDevice;
static DepthRender render;
static CallbackData cb_data;
static char* frameBuffer[2];
static cv::Mat *dt;
static cv::Mat *pColor;

struct param_info_t {
    int min;
    int x_diff;
    int y_diff;
    int lx;
    int rx;
    int ly;
    int ry;
};


int frameHandler8X(TY_FRAME_DATA& frame, void* userdata, jlong deptMat, jlong rgbMat, JNIEnv* env, jobject &obj)
{
    CallbackData* pData = (CallbackData*) userdata;
    LOGD("=== Get frame %d", ++pData->index);

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
    // factor
    // LOGD(" get factor start ");
    // nameFieldId = env->GetFieldID(cls , "dept_factor" , "I"); //获得属性句柄 
    // jint factor_int = env->GetIntField(obj , nameFieldId);
    // float factor = factor_int / 100.0f;
    // LOGD(" get factor %f", factor);
    //LOGD("     get lx=%d,ly=%d,rx=%d,ry=%d,near=%d,far=%d", (int)lx, (int)ly, (int)rx, (int)ry, (int)near, (int)far);
    int min = 1000000;
    int lx = jlx + x_diff;
    int rx = jrx + x_diff;
    // int rx = (jrx  - jlx) / factor + jlx + x_diff;
    int ly = jly + y_diff;
    int ry = jry + y_diff;
    // int ry = (jry - jly) / factor + jly + y_diff;

    for( int i = 0; i < frame.validCount; i++ ){
        // get & show depth image
        if(frame.image[i].componentID == TY_COMPONENT_DEPTH_CAM){
            cv::Mat depth(frame.image[i].height, frame.image[i].width
                    , CV_16U, frame.image[i].buffer);
            int nl = depth.rows;  
            int nc = depth.cols * depth.channels();  
            //LOGD("     depth mat create %d, %d, %d", *(int *)frame.image[i].buffer, nl,nc);
            LOGD("     dept image width:%d, height:%d, size:%d", frame.image[i].width, frame.image[i].height, frame.image[i].size);
			dt = (cv::Mat*)deptMat;
            cv::Mat* rgb = (cv::Mat*)rgbMat;
            // depth.copyTo(*dt);
            // trunk data
            int nr=depth.rows;
            int no=depth.cols;
            for(int i = ly; i < ry; i++)
            {
                for(int j = lx; j < rx; j++) {
                    short value = depth.at<short>(i, j);
                    
                    // LOGD("     get value  %d, %d", (int)value, min);
                    min = min > value && value > 400 ? value : min;
                    // int fi = i * factor + (1 - factor) * ly - y_diff;
                    // int fj = j * factor + (1 - factor) * lx - x_diff;
                    int fi = i - y_diff;
                    int fj = j - x_diff;
                    if (value < near || value > far) {
                        dt->at<short>(fi, fj) = 0;
                        //cv::Vec3b &rgb_vec = rgb->at<cv::Vec3b>(fi, fj);
                        //rgb_vec[0] = 255;
                        //rgb_vec[1] = 255;
                        //rgb_vec[2] = 255;
                        continue;
                    }
                    dt->at<short>(fi, fj) = value;
                    // cv::Vec3b &rgb_vec = rgb->at<cv::Vec3b>(fi, fj);
                    // rgb_vec[0] = 0xf0;
                    // rgb_vec[1] = 0x80;
                    // rgb_vec[2] = 0x80;
                }
            }

            LOGD("     get min  %d", min);
            nameFieldId = env->GetFieldID(cls , "deep_min_deep" , "I"); //获得属性句柄 
            env->SetIntField(obj, nameFieldId, min); 
            //LOGD("     get min  %d", min);
            // short* ptr = depth.ptr<short>();
            // short* dest_ptr = dt->ptr<short>();

            // for (int i = depth.size().area(); i != 0; i--) {
            //     // LOGD("   deep %d" , (int)*ptr);
            //     if (*ptr <= far && *ptr >= near) {
            //         // LOGD("   filte deep %d" , (int)*ptr);
            //         *dest_ptr = *ptr;
            //     } else {
            //         *dest_ptr = 0;
            //     }
            //     ptr++;
            //     dest_ptr++;
            // }
        }
        
        // get & show RGB
        if(frame.image[i].componentID == TY_COMPONENT_RGB_CAM){
            continue;
            LOGD("pixelFormat %d", frame.image[i].pixelFormat); 
            LOGD("     RGB image width:%d, height:%d, size:%d", frame.image[i].width, frame.image[i].height, frame.image[i].size);
            pColor = (cv::Mat*)rgbMat;
            // get BGR
            if (frame.image[i].pixelFormat == TY_PIXEL_FORMAT_YVYU){
                LOGD("RGB YVYU");
                cv::Mat yuv(pColor->rows, pColor->cols
                        , CV_8UC2, frame.image[i].buffer);
                cv::cvtColor(yuv, *pColor, cv::COLOR_YUV2BGR_YVYU);
            }
            else if (frame.image[i].pixelFormat == TY_PIXEL_FORMAT_YUYV){
                LOGD("RGB YUYV");
                cv::Mat yuv(pColor->rows, pColor->cols
                        , CV_8UC2, frame.image[i].buffer);
                cv::cvtColor(yuv, *pColor, cv::COLOR_YUV2BGR_YUYV);
            } else if(frame.image[i].pixelFormat == TY_PIXEL_FORMAT_RGB){
                LOGD("RGB RGB");
                cv::Mat rgb(pColor->rows, pColor->cols
                        , CV_8UC3, frame.image[i].buffer);
                cv::cvtColor(rgb, *pColor, cv::COLOR_RGB2BGR);
            } else if(frame.image[i].pixelFormat == TY_PIXEL_FORMAT_MONO){
                LOGD("RGB MONO");
                cv::Mat gray(pColor->rows, pColor->cols
                        , CV_8U, frame.image[i].buffer);
                cv::cvtColor(gray, *pColor, cv::COLOR_GRAY2BGR);
            }
            int nl = pColor->rows;  
            int nc = pColor->cols;  
            int nn = pColor->channels();  
            LOGD("     RGB mat create %d, %d, %d, %d", *(int *)frame.image[i].buffer, nl, nc, nn);
        }
        // get & show left ir image
        if(frame.image[i].componentID == TY_COMPONENT_IR_CAM_LEFT){
            LOGD("     dept IR_LEFT width:%d, height:%d, size:%d", frame.image[i].width, frame.image[i].height, frame.image[i].size);
            //cv::Mat leftIR(frame.image[i].height, frame.image[i].width
            //        , CV_8U, frame.image[i].buffer);
            //cv::imshow("LeftIR", leftIR);
        }
        // get & show right ir image
        if(frame.image[i].componentID == TY_COMPONENT_IR_CAM_RIGHT){
            LOGD("     dept IR_RIGHT width:%d, height:%d, size:%d", frame.image[i].width, frame.image[i].height, frame.image[i].size);
            //cv::Mat rightIR(frame.image[i].height, frame.image[i].width
            //       , CV_8U, frame.image[i].buffer);
            //cv::imshow("RightIR", rightIR);
        }
        // get point3D
        if(frame.image[i].componentID == TY_COMPONENT_POINT3D_CAM){
            LOGD("     dept 3D_POINT width:%d, height:%d, size:%d", frame.image[i].width, frame.image[i].height, frame.image[i].size);
            //cv::Mat point3D(frame.image[i].height, frame.image[i].width
            //        , CV_32FC3, frame.image[i].buffer);
        }
        
    }

    /*
    int key = cv::waitKey(1);
    switch(key){
        case -1:
            break;
        case 'q': case 1048576 + 'q':
            exit_main = true;
            break;
        default:
            LOGD("Pressed key %d", key);
    }
    */
    // LOGD("=== Callback: Re-enqueue buffer(%p, %d)", frame.userBuffer, frame.bufferSize);
    ASSERT_OK( TYEnqueueBuffer(pData->hDevice, frame.userBuffer, frame.bufferSize) );
}

int frameHandler(TY_FRAME_DATA& frame, void* userdata, jlong deptMat, jlong rgbMat, JNIEnv* env, jobject &obj)
{
    CallbackData* pData = (CallbackData*) userdata;
    LOGD("=== Get frame %d", ++pData->index);

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
    // factor
    // LOGD(" get factor start ");
    // nameFieldId = env->GetFieldID(cls , "dept_factor" , "I"); //获得属性句柄 
    // jint factor_int = env->GetIntField(obj , nameFieldId);
    // float factor = factor_int / 100.0f;
    // LOGD(" get factor %f", factor);
    //LOGD("     get lx=%d,ly=%d,rx=%d,ry=%d,near=%d,far=%d", (int)lx, (int)ly, (int)rx, (int)ry, (int)near, (int)far);
    int min = 1000000;
    int lx = jlx + x_diff;
    int rx = jrx + x_diff;
    // int rx = (jrx  - jlx) / factor + jlx + x_diff;
    int ly = jly + y_diff;
    int ry = jry + y_diff;
    // int ry = (jry - jly) / factor + jly + y_diff;

    for( int i = 0; i < frame.validCount; i++ ){
        // get & show depth image
        if(frame.image[i].componentID == TY_COMPONENT_DEPTH_CAM){
            cv::Mat depth(frame.image[i].height, frame.image[i].width
                    , CV_16U, frame.image[i].buffer);
            int nl = depth.rows;  
            int nc = depth.cols * depth.channels();  
            //LOGD("     depth mat create %d, %d, %d", *(int *)frame.image[i].buffer, nl,nc);
            LOGD("     dept image width:%d, height:%d, size:%d", frame.image[i].width, frame.image[i].height, frame.image[i].size);
			dt = (cv::Mat*)deptMat;
            cv::Mat* rgb = (cv::Mat*)rgbMat;
            // depth.copyTo(*dt);
            // trunk data
            int nr=depth.rows;
            int no=depth.cols;
            for(int i = ly; i < ry; i++)
            {
                for(int j = lx; j < rx; j++) {
                    short value = depth.at<short>(i, j);
                    
                    // LOGD("     get value  %d, %d", (int)value, min);
                    min = min > value && value > 400 ? value : min;
                    // int fi = i * factor + (1 - factor) * ly - y_diff;
                    // int fj = j * factor + (1 - factor) * lx - x_diff;
                    int fi = i - y_diff;
                    int fj = j - x_diff;
                    if (value < near || value > far) {
                        dt->at<short>(fi, fj) = 0;
                        //cv::Vec3b &rgb_vec = rgb->at<cv::Vec3b>(fi, fj);
                        //rgb_vec[0] = 255;
                        //rgb_vec[1] = 255;
                        //rgb_vec[2] = 255;
                        continue;
                    }
                    dt->at<short>(fi, fj) = value;
                    // cv::Vec3b &rgb_vec = rgb->at<cv::Vec3b>(fi, fj);
                    // rgb_vec[0] = 0xf0;
                    // rgb_vec[1] = 0x80;
                    // rgb_vec[2] = 0x80;
                }
            }

            LOGD("     get min  %d", min);
            nameFieldId = env->GetFieldID(cls , "deep_min_deep" , "I"); //获得属性句柄 
            env->SetIntField(obj, nameFieldId, min); 
            //LOGD("     get min  %d", min);
            // short* ptr = depth.ptr<short>();
            // short* dest_ptr = dt->ptr<short>();

            // for (int i = depth.size().area(); i != 0; i--) {
            //     // LOGD("   deep %d" , (int)*ptr);
            //     if (*ptr <= far && *ptr >= near) {
            //         // LOGD("   filte deep %d" , (int)*ptr);
            //         *dest_ptr = *ptr;
            //     } else {
            //         *dest_ptr = 0;
            //     }
            //     ptr++;
            //     dest_ptr++;
            // }
        }
        
        // get & show RGB
        if(frame.image[i].componentID == TY_COMPONENT_RGB_CAM){
            continue;
            LOGD("pixelFormat %d", frame.image[i].pixelFormat); 
            LOGD("     RGB image width:%d, height:%d, size:%d", frame.image[i].width, frame.image[i].height, frame.image[i].size);
            pColor = (cv::Mat*)rgbMat;
            // get BGR
            if (frame.image[i].pixelFormat == TY_PIXEL_FORMAT_YVYU){
                LOGD("RGB YVYU");
                cv::Mat yuv(pColor->rows, pColor->cols
                        , CV_8UC2, frame.image[i].buffer);
                cv::cvtColor(yuv, *pColor, cv::COLOR_YUV2BGR_YVYU);
            }
            else if (frame.image[i].pixelFormat == TY_PIXEL_FORMAT_YUYV){
                LOGD("RGB YUYV");
                cv::Mat yuv(pColor->rows, pColor->cols
                        , CV_8UC2, frame.image[i].buffer);
                cv::cvtColor(yuv, *pColor, cv::COLOR_YUV2BGR_YUYV);
            } else if(frame.image[i].pixelFormat == TY_PIXEL_FORMAT_RGB){
                LOGD("RGB RGB");
                cv::Mat rgb(pColor->rows, pColor->cols
                        , CV_8UC3, frame.image[i].buffer);
                cv::cvtColor(rgb, *pColor, cv::COLOR_RGB2BGR);
            } else if(frame.image[i].pixelFormat == TY_PIXEL_FORMAT_MONO){
                LOGD("RGB MONO");
                cv::Mat gray(pColor->rows, pColor->cols
                        , CV_8U, frame.image[i].buffer);
                cv::cvtColor(gray, *pColor, cv::COLOR_GRAY2BGR);
            }
            int nl = pColor->rows;  
            int nc = pColor->cols;  
            int nn = pColor->channels();  
            LOGD("     RGB mat create %d, %d, %d, %d", *(int *)frame.image[i].buffer, nl, nc, nn);
        }
        // get & show left ir image
        if(frame.image[i].componentID == TY_COMPONENT_IR_CAM_LEFT){
            LOGD("     dept IR_LEFT width:%d, height:%d, size:%d", frame.image[i].width, frame.image[i].height, frame.image[i].size);
            //cv::Mat leftIR(frame.image[i].height, frame.image[i].width
            //        , CV_8U, frame.image[i].buffer);
            //cv::imshow("LeftIR", leftIR);
        }
        // get & show right ir image
        if(frame.image[i].componentID == TY_COMPONENT_IR_CAM_RIGHT){
            LOGD("     dept IR_RIGHT width:%d, height:%d, size:%d", frame.image[i].width, frame.image[i].height, frame.image[i].size);
            //cv::Mat rightIR(frame.image[i].height, frame.image[i].width
            //       , CV_8U, frame.image[i].buffer);
            //cv::imshow("RightIR", rightIR);
        }
        // get point3D
        if(frame.image[i].componentID == TY_COMPONENT_POINT3D_CAM){
            LOGD("     dept 3D_POINT width:%d, height:%d, size:%d", frame.image[i].width, frame.image[i].height, frame.image[i].size);
            //cv::Mat point3D(frame.image[i].height, frame.image[i].width
            //        , CV_32FC3, frame.image[i].buffer);
        }
        
    }

    /*
    int key = cv::waitKey(1);
    switch(key){
        case -1:
            break;
        case 'q': case 1048576 + 'q':
            exit_main = true;
            break;
        default:
            LOGD("Pressed key %d", key);
    }
    */
    // LOGD("=== Callback: Re-enqueue buffer(%p, %d)", frame.userBuffer, frame.bufferSize);
    ASSERT_OK( TYEnqueueBuffer(pData->hDevice, frame.userBuffer, frame.bufferSize) );
}

int OpenDevice() {
	   LOGD("=== Init lib");
	    ASSERT_OK( TYInitLib() );
	    TY_VERSION_INFO* pVer = (TY_VERSION_INFO*)buffer;
	    ASSERT_OK(TYLibVersion(pVer));
	    LOGD("     - lib version: %d.%d.%d", pVer->major, pVer->minor, pVer->patch);

	    LOGD("=== Get device info");
	    ASSERT_OK(TYGetDeviceNumber(&n));
	    LOGD("     - device number %d", n);

	    TY_DEVICE_BASE_INFO* pBaseInfo = (TY_DEVICE_BASE_INFO*)buffer;
	    ASSERT_OK(TYGetDeviceList(pBaseInfo, 100, &n));

	    if(n == 0){
	        LOGD("=== No device got");
	        return -1;
	    }

	    LOGD("=== Open device 0");
	    ASSERT_OK(TYOpenDevice(pBaseInfo[0].id, &hDevice));

	    int32_t allComps;
	    ASSERT_OK( TYGetComponentIDs(hDevice, &allComps) );
	    if(allComps & TY_COMPONENT_RGB_CAM){
	        LOGD("=== Has RGB camera, open RGB cam");
	        ASSERT_OK( TYEnableComponents(hDevice, TY_COMPONENT_RGB_CAM) );
	    }

	    LOGD("=== Configure components, open depth cam");
	    int32_t componentIDs = TY_COMPONENT_DEPTH_CAM | TY_COMPONENT_IR_CAM_LEFT;
	    ASSERT_OK( TYEnableComponents(hDevice, componentIDs) );

	    LOGD("=== Configure feature, set resolution to 640x480.");
	    LOGD("Note: DM460 resolution feature is in component TY_COMPONENT_DEVICE,");
	    LOGD("      other device may lays in some other components.");
	    int err = TYSetEnum(hDevice, TY_COMPONENT_DEPTH_CAM, TY_ENUM_IMAGE_MODE, TY_IMAGE_MODE_640x480);
		LOGD("err = %d", err);
	    ASSERT(err == TY_STATUS_OK || err == TY_STATUS_NOT_PERMITTED);

	    LOGD("=== Prepare image buffer");
	    int32_t frameSize;
	    ASSERT_OK( TYGetFrameBufferSize(hDevice, &frameSize) );
	    LOGD("     - Get size of framebuffer, %d", frameSize);
	    // ASSERT( frameSize >= 640*480*2 );

	    LOGD("     - Allocate & enqueue buffers");

	    frameBuffer[0] = new char[frameSize];
	    frameBuffer[1] = new char[frameSize];
	    LOGD("     - Enqueue buffer (%p, %d)", frameBuffer[0], frameSize);
	    ASSERT_OK( TYEnqueueBuffer(hDevice, frameBuffer[0], frameSize) );
	    LOGD("     - Enqueue buffer (%p, %d)", frameBuffer[1], frameSize);
	    ASSERT_OK( TYEnqueueBuffer(hDevice, frameBuffer[1], frameSize) );

	    LOGD("=== Register callback");
	    LOGD("Note: Callback may block internal data receiving,");
	    LOGD("      so that user should not do long time work in callback.");
	    LOGD("      To avoid copying data, we pop the framebuffer from buffer queue and");
	    LOGD("      give it back to user, user should call TYEnqueueBuffer to re-enqueue it.");

	    cb_data.index = 0;
	    cb_data.hDevice = hDevice;
	    cb_data.render = &render;
	    // ASSERT_OK( TYRegisterCallback(hDevice, frameHandler, &cb_data) );

	    LOGD("=== Disable trigger mode");
	    ASSERT_OK( TYSetBool(hDevice, TY_COMPONENT_DEVICE, TY_BOOL_TRIGGER_MODE, false) );

	    LOGD("=== Start capture");
	    ASSERT_OK( TYStartCapture(hDevice) );

	LOGD("open device successful");
	return 0;
}

int CloseDevice() {
    LOGD("start close");
    ASSERT_OK( TYStopCapture(hDevice) );
    LOGD("stop capture");
    ASSERT_OK( TYCloseDevice(hDevice) );
    LOGD("close device");
    ASSERT_OK( TYDeinitLib() );
    LOGD("deinitlib");
    // MSLEEP(10); // sleep to ensure buffer is not used any more
    delete frameBuffer[0];
    delete frameBuffer[1];
	return 0;
}

extern "C" {
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_TyNativeUtils_OpenDevice(JNIEnv* env, jobject thiz);
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_TyNativeUtils_CloseDevice(JNIEnv* env, jobject thiz);
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_TyNativeUtils_StartDevice(JNIEnv* env, jobject thiz);
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_TyNativeUtils_StopDevice(JNIEnv* env, jobject thiz);
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_TyNativeUtils_FetchData(JNIEnv* env, jobject thiz, jlong depthMatAddr, jlong rgbMatAddr);
// ab
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_AbNativeUtils_ConvertTORGBA(JNIEnv* env, jobject obj, jobject src, jobject dst, jint w, jint h, jint strideInBytes);
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_AbNativeUtils_RGB888TORGBA(JNIEnv* env, jobject obj, jobject src, jobject dst, jint w, jint h, jint strideInBytes);
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_AbNativeUtils_rgb2mat(JNIEnv* env, jobject obj, jobject src, jlong rgbMatAddr, jint strideInBytes);
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_AbNativeUtils_depth2mat(JNIEnv* env, jobject obj, jobject src, jlong rgbMatAddr, jint strideInBytes);
}

JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_TyNativeUtils_OpenDevice(JNIEnv* env, jobject thiz)
{
    return OpenDevice();
}

JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_TyNativeUtils_CloseDevice(JNIEnv* env, jobject thiz)
{
    return CloseDevice();
}

JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_TyNativeUtils_StartDevice(JNIEnv* env, jobject thiz)
{
    LOGD("=== Start capture");
	return TYStartCapture(hDevice);
}

JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_TyNativeUtils_StopDevice(JNIEnv* env, jobject thiz)
{
    return TYStopCapture(hDevice);
}

JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_TyNativeUtils_FetchData(JNIEnv* env, jobject thiz, jlong depthMatAddr, jlong rgbMatAddr)
{
	LOGD("Fetch Data");
    //exit_main = false;
    TY_FRAME_DATA frame;
    int err = TYFetchFrame(hDevice, &frame, (uint64_t)100000);
    LOGD("err = %d", err);
    if( err != TY_STATUS_OK ){
        //LOGD("... Drop one frame");
        return err;
    }
    return frameHandler(frame, &cb_data, depthMatAddr, rgbMatAddr, env, thiz);
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
