package com.luxuan.stitcher.stitcher.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.luxuan.stitcher.R;
import com.luxuan.stitcher.stitcher.Util.OpenCVHelper;
import com.luxuan.stitcher.stitcher.Util.Utils;
import com.luxuan.stitcher.stitcher.widget.PolygonView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class PolygonViewScreenActivity extends AppCompatActivity {

    private ImageView imageView;
    private Uri uri;
    private PolygonView polygonView;
    private Uri imageUriFromGallery, imageUriFromCamera, test_uri, imageUriFromBatch;
    private Bitmap bitmap, op, op2, tempBitmap;
    private int status=0;
    private FileOutputStream fos;
    private float persWidth, persHeight;
    private Bitmap original, scaledBitmap;
    private FrameLayout sourceFrame;
    private LinearLayout graLayout, bwLayout, enhancedLayout, lightenLayout, normalLayout, colorLayout;
    private ImageView gray, bw, enhanced, lighten, normal;
    private TextView tGray, tBW, tEnhanced, tLighten, tNormal;
    private static String test;
    private Uri test1;
    private Matrix matrix;
    private Map<Integer, PointF> pointFs;
    private ProgressBar grievanceProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=(ImageView)findViewById(R.id.img1);
        polygonView=(PolygonView)findViewById(R.id.polygonView);
        sourceFrame=(FrameLayout)findViewById(R.id.sourceFrame);
        grievanceProgressBar=(ProgressBar)findViewById(R.id.grievanceProgressBar);

        ImageView submit=(ImageView)findViewById(R.id.submit);
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
                Bitmap rotatedBitmap=rotateImage(((BitmapDrawable)imageView.getDrawable()).getBitmap(), -90);
                imageView.setImageBitmap(rotatedBitmap);
            }
        })

        ImageView rotateRight=(ImageView)findViewById(R.id.rotateright);
        rotateRight.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Bitmap rotatedBitmap=rotateImage(((BitmapDrawable)imageView.getDrawable()).getBitmap(), 90);
                imageView.setImageBitmap(rotatedBitmap);
            }
        });

        sourceFrame=(FrameLayout)findViewById(R.id.sourceFrame);
        sourceFrame.post(new Runnable(){

            @Override
            public void run(){
                original=getBitmap();
                original=rotateImage(original, 90);

                if(original!=null){
                    setBitmap(original);
                }
            }
        });

        submit.setOnClickListener(new ScanButtonClickListener());
    }

    private Bitmap getBitmap(){
        Uri uri=getUri();
        try{
            bitmap= Utils.getBitmap(PolygonViewScreenActivity.this, uri);
            getContentResolver().delete(uri, null, null, null);
            return bitmap;
        }catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }

    private Uri getUri(){
        imageUriFromGallery=getIntent().getParcelableExtra("imageTest1");
        imageUriFromCamera=getIntent().getParcelableExtra("imageTest");
        imageUriFromBatch=getIntent().getParcelableExtra("image2");

        if(imageUriFromGallery!=null&&imageUriFromBatch==null){
            uri=getIntent().getParcelableExtra("imageTest1");
            Log.i("uri test gallery", uri.toString());
        }else {
            uri=getIntent().getParcelableExtra("image2");
            Log.i("uri test cameraBatch", uri.toString());
        }

        test=uri.toString();
        test1=uri;
        Log.i("test test", test.toString());
        Log.i("test test2", test1.toString());

        return uri;
    }

    private void setBitmap(Bitmap original){
        scaledBitmap=scaledBitmap(original, sourceFrame.getWidth(), sourceFrame.getHeight());
        imageView.setImageBitmap(scaledBitmap);
        new EdgeAsyncTask(scaledBitmap).execute();
    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height){
        Matrix matrix=new Matrix();
        matrix.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap rotateImage(Bitmap bitmap, float angle){
        Matrix matrix=new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private boolean isScanPointsValid(Map<Integer, PointF> points){
        return points.size()==4;
    }

    private class ScanButtonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view){
            if(status==0){
                Map<Integer, PointF> points=polygonView.getPoints();
                if(isScanPointsValid(points)){
                    new ScanAyncTask(points).execute();
                }else{
                    Toast.makeText(PolygonViewScreenActivity.this, "error,", Toast.LENGTH_SHORT).show();
                }
            }else if(status==1){
                Uri uri=Utils.getUri(PolygonViewScreenActivity.this, op2);

                Log.i("testUri", uri.toString());
                Intent intent=new Intent(PolygonViewScreenActivity.this, ColorImageActivity.class);
                intent.putExtra("image", uri);
                startActivity(intent);
                finish();
            }
        }
    }

    private class ScanAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        private Map<Integer, PointF> points;

        public ScanAsyncTask(Map<Integer, PointF> points){
            this.points=points;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        public Bitmap doInBackground(Void... params){
            runOnUiThread(new Runnable(){

                @Override
                public void run(){
                    try{
                        original=Utils.getBitmap(PolygonViewScreenActivity.this, uri);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                    op=getScannedBitmap(original, points);

                    Log.d("MainActivity", "Sizes are"+original.getWidth()+" "+original.getHeight());
                    if(persWidth>persHeight){
                        op2=Bitmap.createScaledBitmap(op, 1600, 1000, false);
                    }
                    imageView.setImageBitmap(op2);

                    status=1;
                }
            });

            return original;
        }

        @Override
        public void onPostExecute(Bitmap bitmap){
            super.onPostExecute(bitmap);
        }
    }

    private Bitmap getScannedBitmap(Bitmap original, Map<Integer, PointF> points){

        int width=original.getWidth();
        int height=original.getHeight();
        float xRatio=(float)original.getWidth()/imageView.getWidth();
        float yRatio=(float)original.getHeight()/imageView.getHeight();

        Log.d("width", width+"");
        Log.d("height", height+"");
        Log.d("xRatio", xRatio+"");
        Log.d("yRatio", yRatio+"");

        int[] newPoints={0, 0, 0, 0, 0, 0, 0, 0};
        newPoints[0]=(int)((points.get(0).x)*xRatio);
        newPoints[1]=(int)((points.get(0).y)*yRatio);
        newPoints[2]=(int)((points.get(1).x)*xRatio);
        newPoints[3]=(int)((points.get(1).y)*yRatio);
        newPoints[4]=(int)((points.get(2).x)*xRatio);
        newPoints[5]=(int)((points.get(2).y)*yRatio);
        newPoints[6]=(int)((points.get(3).x)*xRatio);
        newPoints[7]=(int)((points.get(3).y)*yRatio);

        Log.d("OpenCV", "hello stated");
        Log.d("OpenCV", String.valueOf(newPoints[0]));
        Log.d("OpenCV", String.valueOf(newPoints[1]));
        Log.d("OpenCV", String.valueOf(newPoints[2]));
        Log.d("OpenCV", String.valueOf(newPoints[3]));
        Log.d("OpenCV", String.valueOf(newPoints[4]));
        Log.d("OpenCV", String.valueOf(newPoints[5]));
        Log.d("OpenCV", String.valueOf(newPoints[6]));
        Log.d("OpenCV", String.valueOf(newPoints[7]));

        persWidth=max(abs(newPoints[2]-newPoints[0]), abs(newPoints[4]-newPoints[0]));
        persHeight=max(abs(newPoints[3]-newPoints[1]), abs(newPoints[5]-newPoints[1]));

        int[] pixels=new int[width*height];
        original.getPixels(pixels, 0, width, 0, 0, width, height);
        Log.d("OpenCV4Android", "Came here");
        int[] resultPixels= OpenCVHelper.perspective(pixels, newPoints, width, height);
        Log.d("OpenCV4Android", "CheckPoint");
        Bitmap result=Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        result.setPixels(resultPixels, 0, width, 0, 0, width, height);
        polygonView.setVisibility(View.GONE);
        return result;
    }
}
