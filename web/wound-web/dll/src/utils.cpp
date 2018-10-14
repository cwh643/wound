#include "stdafx.h"
#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <vector>
#define LOG_TAG "rooxin_jni"
#include <string.h>
#include "common.hpp"

//Ax+by+cz=D  
int cvFitPlane(cv::Mat* points, cv::Mat* plane){  
    LOGD("start calc plane point:%ld, plane:%ld", (long)points, (long)plane);
    // Estimate geometric centroid.  
    //LOGD("calc plane1");
    CvMat p_tmp(*points);
    CvMat *points_1 = &p_tmp;
    //LOGD("calc plane2");
    int nrows = points->rows;  
    int ncols = points->cols;  
    int type = points->type();  
    CvMat* centroid = cvCreateMat(1, ncols, type);  
    cvSet(centroid, cvScalar(0));  
    LOGD("calc plane ponits rows:%d, cols:%d, type:%d", nrows, ncols, type);
    LOGD("calc plane planeMat rows:%d, cols:%d, type:%d", plane->rows, plane->cols, plane->type());
    for (int c = 0; c<ncols; c++){  
        for (int r = 0; r < nrows; r++)  
        {  
            // LOGD("calc plane:%f", points->at<float>(r, c));
            centroid->data.fl[c] += points->at<float>(r, c);  
        }  
        centroid->data.fl[c] /= nrows;  
    }  
    //LOGD("calc plane4");
    // Subtract geometric centroid from each point.  
    CvMat* points2 = cvCreateMat(nrows, ncols, type);  
    for (int r = 0; r<nrows; r++)  
        for (int c = 0; c<ncols; c++)  
            points2->data.fl[ncols*r + c] = points->at<float>(r, c) - centroid->data.fl[c];  
    //LOGD("calc plane5");
    // Evaluate SVD of covariance matrix.  
    CvMat* A = cvCreateMat(ncols, ncols, type);  
    CvMat* W = cvCreateMat(ncols, ncols, type);  
    CvMat* V = cvCreateMat(ncols, ncols, type);  
    //LOGD("calc plane6");
    cvGEMM(points2, points_1, 1, NULL, 0, A, CV_GEMM_A_T);  
    cvSVD(A, W, NULL, V, CV_SVD_V_T);  
    // Assign plane coefficients by singular vector corresponding to smallest singular value.  
    //LOGD("calc plane7");
    plane->at<float>(0, ncols) = 0.0f;  
    for (int c = 0; c<ncols; c++){  
        plane->at<float>(0, c) = V->data.fl[ncols*(ncols - 1) + c];  
        plane->at<float>(0, ncols) += plane->at<float>(0, c) * centroid->data.fl[c];  
    }  
    LOGD("calc plane a:%f,b:%f,c:%f,d:%f", plane->at<float>(0, 0), plane->at<float>(0, 1),plane->at<float>(0, 2), plane->at<float>(0, 3));
    // Release allocated resources.  
    cvReleaseMat(&centroid);  
    cvReleaseMat(&points2);  
    cvReleaseMat(&A);  
    cvReleaseMat(&W);  
    cvReleaseMat(&V);  
    return 0;
} 

extern "C" {
    JNIEXPORT jint JNICALL Java_com_iteye_chenwh_wound_native_1utils_CommonNativeUtils_cvFitPlane(JNIEnv* env, jobject thiz, jlong points, jlong  plane);
}

JNIEXPORT jint JNICALL Java_com_iteye_chenwh_wound_native_1utils_CommonNativeUtils_cvFitPlane(JNIEnv* env, jobject thiz, jlong points, jlong plane) {
    return cvFitPlane((cv::Mat*)points, (cv::Mat*)plane);
}
