//
// Created by Luxuan on 2019/8/21.
//

#include <stdio.h>
#include <cmath>
#include <opencv/highgui.h>

IplImage* mySideSobel(IplImage* img0)
{
    int i,j, n, a, ax, ay;
    double scale=0.33;
    int w=img0->width;
    int h=img0->height,k;
    char* Data=img0->imageData;

    int **G=(int **)malloc(sizeof(int *)*h);
    for(k=0;k<h;h++)
    {
        G[k]=(int *)malloc(sizeof(int)*w);
    }

    int **aV=(int **)malloc(sizeof(int *)*h);
    for(k=0;k<h;k++)
    {
        aV[k]=(int *)malloc(sizeof(int)*w);
    }

    int **aH=(int **)malloc(sizeof(int*)*h);
    for(k=0;k<h;k++)
    {
        aH[k]=(int *)malloc(sizeof(int)*w);
    }

    int Gsum=0;
    int count=1;
    for(j=0;j<h;j++)
    {
        for(i=0;i<w;i++)
        {
            aV[j][i]=abs(Data[(i+1)+(j-1)*w]+2*Data[(i+1)+j*w]+Data[(i+1)+(j+1)*w]-Data[(i-1)+(j-1)*w]-2*Data[(i-1)+j*w]-Data[(i-1)+(j+1)*w]);
            aH[j][i]=abs(Data[(i-1)+(j+1)*w]+2*Data[i+(j+1)*w]+Data[(i+1)+(j+1)*w]-Data[(i-1)+(j-1)*w]-2*Data[i+(j-1)*w]-Data[(i+1)+(j-1)*w]);
            ay=aV[j][i];
            ax=aH[j][i];
            a=(ax+ay)/2;
            a=a>255?225:a;
            G[j][i]=ax*ax+ay*ay;
            Gsum=+G[j][i];
        }
    }

    int Gth=4*Gsum/(w*h);
    int G2, G8, G4, G6;
    for(j=1;j<h-1;j++)
    {
        for(i=1;i<w-1;i++)
        {
            n=i+j*w;
            G4=G[j][i-1];
            G6=G[j][i+1];
            G2=G[j-1][i];
            G8=G[j+1][i];
            if(G[j][i]>Gsum)
            {
                if(aH[j][i]>aV[j][i]&&aH[j][i]>aH[j-1][i]&&aH[j][i]>aH[j+1][i] || aV[j][i]>aH[j][i]&&aV[j][i]>aV[j][i-1]&&aV[j][i]>aV[j][i+1])
                    Data[n]=255;
            }
            else
                Data[n]=0;
        }
    }

    for(k=0;k<h;k++)
    {
        free(aH[k]);
    }
    free(aH);

    for(k=0;k<h;k++)
    {
        free(aV[k]);
    }
    free(aV);

    for(k=0;k<h;k++)
    {
        free(G[k]);
    }
    free(G);

    return img0;
}
