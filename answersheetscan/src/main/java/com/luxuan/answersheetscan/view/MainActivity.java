package com.luxuan.answersheetscan.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.luxuan.answersheetscan.R;
import com.luxuan.answersheetscan.utils.PermissionUtils;
import com.luxuan.answersheetscan.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private static final String TAG="MainActivity";
    private MainPresenter mPresenter;
    private File mFile;
    private ImageResultAdapter mAdapter;
    private DealPhotoProgressDialog mProgressDialog;

    public static List<Bitmap> mBitmaps= new ArrayList<>();
    public static List<String> mTitles=new ArrayList<>();
    public static List<AnswerSheetItemModel> mAnswers=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        initData();
        initOpenCV();
        initView();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mBitmaps.clear();
        mTitles.clear();
        mFile=null;
        refreshData();
    }

    private void initOpenCV(){
        mPresenter.initOpenCV();
    }

    private void initData(){
        mPresenter=new MainPresenterImpl(this);
    }

    private void initView(){
        findViewById(R.id.btn_take_photo).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mPresenter.takePhoto();
            }
        });

        findViewById(R.id.btn_view_album).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                mPresenter.viewAlbum();
            }
        });

        findViewById(R.id.btn_deal_photo).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                if(mFile==null){
                    ToastUtils.showToast(MainActivity.this, "请先通过相机或相册传入图片");
                    return;
                }
                if(mBitmaps.size()>1){
                    Bitmap srcBitmap=mBitmaps.get(0);
                    String srcTitle=mTitles.get(0);
                    mBitmaps.clear();
                    mBitmaps.add(srcBitmap);
                    mTitles.clear();
                    mTitles.add(srcTitle);
                    mAdapter.notifyDataSetChanged();
                }
                showProgress("读取原图", 0, 0);
                mPresenter.dealWithPhoto(mFile);
            }
        });

        findViewById(R.id.btn_view_answer).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                ToastUtils.showToast(MainActivity.this, "请先通过相机或相册传入图片");
                return;
            }
            if(mAnswers.size()==0){
                ToastUtils.showToast(MainActivity.this, "请先处理图片");
                return;
            }
            ResultActivity.startAction(MainActivity.this);
        });

        RecyclerView rvResult=findViewById(R.id.rv_result);
        rvResult.setLayoutManager(new LinearLayoutManager(this));
        mAdapter=new ImageResultAdapter(this, mBitmaps, mTitles);
        rvResult.setAdapter(mAdapter);
    }

    private void requestPermission(){
        if(!EasyPermissions.hasPermissions(this, PermissionUtils.permissions)){
            EasyPermissions.requestPermissions(this, "为保证Demo运行，需要申请权限", PermissionUtils.REQUEST_PERMISSION_CODE, PermissionUtils.permissions);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> params){

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> params){
        ToastUtils.showToast(this, "权限未获取，请手动打开");
    }
}
