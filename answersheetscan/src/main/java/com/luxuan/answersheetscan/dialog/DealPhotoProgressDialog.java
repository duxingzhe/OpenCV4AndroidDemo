package com.luxuan.answersheetscan.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.luxuan.answersheetscan.R;

public class DealPhotoProgressDialog extends CustomBaseDialog {

    private Context mContext;
    private TextView mTextViewDealProgress;
    private ProgressBar mProgressBarDealProgress;
    private TextView mTextViewContent;

    public DealPhotoProgressDialog(Context context){
        super(context);
        mContext=context;
    }

    @Override
    public void initWindow(WindowManager windowManager, Window window){
        window.setGravity(Gravity.CENTER);
        initView();
    }

    private void initView(){
        mTextViewDealProgress=findViewById(R.id.tv_deal_progress);
        mProgressBarDealProgress=findViewById(R.id.pb_deal_progress);
        mTextViewContent=findViewById(R.id.tv_content);
    }

    @Override
    public boolean setDialogCancelable(){
        return false;
    }

    @Override
    public View setDialogView(){
        return LayoutInflater.from(mContext).inflate(R.layout.dialog_deal_photo_progress,null);
    }

    public void setDealProgress(String progressName, int current, int total){
        if(mProgressBarDealProgress!=null&&mTextViewDealProgress!=null&&mTextViewContent!=null&&isShowing()){
            if(total==0){
                mProgressBarDealProgress.setMax(1);
                mProgressBarDealProgress.setProgress(0);
                mTextViewContent.setText("");
            }else{
                mProgressBarDealProgress.setMax(total);
                mProgressBarDealProgress.setProgress(current);
                mTextViewContent.setText(current+"/"+total);
            }
            mTextViewContent.setText("正在执行："+progressName);
        }
    }
}
