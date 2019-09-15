package com.luxuan.stitcher.stitcher.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.luxuan.stitcher.R;

public class SplashScreenActivity extends AppCompatActivity {

    private static boolean splashLoaded=false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(!splashLoaded){
            int secondsDelayed=1;
            new Handler().postDelayed(new Runnable(){

                @Override
                public void run(){
                    startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
                    finish();
                }
            }, secondsDelayed*1500);
            splashLoaded=true;
        }else{
            Intent intent=new Intent(SplashScreenActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }
    }

}
