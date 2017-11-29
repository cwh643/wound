#include <jni.h>
#include <stdio.h>

extern "C" {
JNIEXPORT void JNICALL Java_com_rooxin_camera_CamportActivity_OpenDevice(JNIEnv* env, jobject thiz);
JNIEXPORT void JNICALL Java_com_rooxin_camera_CamportActivity_CloseDevice(JNIEnv* env, jobject thiz);
JNIEXPORT void JNICALL Java_com_rooxin_camera_CamportActivity_FetchData(JNIEnv* env, jobject thiz,jlong matAddr);
}

JNIEXPORT void JNICALL Java_com_rooxin_camera_CamportActivity_OpenDevice(JNIEnv* env, jobject thiz)
{
    printf("opendevice \n");
}

JNIEXPORT void JNICALL Java_com_rooxin_camera_CamportActivity_CloseDevice(JNIEnv* env, jobject thiz)
{
    printf("closedevice \n");
}

JNIEXPORT void JNICALL Java_com_rooxin_camera_CamportActivity_FetchData(JNIEnv* env, jobject thiz, jlong matAddr)
{
	printf("device:%d \n", matAddr);
}


