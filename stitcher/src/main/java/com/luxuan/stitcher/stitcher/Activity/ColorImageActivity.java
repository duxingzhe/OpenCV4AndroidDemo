package com.luxuan.stitcher.stitcher.Activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.luxuan.stitcher.R;
import com.luxuan.stitcher.stitcher.SaveImageToStorageAsyncTask;
import com.luxuan.stitcher.stitcher.Util.OpenCVHelper;
import com.luxuan.stitcher.stitcher.Util.Utils;

import java.io.FileOutputStream;
import java.io.IOException;

public class ColorImageActivity extends AppCompatActivity {

    private FrameLayout sourceFrame;
    private LinearLayout grayLayout, bwLayout, enhancedLayout, lightenLayout, normalLayout, colorLayout;
    private ImageView grayImage, bwImage, enhancedImage, lightenImage, normalImage, actualImage;
    private TextView tvGray, tvBW, tvEnhanced, tvLighten, tvNormal;
    private Uri uri;

    private Bitmap original, bitmap;
    private FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_scanned_image);

        actualImage=(ImageView)findViewById(R.id.img11);
        colorLayout=(LinearLayout)findViewById(R.id.layout_color);

        grayLayout=(LinearLayout)findViewById(R.id.llgray);
        bwLayout=(LinearLayout)findViewById(R.id.llbw);
        enhancedLayout=(LinearLayout)findViewById(R.id.llenhanced);
        lightenLayout=(LinearLayout)findViewById(R.id.lllighten);
        normalLayout=(LinearLayout)findViewById(R.id.llnormal);

        grayImage=(ImageView)findViewById(R.id.myImageViewGray);
        bwImage=(ImageView)findViewById(R.id.myImageViewLighten);
        enhancedImage=(ImageView)findViewById(R.id.myImageViewEnhance);
        lightenImage=(ImageView)findViewById(R.id.myImageViewBW);
        normalImage=(ImageView)findViewById(R.id.myImageViewNormal);

        tvGray=(TextView)findViewById(R.id.gray);
        tvBW=(TextView)findViewById(R.id.bw);
        tvEnhanced=(TextView)findViewById(R.id.enhance);
        tvLighten=(TextView)findViewById(R.id.light);
        tvNormal=(TextView)findViewById(R.id.normal);

        fab=(FloatingActionButton)findViewById(R.id.submit);

        uri=getIntent().getParcelableExtra("image");

        original=getBitmap();

        actualImage.setImageBitmap(original);

        fab.setOnClickListener(new ScanButtonClickListener());

        grayLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Bitmap transfer= OpenCVHelper.getGrayBitmapP(original);
                actualImage.setImageBitmap(transfer);
            }
        });

        bwLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Bitmap transfer= OpenCVHelper.getBlackWhiteBitmapP(original);
                actualImage.setImageBitmap(transfer);
            }
        });

        enhancedLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Bitmap transfer= OpenCVHelper.getMagicBitmap(original);
                actualImage.setImageBitmap(transfer);
            }
        });

        lightenLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Bitmap transfer= OpenCVHelper.getLightedBitmap(original);
                actualImage.setImageBitmap(transfer);
            }
        });

        bwLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Bitmap transfer= original;
                actualImage.setImageBitmap(transfer);
            }
        });
    }

    private class ScanButtonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view){
            popup_request(((BitmapDrawable)actualImage.getDrawable()).getBitmap());
        }
    }

    private void popup_request(Bitmap bitmap){
        int i=(int)(System.currentTimeMillis()/1000);
        String name=String.valueOf(i);
        String val=name;
        new SaveImageToStorageAsyncTask(val, bitmap).execute();
        Toast.makeText(this, "Image saved", Toast.LENGTH_LONG).show();
        finish();
    }

    private Bitmap getBitmap(){
        Uri uri=getUri();
        try{
            bitmap= Utils.getBitmap(this,uri);
            getContentResolver().delete(uri, null, null);
            return bitmap;
        }catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }

    private Uri getUri(){
        uri=getIntent().getParcelableExtra("image");
        return uri;
    }
}
