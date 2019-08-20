//
// Created by Luxuan on 2019/8/20.
//

#include <opencv/highgui.h>
#include <opencv/cv.h>
#include <opencv/cvaux.h>

void myConnect(IplImage* img_src, IplImage* img_Clone)
{
    CvSeq* contour=NULL;
    double minarea=400.0;
    double temparea=0.0;
    CvMemStorage* storage=cvCreateMemStorage(0);
    uchar* pp;
    IplImage* img_dst=cvCreateImage(cvGetSize(img_src), IPL_DEPTH_8U, 1);

    CvScalar color=cvScalar(255, 0, 0);
    CvContourScanner scanner=NULL;
    scanner=cvStartFindContours(img_src, storage, sizeof(CvContour), CV_RETR_CCOMP, CV_CHAIN_APPROX_NONE, cvPoint(0,0));

    CvRect rect;
    while(contour=cvFindNextContour(scanner))
    {
        temparea=fabs(cvContourArea(contour));
        rect=cvBoundingRect(contour, 0);
        if(temparea<minarea)
        {
            pp=(uchar*)(img_Clone->imageData+img_Clone->widthStep*(rect.y+rect.height/2)+rect.x+rect.width/2);
            if(pp[0]==255)
            {
                for(int y=rect.y;y<rect.y+rect.height;y++)
                {
                    for(int x=rect.x;x<rect.x+rect.width;x++)
                    {
                        pp=(uchar*)(img_Clone->imageData+img_Clone->widthStep*y+x);

                        if(pp[0]==255)
                        {
                            pp[0]=0;
                        }
                    }
                }
            }
        }
    }
}