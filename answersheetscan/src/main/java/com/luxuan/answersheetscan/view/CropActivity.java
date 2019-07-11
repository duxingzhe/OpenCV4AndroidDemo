package com.luxuan.answersheetscan.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.luxuan.answersheetscan.R;
import com.luxuan.answersheetscan.presenter.CropPresenter;
import com.luxuan.answersheetscan.presenter.CropPresenterImpl;
import com.luxuan.answersheetscan.utils.ToastUtils;

import me.pqpo.smartcropperlib.view.CropImageView;

public class CropActivity extends Activity {

    private CropPresenter mPresenter;
    private CropImageView mImageViewCrop;
    private TextView mTextViewCancel;
    private TextView mTextViewConfirm;
    private SimpleProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        initData();
        initView();
    }

    private void initData(){
        mPresenter = new CropPresenterImpl(this);
    }

    private void initView(){
        mImageViewCrop=findViewById(R.id.iv_crop);
        mTextViewCancel=findViewById(R.id.tv_exit);
        mTextViewConfirm=findViewById(R.id.tv_confirm);
        mTextViewCancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        mTextViewConfirm.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                if(mImageViewCrop.canRightCrop()){
                    mPresenter.crop(mImageViewCrop);
                }else{
                    ToastUtils.showToast(CropActivity.this, "初始化失败");
                }
            }
        });

        mPresenter.analyzeSrcBitmap(getIntent().getStringExtra("filePath"));
    }

    private void showProgress(String content){
        if(mProgressDialog==null){
            mProgressDialog=new SimpleProgressDialog(this);
        }
        mProgressDialog.show();
        mProgressDialog.setContent(content);
    }
}
