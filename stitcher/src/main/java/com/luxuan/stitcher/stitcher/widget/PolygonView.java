package com.luxuan.stitcher.stitcher.widget;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

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
}
