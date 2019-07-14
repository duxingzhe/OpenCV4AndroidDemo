package com.luxuan.answersheetscan.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.luxuan.answersheetscan.R;

public class PhotoActivity extends Activity {

    public static void startAction(Context context, int index){
        Intent intent=new Intent(context, PhotoActivity.class);
        intent.putExtra("index", index);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        initView();
    }

    private void initView(){
        int index=getIntent().getIntExtra("index", -1);
        if(index==-1||MainActivity.mBitmaps.size()<=index){
            finish();
            return;
        }

        findViewById(R.id.tv_exit).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                finish();
            }
        });

        PhotoView pvPhoto=findViewById(R.id.pv_photo);
        pvPhoto.setImageBitmap(MainActivity.mBitmaps.get(index));
        TextView tvTitle=findViewById(R.id.tv_title);
        tvTitle.setText(MainActivity.mTitles.get(index));
    }
}
