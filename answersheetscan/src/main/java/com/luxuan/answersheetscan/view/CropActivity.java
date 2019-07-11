package com.luxuan.answersheetscan.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.luxuan.answersheetscan.R;
import com.luxuan.answersheetscan.presenter.CropPresenter;
import com.luxuan.answersheetscan.presenter.CropPresenterImpl;

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
}
