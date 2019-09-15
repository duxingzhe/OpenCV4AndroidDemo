package com.luxuan.stitcher.stitcher.Activity;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.luxuan.stitcher.R;
import com.luxuan.stitcher.stitcher.Util.Utils;
import com.luxuan.stitcher.stitcher.widget.PolygonView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import static com.luxuan.stitcher.stitcher.Activity.PolygonViewScreenActivity.rotateImage;

public class MainActivity extends AppCompatActivity {

    private ImageView iv_show_img;
    private PolygonView polygonView;
    private String imgDecodeStr;
    private Button scanButton;
    private FrameLayout sourceFrameLayout;
    private Uri imageUri;
    private Bitmap bitmap, op, op2;
    private int status=0;
    private FileOutputStream fos;
    private float perWidth, perHieght;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_show_img=(ImageView)findviewById(R.id.iv_show_img);

        ImageView submitImg=(ImageView)findViewById(R.id.submit);
        submitImg.setOnClickListener(new ScanButtonClickListener());
        ImageView cancel=(ImageView)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                onBackPressed();
            }
        });

        ImageView rotateLeft=(ImageView)findViewById(R.id.rotateleft);
        rotateLeft.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Bitmap rotatedBitmap=rotateImage(((BitmapDrawable)iv_show_img.getDrawable()).getBitmap(), -90);
                iv_show_img.setImageBitmap(rotatedBitmap);
            }
        });

        ImageView rotateRight=(ImageView)findViewById(R.id.rotateRight);
        rotateRight.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Bitmap rotatedBitmap=rotateImage(((BitmapDrawable)iv_show_img.getDrawable()).getBitmap(), 90);
                iv_show_img.setImageBitmap(rotatedBitmap);
            }
        });

        polygonView=(PolygonView)findViewById(R.id.polygonView);
        final Uri uri=getIntent().getParcelableExtra("imageUri");

        try{
            bitmap= Utils.getBitmap(MainActivity.this, uri);
            getContentResolver().delete(uri,null, null);
            sourceFrameLayout=(FrameLayout)findViewById(R.id.sourceFrame);
            sourceFrameLayout.post(new Runnable(){

                @Override
                public void run(){
                    ExifInterface exifInterface=null;
                    Bitmap scaledBitmap=scaledBitmap(bitmap, sourceFrameLayout.getWidth(), sourceFrameLayout.getHeight());
                    Bitmap tempBitmap=((BitmapDrawable)iv_show_img.getDrawable()).getBitmap();
                    Map<Integer, PointF> pointFs=getEdgePoints(tempBitmap);
                    polygonView.setPoints(pointFs);
                    polygonView.setVisibility(View.VISIBLE);
                    int padding=(int)getResources().getDimension(R.dimen.scanPadding);
                    FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(tempBitmap.getWidth()+2*padding, tempBitmap.getHeight()+2*padding);
                    layoutParams.gravity= Gravity.CENTER;
                    polygonView.setLayoutParams(layoutParams);
                }
            });
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
