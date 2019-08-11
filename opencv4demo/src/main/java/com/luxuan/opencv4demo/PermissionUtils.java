package com.luxuan.opencv4demo;

import android.Manifest;

public class PermissionUtils {

    public static int REQUEST_PERMISSION_CODE=10101;

    public static final String[] permissions={
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
}
