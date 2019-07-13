package com.luxuan.answersheetscan.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.luxuan.answersheetscan.R;
import com.luxuan.answersheetscan.adapter.AnswerAdapter;

public class ResultActivity extends Activity {

    public static void startAction(Context context){
        Intent intent=new Intent(context, ResultActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initView();
    }

    private void initView(){
        findViewById(R.id.tv_exit).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                finish();
            }
        });

        RecyclerView rvResult=findViewById(R.id.rv_result);
        rvResult.setLayoutManager(new LinearLayoutManager(this));
        rvResult.setAdapter(new AnswerAdapter(this, MainActivity.mAnswers));
    }
}
