//
// Created by Luxuan on 2019/8/21.
//

#include <opencv/highgui.h>
#include <opencv/cv.h>
#include <opencv/cvaux.h>

void myErode(IplImage* src, IplImage* dst, int a)
{
    IplConvKernel* element=cvCreateStructuringElementEx(a, 1, a/2, 0, CV_SHAPE_RECT, NULL);
    cvErode(src, dst, element, 1);
}