package com.luxuan.answersheetscan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.luxuan.answersheetscan.R;

public abstract class CustomBaseDialog extends Dialog {

    public CustomBaseDialog(Context context){
        this(context, R.style.CustomDialog);
    }

    private CustomBaseDialog(Context context, int themeResId){
        super(context, themeResId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(setDialogView());
        setCancelable(setDialogCancelable());

        WindowManager windowManager=(WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        Window window=this.getWindow();
        initWindow(windowManager, window);
    }

    public abstract void initWindow(WindowManager windowManager, Window window);
    public abstract boolean setDialogCancelable();
    public abstract View setDialogView();
}
