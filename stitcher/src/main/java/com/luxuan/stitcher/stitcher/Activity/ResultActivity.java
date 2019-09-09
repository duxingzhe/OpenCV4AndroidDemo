package com.luxuan.stitcher.stitcher.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.luxuan.stitcher.R;
import com.luxuan.stitcher.stitcher.Util.OpenCVHelper;

public class ResultActivity extends AppCompatActivity {

    private ImageView image;
    private Bitmap op, original;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_layout);
        image=(ImageView)findViewById(R.id.scannedImage);
        Button black_white=(Button)findViewById(R.id.BWMode);

        Intent intent=getIntent();
        Bitmap bitmap=(Bitmap)intent.getParcelableExtra("BitmapImage");
        Toast.makeText(ResultActivity.this, bitmap.toString(), Toast.LENGTH_SHORT).show();

        black_white.setOnClickListener(new BWButtonClickListener());
    }

    private Bitmap JPGtoRGB8888(Bitmap img){
        Bitmap result=null;

        int numPixels=img.getWidth()*img.getHeight();
        int[] pixels=new int[numPixels];

        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        result=Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        result.getPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());

        return result;
    }
    private class BWButtonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view){
            colorChange();
        }
    }

    private void colorChange(){
        op= OpenCVHelper.getMagicColorBitmap(original);
        image.setImageBitmap(op);
    }

    static{
        System.loadLibrary("OpenCV");
    }
}
