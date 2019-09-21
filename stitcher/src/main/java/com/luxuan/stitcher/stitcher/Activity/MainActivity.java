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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
}
