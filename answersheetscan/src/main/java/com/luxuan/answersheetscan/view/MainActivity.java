package com.luxuan.answersheetscan.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
