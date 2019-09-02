package com.luxuan.stitcher.stitcher.widget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.luxuan.stitcher.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PolygonView extends FrameLayout {

    private Context mContext;
    private Paint paint;
    private ImageView pointer1;
    private ImageView pointer2;
    private ImageView pointer3;
    private ImageView pointer4;
    private ImageView midPointer13;
    private ImageView midPointer12;
    private ImageView midPointer34;
    private ImageView midPointer24;

    public PolygonView(Context context){
        super(context);
        mContext=context;
        init();
    }

    public PolygonView(Context context, AttributeSet attrs){
        super(context, attrs);
        mContext=context;
        init();
    }

    public PolygonView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        mContext=context;
        init();
    }

    private void init(){
        pointer1=getImageView(0,0);
        pointer2=getImageView(getWidth(), 0);
        pointer3=getImageView(0, getHeight());
        pointer4=getImageView(getWidth(), getHeight());

        midPointer13=getImageView(0, getHeight()/2);
        midPointer13.setOnTouchListener(new MidPointTouchListenerImpl(pointer1, pointer3));

        midPointer12=getImageView(getWidth()/2, 0);
        midPointer12.setOnTouchListener(new MidPointTouchListenerImpl(pointer1, pointer2));

        midPointer34=getImageView(getWidth()/2, getHeight());
        midPointer34.setOnTouchListener(new MidPointerTouchListenerImpl(pointer3, pointer4));

        midPointer24=getImageView(getWidth(), getHeight()/2);
        midPointer24.setOnTouchListener(new MidPointerTouchLitenerImpl(pointer2, pointer4));

        addView(pointer1);
        addView(pointer2);
        addView(midPointer13);
        addView(midPointer12);
        addView(midPointer34);
        addView(midPointer24);
        addView(pointer3);
        addView(pointer4);

        initPaint();
    }

    @Override
    public void attachViewToParent(View child, int index, ViewGroup.LayoutParams params){
        super.attachViewToParent(child, index, params);
    }

    private void initPaint(){
        paint=new Paint();
        paint.setColor(getResources().getColor(R.color.blue));
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
    }

    public Map<Integer, PointF> getPoints(){
        List<PointF> points=new ArrayList<>();
        points.add(new PointF(pointer1.getX(), pointer1.getY()));
        points.add(new PointF(pointer2.getX(), pointer2.getY()));
        points.add(new PointF(pointer3.getX(), pointer3.getY()));
        points.add(new PointF(pointer4.getX(), pointer4.getY()));

        return getOrderedPoints(points);
    }

    public Map<Integer, PointF> getOrderedPoints(List<PointF> points){
        PointF centerPoint=new PointF();
        int size=points.size();
        for(PointF pointF: points){
            centerPoint.x+=pointF.x/size;
            centerPoint.y+=pointF.y/size;
        }

        Map<Integer, PointF> orderedPoints=new HashMap<>();
        for(PointF pointF: points){
            int index=-1;
            if(pointF.x<centerPoint.x&&pointF.y<centerPoint.y){
                index=0;
            }else if(pointF.x>centerPoint.x&&pointF.y<centerPoint.y){
                index=1;
            }else if(pointF.x<centerPoint.x&&pointF.y>centerPoint.y){
                index=2;
            }else if(pointF.x>centerPoint.x&&pointF.y>centerPoint.y){
                index=3;
            }

            orderedPoints.put(index, pointF);
        }

        return orderedPoints;
    }
}
