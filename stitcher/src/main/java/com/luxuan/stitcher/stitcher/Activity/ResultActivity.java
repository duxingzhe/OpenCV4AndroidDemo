package com.luxuan.stitcher.stitcher.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.luxuan.stitcher.R;

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

        black_white.setOnClickListener(new BWButtonClikcListener());
    }
}
