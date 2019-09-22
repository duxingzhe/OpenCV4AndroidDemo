package com.luxuan.stitcher.stitcher.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.luxuan.stitcher.R;
import com.luxuan.stitcher.stitcher.Util.OpenCVHelper;
import com.luxuan.stitcher.stitcher.Util.Utils;
import com.luxuan.stitcher.stitcher.widget.PolygonView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.luxuan.stitcher.stitcher.Activity.PolygonViewScreenActivity.rotateImage;
import static java.lang.Math.abs;
import static java.lang.Math.max;

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
    private float persWidth, persHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_show_img=(ImageView)findViewById(R.id.iv_show_img);

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

        ImageView rotateRight=(ImageView)findViewById(R.id.rotateright);
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

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        finish();
        super.onBackPressed();
    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height){
        Matrix matrix=new Matrix();
        matrix.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private Map<Integer, PointF> getEdgePoints(Bitmap tempBitmap){
        List<PointF> pointFs=getContourEdgePoints(tempBitmap);
        Map<Integer, PointF> orderedPoints=orderedValidEdgePoints(tempBitmap, pointFs);
        return orderedPoints;
    }

    private List<PointF> getContourEdgePoints(Bitmap tempBitmap){
        int w=tempBitmap.getWidth(), h=tempBitmap.getHeight();
        int[] pixels=new int[w*h];
        tempBitmap.getPixels(pixels, 0, w, 0, 0, w, h);

        int[] points= OpenCVHelper.getBoxPoints(pixels, w, h);
        float x1=points[0];
        float y1=points[1];
        float x2=points[2];
        float y2=points[3];

        float x3=points[4];
        float y3=points[5];
        float x4=points[6];
        float y4=points[7];

        List<PointF> pointFs=new ArrayList<>();
        pointFs.add(new PointF(x1, y1));
        pointFs.add(new PointF(x2, y2));
        pointFs.add(new PointF(x3, y3));
        pointFs.add(new PointF(x4, y4));
        return pointFs;
    }

    private Map<Integer, PointF> orderedValidEdgePoints(Bitmap tempBitmap, List<PointF> pointFs){
        Map<Integer, PointF> orderedPoints=polygonView.getOrderedPoints(pointFs);
        if(!polygonView.isValidShape(orderedPoints)){
            orderedPoints=getOutlinePoints(tempBitmap);
        }
        return orderedPoints;
    }

    private Map<Integer, PointF> getOutlinePoints(Bitmap tempBitmap){
        Map<Integer, PointF> outlinePoints=new HashMap<>();
        outlinePoints.put(0, new PointF(0, 0));
        outlinePoints.put(1, new PointF(tempBitmap.getWidth(), 0));
        outlinePoints.put(2, new PointF(0, tempBitmap.getHeight()));
        outlinePoints.put(3, new PointF(tempBitmap.getWidth(), tempBitmap.getHeight()));

        return outlinePoints;
    }

    private void popup_request(final Bitmap bitmap){
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);

        View promptView=layoutInflater.inflate(R.layout.popup_layout, null);

        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(MainActivity.this);

        alertDialogBuilder.setView(promptView);

        final EditText inputEditText=(EditText)promptView.findViewById(R.id.userInput);

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int id){
                saveToInternalStorage(bitmap, inputEditText.getText().toString());
                Toast.makeText(MainActivity.this, "Image saved to "+ inputEditText.getText().toString(), Toast.LENGTH_LONG).show();
                finish();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int id){
                dialog.cancel();
            }
        });

        AlertDialog alertDialog=alertDialogBuilder.create();

        alertDialog.show();
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String string){
        File directory=new File(Environment.getExternalStorageDirectory(), "DocumentScanner" + File.separator + string);

        if(!directory.exists()){
            directory.mkdirs();
            File directoryThumb=new File(Environment.getExternalStorageDirectory(), "DocumentScanner"+File.separator+"thumbnails");
            if(!directoryThumb.exists()){
                directoryThumb.mkdirs();
            }
            File myThumbPath=new File(directoryThumb, string + ".jpg");

            try{
                FileOutputStream fosThumb=new FileOutputStream(myThumbPath);
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fosThumb);
                fosThumb.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        int time=(int)System.currentTimeMillis();
        Timestamp tsTemp=new Timestamp(time);
        String ts=tsTemp.toString();
        File myPath=new File(directory, ts+".jpg");

        try{
            fos=new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return directory.getAbsolutePath();
    }

    private class ScanButtonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view){
            if(status==0){
                Map<Integer, PointF> points=polygonView.getPoints();
                Log.d("OpenCV4Android", String.valueOf(points.size()));
                op=getScannedBitmap(bitmap, points);

                Log.d("MainActivity", "Sizes are" +bitmap.getWidth()+" "+bitmap.getHeight());
                if(persWidth>persHeight){
                    op2=Bitmap.createScaledBitmap(op, 1600, 1000, false);
                }else{
                    op2=Bitmap.createScaledBitmap(op, 100, 1600, false);
                }

                status=1;
            }else if(status==1){
                popup_request(((BitmapDrawable)iv_show_img.getDrawable()).getBitmap());
            }
        }
    }

    private Bitmap getScannedBitmap(Bitmap original, Map<Integer, PointF> points){
        int width=original.getWidth();
        int height=original.getHeight();
        float xRatio=(float)original.getWidth()/iv_show_img.getWidth();
        float yRatio=(float)original.getHeight()/iv_show_img.getHeight();

        int[] newPoints={0,0,0,0,0,0,0,0};
        newPoints[0]=(int)((points.get(0).x)*xRatio);
        newPoints[1]=(int)((points.get(0).y)*xRatio);
        newPoints[2]=(int)((points.get(1).x)*xRatio);
        newPoints[3]=(int)((points.get(1).y)*xRatio);
        newPoints[4]=(int)((points.get(2).x)*xRatio);
        newPoints[5]=(int)((points.get(2).y)*xRatio);
        newPoints[6]=(int)((points.get(3).x)*xRatio);
        newPoints[7]=(int)((points.get(3).y)*xRatio);
        Log.d("OpenCV", String.valueOf(newPoints[0]));
        Log.d("OpenCV", String.valueOf(newPoints[1]));
        Log.d("OpenCV", String.valueOf(points.get(1).x));
        Log.d("OpenCV", String.valueOf(points.get(1).y));
        Log.d("OpenCV", String.valueOf(points.get(2).x));
        Log.d("OpenCV", String.valueOf(points.get(2).y));
        Log.d("OpenCV", String.valueOf(points.get(3).x));
        Log.d("OpenCV", String.valueOf(points.get(3).y));
        persWidth=max(abs(newPoints[2]-newPoints[0]), abs(newPoints[4]-newPoints[0]));
        persHeight=max(abs(newPoints[3]-newPoints[1]), abs(newPoints[5]-newPoints[1]));

        int[] pixels=new int[width*height];
        original.getPixels(pixels, 0, width, 0, 0, width, height);
        Log.d("OpenCV4Android", "Came here");
        int[] resultPixels=OpenCVHelper.perspective(pixels, newPoints, width, height);
        Log.d("OpenCV4Android", "Check point");
        Bitmap result=Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);
        result.setPixels(resultPixels, 0, width, 0, 0, width, height);
        polygonView.setVisibility(View.GONE);
        return result;
    }

    private boolean isScanPointsValid(Map<Integer, PointF> points){
        return points.size()==4;
    }

    public static Bitmap rotateImage(Bitmap source, float angle){
        Matrix matrix=new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
