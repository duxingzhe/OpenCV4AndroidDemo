package com.luxuan.answersheetscan.presenter;

import me.pqpo.smartcropperlib.view.CropImageView;

public interface CropPresenter {

    void crop(CropImageView imageView);

    void analyzeSrcBitmap(String filePath);
}
