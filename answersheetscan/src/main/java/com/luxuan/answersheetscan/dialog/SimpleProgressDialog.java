package com.luxuan.answersheetscan.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.luxuan.answersheetscan.R;

public class SimpleProgressDialog extends CustomBaseDialog {

    private Context mContext;
    private TextView mTextViewContent;

    public SimpleProgressDialog(Context context){
        super(context);
        mContext=context;
    }

    @Override
    public void initWindow(WindowManager windowManager, Window window){
        window.setGravity(Gravity.CENTER);
        initView();
    }

    private void initView(){
        mTextViewContent=findViewById(R.id.tv_content);
    }

    @Override
    public boolean setDialogCancelable(){
        return false;
    }

    @Override
    public View setDialogView(){
        return LayoutInflater.from(mContext).inflate(R.layout.dialog_simple_progress, null);
    }

    public void setContent(String content){
        if(mTextViewContent!=null&&isShowing()){
            mTextViewContent.setText("正在执行：" + content + "，请稍后");
        }
    }
}
