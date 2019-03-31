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
#include "utils.cpp"

#define LOG_TAG "rooxin_jni"
#include <android/log.h>
#include <string.h>
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, fmt, ##args)

struct CallbackData {
    int             index;
    TY_DEV_HANDLE   hDevice;
    DepthRender*    render;
    cv::Mat         colorM;
    cv::Mat         colorD;
    TY_CAMERA_DISTORTION color_dist;
    TY_CAMERA_INTRINSIC color_intri;
};

static int n;
static char buffer[1024*1024 * 20];
static volatile bool exit_main;
static TY_DEV_HANDLE hDevice;
static DepthRender render;
static CallbackData cb_data;
static char* frameBuffer[2];
static cv::Mat *dt;
static cv::Mat *pColor;

int undistort_rgb(CallbackData &pData, cv::Mat &color, cv::Mat &dst_color) {
    cv::Mat undistort_result(color.size(), CV_8UC3);
    TY_IMAGE_DATA dst;
    dst.width = color.cols;
    dst.height = color.rows;
    dst.size = undistort_result.size().area() * 3;
    dst.buffer = undistort_result.data;
    dst.pixelFormat = TY_PIXEL_FORMAT_RGB;
    TY_IMAGE_DATA src;
    src.width = color.cols;
    src.height = color.rows;
    src.size = color.size().area() * 3;
    src.pixelFormat = TY_PIXEL_FORMAT_RGB;
    src.buffer = color.data;
    //undistort camera image 
    //TYUndistortImage accept TY_IMAGE_DATA from TY_FRAME_DATA , pixel format RGB888 or MONO8
    //you can also use opencv API cv::undistort to do this job.
    ASSERT_OK(TYUndistortImage(&pData.color_intri, &pData.color_dist, NULL, &src, &dst));
    dst_color = undistort_result;
    return 0;
}

