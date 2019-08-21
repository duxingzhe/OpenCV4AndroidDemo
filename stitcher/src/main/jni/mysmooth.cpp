//
// Created by Luxuan on 2019/8/21.
//

#include <opencv/highgui.h>
#include <opencv/cv.h>
#include <opencv/cvaux.h>

void mySmooth(IplImage* img1, IplImage* img2)
{
    cvSmooth(img1, img2, CV_MEDIAN, 3, 0, 0, 0);
}