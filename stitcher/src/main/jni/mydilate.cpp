//
// Created by Luxuan on 2019/8/20.
//

#include <opencv/highgui.h>
#include <opencv/cv.h>
#include <opencv/cvaux.h>

void myDilate(IplImage* src, IplImage* dst, int a)
{
    IplConvKernel* element=cvCreateStructuringElementEx(a, 1, a/2, 0, CV_SHAPE_RECT, NULL);
    cvDilate(src, dst, element, 1);
}