int frameHandler8X(TY_FRAME_DATA& frame, void* userdata, jlong deptMat, jlong rgbMat, jlong pointMat, JNIEnv* env, jobject &obj)
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

    nameFieldId = env->GetFieldID(cls , "deep_center_dis" , "I"); //获得属性句柄 
    jint center_dis = env->GetIntField(obj , nameFieldId);
    // int ry = (jry - jly) / factor + jly + y_diff;

    cv::Size depth_size = (*((cv::Mat*) pointMat)).size();
    for( int i = 0; i < frame.validCount; i++ ){
        // get & show depth image
        LOGD("=== frame id %d", frame.image[i].componentID);
        if(frame.image[i].componentID == TY_COMPONENT_DEPTH_CAM){
            // cv::Mat depth(frame.image[i].height, frame.image[i].width
            //         , CV_16U, frame.image[i].buffer);
            // int nl = depth.rows;  
            // int nc = depth.cols * depth.channels();  

            // //LOGD("     depth mat create %d, %d, %d", *(int *)frame.image[i].buffer, nl,nc);
            // LOGD("     dept width:%d, height:%d, size:%d", frame.image[i].width, frame.image[i].height, frame.image[i].size);
			// dt = (cv::Mat*)deptMat;
            // // depth.copyTo(*dt);
            // // trunk data
            // int nr=depth.rows;
            // int no=depth.cols;
            // int min = 0;
            // int min_count = 0;
            // // calc offser
            // int lx = jlx + x_diff;
            // int rx = jrx + x_diff;
            // int ly = jly + y_diff;
            // int ry = jry + y_diff;
            // 
            // int center_lx = (rx - lx) / 2 + lx - center_dis;
            // int center_ly = (ry - ly) / 2 + ly - center_dis;
            // int center_rx = center_lx + center_dis * 2;
            // int center_ry = center_ly + center_dis * 2;
            // for(int i = ly; i < ry; i++)
            // {
            //     for(int j = lx; j < rx; j++) {
            //         short &value = depth.at<short>(i, j);
            //         
            //         // LOGD("     get value %d, %d, %d, %d", j, i, value, min);
            //         
            //         int fi = i - y_diff;
            //         int fj = j - x_diff;
            //         if (value < near || value > far) {
            //             dt->at<short>(fi, fj) = 0;
            //             continue;
            //         }
            //         if (fi >= center_ly && fi < center_ry
            //             && fj >= center_lx && fj < center_rx) {
            //             min += value;
            //             min_count++;
            //         }
            //         dt->at<short>(fi, fj) = value;
            //     }
            // }
            // nameFieldId = env->GetFieldID(cls , "deep_center_deep" , "I"); //获得属性句柄 
            // if (min_count == 0) {
            //     min_count = 1;
            // }
            // int center_deep = min/min_count;
            // LOGD("     get center deep  %d", center_deep);
            // env->SetIntField(obj, nameFieldId, center_deep);
            // depth_size = dt->size();
        }
        
        // get & show RGB
        if(frame.image[i].componentID == TY_COMPONENT_RGB_CAM){
            // LOGD("pixelFormat %d", frame.image[i].pixelFormat); 
            LOGD("     RGB image width:%d, height:%d, size:%d", frame.image[i].width, frame.image[i].height, frame.image[i].size);
            cv::Mat &color =  *((cv::Mat*)rgbMat);
            // cv::Mat color;
            pColor = (cv::Mat*)rgbMat;
            // get BGR
            if (frame.image[i].pixelFormat == TY_PIXEL_FORMAT_YVYU){
                LOGD("RGB YVYU");
                cv::Mat yuv(pColor->rows, pColor->cols
                        , CV_8UC2, frame.image[i].buffer);
                cv::cvtColor(yuv, color, cv::COLOR_YUV2BGR_YVYU);
            }
            else if (frame.image[i].pixelFormat == TY_PIXEL_FORMAT_YUYV){
                LOGD("RGB YUYV");
                cv::Mat yuv(pColor->rows, pColor->cols
                        , CV_8UC2, frame.image[i].buffer);
                // 注意 这里是转换为RGB，源代码中是BGR，是sdk的bug还是demo写错了需要确认
                cv::cvtColor(yuv, color, cv::COLOR_YUV2RGB_YUYV);
            } else if(frame.image[i].pixelFormat == TY_PIXEL_FORMAT_RGB){
                LOGD("RGB RGB");
                cv::Mat rgb(pColor->rows, pColor->cols
                        , CV_8UC3, frame.image[i].buffer);
                cv::cvtColor(rgb, color, cv::COLOR_RGB2BGR);
            } else if(frame.image[i].pixelFormat == TY_PIXEL_FORMAT_MONO){
                LOGD("RGB MONO");
                cv::Mat gray(pColor->rows, pColor->cols
                        , CV_8U, frame.image[i].buffer);
                cv::cvtColor(gray, color, cv::COLOR_GRAY2BGR);
            }
            int nl = pColor->rows;  
            int nc = pColor->cols;  
            int nn = pColor->channels();  
            LOGD("     RGB mat create %d, %d, %d, %d", *(int *)frame.image[i].buffer, nl, nc, nn);
            // 使用图样api
            // undistort_rgb(*pData, color, *pColor);
            cv::Mat undistort_color;
            if(!pData->colorM.empty()){
                LOGD("     RGB mat undistortImage");
                // 直接使用opencv的畸变方式
                // cv::undistort(color, undistort_color, pData->colorM, pData->colorD, pData->colorM);
            }
            //cv::resize(undistort_color, *pColor, depth_size);
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
            //*pPoints = cv::Mat(frame.image[i].height, frame.image[i].width
            //        , CV_32FC3, frame.image[i].buffer);
			
			cv::Mat point3D(frame.image[i].height, frame.image[i].width
                    , CV_32FC3, frame.image[i].buffer);

			dt = (cv::Mat*)pointMat;
            LOGD("color rows:%d, cols:%d", point3D.rows, point3D.cols);
			ASSERT_OK( TYRegisterWorldToColor(pData->hDevice, (TY_VECT_3F*)point3D.data, 0
						, point3D.cols * point3D.rows, (uint16_t*)buffer, sizeof(buffer)
						));
			cv::Mat depth = cv::Mat(dt->rows, dt->cols, CV_16U, (uint16_t*)buffer);
            //*dt = point3D;
			//*dt = cv::Mat(dt->rows, dt->cols, CV_16U, (uint16_t*)buffer);

			int nl = depth.rows;  
            int nc = depth.cols * depth.channels();  
            int nr=depth.rows;
            int no=depth.cols;
            int min = 0;
            int min_count = 0;
            // calc offser
            int lx = jlx + x_diff;
            int rx = jrx + x_diff;
            int ly = jly + y_diff;
            int ry = jry + y_diff;
            
            int center_lx = (rx - lx) / 2 + lx - center_dis;
            int center_ly = (ry - ly) / 2 + ly - center_dis;
            int center_rx = center_lx + center_dis * 2;
            int center_ry = center_ly + center_dis * 2;
            for(int i = ly; i < ry; i++)
            {
                for(int j = lx; j < rx; j++) {
                    short &value = depth.at<short>(i, j);
                    
                    // LOGD("     get value %d, %d, %d, %d", j, i, value, min);
                    
                    int fi = i - y_diff;
                    int fj = j - x_diff;
                    if (value < near || value > far) {
                        dt->at<short>(fi, fj) = 0;
                        continue;
                    }
                    if (fi >= center_ly && fi < center_ry
                        && fj >= center_lx && fj < center_rx) {
                        min += value;
                        min_count++;
                    }
                    dt->at<short>(fi, fj) = value;
                }
            }
            nameFieldId = env->GetFieldID(cls , "deep_center_deep" , "I"); //获得属性句柄 
            if (min_count == 0) {
                min_count = 1;
            }
            int center_deep = min/min_count;
            LOGD("     get center deep  %d", center_deep);
            env->SetIntField(obj, nameFieldId, center_deep);
        }
    }
    // LOGD("=== Callback: Re-enqueue buffer(%p, %d)", frame.userBuffer, frame.bufferSize);
    ASSERT_OK( TYEnqueueBuffer(pData->hDevice, frame.userBuffer, frame.bufferSize) );
}

