#define _CRT_SECURE_NO_WARNINGS

#define LOG_TAG "rooxin_jni_gpio"
#include <android/log.h>
#include <string.h>
#include <jni.h>
#include <android/log.h>
#include <stdlib.h>
#include <stdio.h>
#include <fcntl.h>
// #include <hardware/hardware.h>
// #include <termios.h>
#include <errno.h>
// #include <pthread.h>
// #include <cutils/log.h>
// #include <linux/delay.h>
// #include <pthread.h>
#include <dlfcn.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <string>
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, fmt, ##args)

int fd;

extern "C" {
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_GPIOControl_open(JNIEnv* env, jobject thiz);
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_GPIOControl_read(JNIEnv* env, jobject thiz, jint value);
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_GPIOControl_write(JNIEnv* env, jobject thiz, jint gpio, jint value);
JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_GPIOControl_close(JNIEnv* env, jobject thiz);

}

JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_GPIOControl_open(JNIEnv* env, jobject thiz)
{
    fd = open("/dev/c52add",O_RDWR|O_NOCTTY|O_NONBLOCK);
    if(fd<0){
        LOGD("/dev/c52add open  failed\n");
    }
    LOGD("-----open----fd=%d",fd);
    return fd;
}

JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_GPIOControl_read(JNIEnv* env, jobject thiz, jint value)
{
    int err = read(fd,NULL,(char)value);
    return err;
}

JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_GPIOControl_write(JNIEnv* env, jobject thiz, jint gpio, jint value)
{
    int result=-1;
	int i=0;
	jint length;
    jchar *array;
    jchar writebuff[16];
	array = env->GetCharArrayElements((jcharArray)value,NULL);
	length = env->GetArrayLength((jcharArray)value);
	for(i;i<length;i++){
		writebuff[i]=array[i];
		LOGD("%d=%c",i,writebuff[i]);
	}
	//memset(writebuff,'a',sizeof(writebuff));
	result = write(fd,writebuff,16);
    return result;
}

JNIEXPORT jint JNICALL Java_com_dnion_app_android_injuriesapp_camera_1tool_native_1utils_GPIOControl_close(JNIEnv* env, jobject thiz)
{
    close(fd);
}