void trans_width_and_log(int i, float &value, float &factor) {
    LOGD("=== index:%d, value:%f, factor:%f", i, value, factor);
    value = value * factor;
}

int OpenDevice(jint width, jint heigth, jint type) {
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
        
        TY_IMAGE_MODE_LIST image_size = TY_IMAGE_MODE_640x480;
        if (width == 1280) {
            image_size = TY_IMAGE_MODE_1280x960;
        } else if (width == 2592) {
            image_size = TY_IMAGE_MODE_2592x1944;
        } else if (width == 320) {
            image_size = TY_IMAGE_MODE_320x240;
        } else if (width == 160) {
            image_size = TY_IMAGE_MODE_160x120;
        }
        int err;
	    int32_t allComps;
	    ASSERT_OK( TYGetComponentIDs(hDevice, &allComps) );
	    if(allComps & TY_COMPONENT_RGB_CAM){
            // 先读取硬件参数，再进行初始化，默认使用1280大小的参数
            LOGD("=== Read color rectify matrix");
            // 先将图片的大小设置为1280，获取参数
            err = TYSetEnum(hDevice, TY_COMPONENT_RGB_CAM, TY_ENUM_IMAGE_MODE, TY_IMAGE_MODE_1280x960);
            LOGD("err = %d", err);
            ASSERT(err == TY_STATUS_OK || err == TY_STATUS_NOT_PERMITTED);
            // 读取depth参数
  			TY_CAMERA_DISTORTION point_dist;
            TY_CAMERA_INTRINSIC point_intri;
            int intri_length_point = sizeof(point_intri.data) / sizeof(float);
            int dist_length_point = sizeof(point_dist.data) / sizeof(float);

            TY_STATUS ret_depth = TYGetStruct(hDevice, TY_COMPONENT_POINT3D_CAM, TY_STRUCT_CAM_DISTORTION, &point_dist, sizeof(point_dist));
            ret_depth |= TYGetStruct(hDevice, TY_COMPONENT_POINT3D_CAM, TY_STRUCT_CAM_INTRINSIC, &point_intri, sizeof(point_intri));
			for (int i = 0; i < intri_length_point; i++) {
				LOGD("point param intri: %.f", point_intri.data[i]);
			}
			for (int i = 0; i < dist_length_point; i++) {
				LOGD("point param dist: %.f", point_dist.data[i]);
			}

            // 读取RGB参数
            TY_CAMERA_DISTORTION color_dist;
            TY_CAMERA_INTRINSIC color_intri;
            TY_STATUS ret = TYGetStruct(hDevice, TY_COMPONENT_RGB_CAM, TY_STRUCT_CAM_DISTORTION, &color_dist, sizeof(color_dist));
            ret |= TYGetStruct(hDevice, TY_COMPONENT_RGB_CAM, TY_STRUCT_CAM_INTRINSIC, &color_intri, sizeof(color_intri));
            if (ret == TY_STATUS_OK)
            {
                LOGD("=== Read color rectify matrix succ");
                                cb_data.color_intri = color_intri;
                cb_data.color_dist = color_dist;
                float param_factor = width / 1280.0f;
                // 根据图片尺寸转换畸变参数
                trans_width_and_log(0, cb_data.color_intri.data[0], param_factor);
                trans_width_and_log(2, cb_data.color_intri.data[2], param_factor);
                trans_width_and_log(4, cb_data.color_intri.data[4], param_factor);
                trans_width_and_log(5, cb_data.color_intri.data[5], param_factor);

                int intri_length = sizeof(cb_data.color_intri.data) / sizeof(float);
                int dist_length = sizeof(cb_data.color_dist.data) / sizeof(float);
                for (int i = 0; i < intri_length; i++) {
                    LOGD("color param intri: %.f", cb_data.color_intri.data[i]);
                }
                for (int i = 0; i < dist_length; i++) {
                    LOGD("color param dist: %.f", cb_data.color_dist.data[i]);
                }

                cb_data.colorM.create(3, 3, CV_32FC1);
                cb_data.colorD.create(12, 1, CV_32FC1);
                memcpy(cb_data.colorM.data, cb_data.color_intri.data, sizeof(cb_data.color_intri.data));
                memcpy(cb_data.colorD.data, cb_data.color_dist.data, sizeof(cb_data.color_dist.data));
            } else {//let's try  to load from file...
                LOGD("=== Read color rectify matrix failed, use default");
                memset(cb_data.colorD.data, 0, 12 * sizeof(float));
                memset(cb_data.colorM.data, 0, 9 * sizeof(float));
                cb_data.colorM.data[0] = 1117.0f;
                cb_data.colorM.data[4] = 1120.0f;
                cb_data.colorM.data[2] = 654.0f;
                cb_data.colorM.data[5] = 496.0f;
                cb_data.colorD.data[2] = 1.0f;
            }
            LOGD("=== Has RGB camera, open RGB cam");
	        ASSERT_OK( TYEnableComponents(hDevice, TY_COMPONENT_RGB_CAM) );
            err = TYSetEnum(hDevice, TY_COMPONENT_RGB_CAM, TY_ENUM_IMAGE_MODE, image_size);
            // err = TYSetEnum(hDevice, TY_COMPONENT_RGB_CAM, TY_ENUM_IMAGE_MODE, TY_IMAGE_MODE_1280x960);
            LOGD("err = %d", err);
            ASSERT(err == TY_STATUS_OK || err == TY_STATUS_NOT_PERMITTED);
        }

        if (type == 1 || type == 3) {
            LOGD("=== Configure components, open depth cam");
            // int32_t componentIDs = TY_COMPONENT_DEPTH_CAM | TY_COMPONENT_IR_CAM_LEFT;
            int32_t componentIDs = TY_COMPONENT_POINT3D_CAM;// |TY_COMPONENT_DEPTH_CAM;
            ASSERT_OK( TYEnableComponents(hDevice, componentIDs) );

            LOGD("=== Configure feature, set resolution to %dx%d.", width, heigth);
            LOGD("Note: DM460 resolution feature is in component TY_COMPONENT_DEVICE,");
            LOGD("      other device may lays in some other components.");

            // err = TYSetEnum(hDevice, TY_COMPONENT_DEPTH_CAM, TY_ENUM_IMAGE_MODE, image_size);
            // LOGD("err = %d", err);
            // ASSERT(err == TY_STATUS_OK || err == TY_STATUS_NOT_PERMITTED);

            err = TYSetEnum(hDevice, TY_COMPONENT_POINT3D_CAM, TY_ENUM_IMAGE_MODE, image_size);
            LOGD("err = %d", err);
            ASSERT(err == TY_STATUS_OK || err == TY_STATUS_NOT_PERMITTED);
        }

        //LOGD("=== Enable rgb undistort");
        //TYSetBool(hDevice, TY_COMPONENT_RGB_CAM, TY_BOOL_UNDISTORTION, true);
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
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_TyNativeUtils_OpenDevice(JNIEnv* env, jobject thiz, jint width, jint heigth);
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_TyNativeUtils_OpenDeviceCustom(JNIEnv* env, jobject thiz, jint width, jint heigth, jint type);
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_TyNativeUtils_CloseDevice(JNIEnv* env, jobject thiz);
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_TyNativeUtils_StartDevice(JNIEnv* env, jobject thiz);
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_TyNativeUtils_StopDevice(JNIEnv* env, jobject thiz);
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_TyNativeUtils_FetchData(JNIEnv* env, jobject thiz, jlong depthMatAddr, jlong rgbMatAddr, jlong pointMatAddr);
}

JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_TyNativeUtils_OpenDevice(JNIEnv* env, jobject thiz, jint width, jint heigth)
{
    return OpenDevice(width, heigth, 1);
}

JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_TyNativeUtils_OpenDeviceCustom(JNIEnv* env, jobject thiz, jint width, jint heigth, jint type)
{
    return OpenDevice(width, heigth, type);
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

JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_TyNativeUtils_FetchData(JNIEnv* env, jobject thiz, jlong depthMatAddr, jlong rgbMatAddr, jlong pointMatAddr)
{
	LOGD("Fetch Data");
    //exit_main = false;
    TY_FRAME_DATA frame;
    int err = TYFetchFrame(hDevice, &frame, -1);
    LOGD("err = %d", err);
    if( err != TY_STATUS_OK ){
        //LOGD("... Drop one frame");
        return err;
    }
    return frameHandler8X(frame, &cb_data, depthMatAddr, rgbMatAddr, pointMatAddr, env, thiz);
}

