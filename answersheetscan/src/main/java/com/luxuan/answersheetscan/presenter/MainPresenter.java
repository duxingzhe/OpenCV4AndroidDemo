package com.luxuan.answersheetscan.presenter;

import android.content.Intent;

import java.io.File;

public interface MainPresenter {

    void initOpenCV();

    void handleActivityResult(int requestCode, int resultCode, Intent data);

    void takePhoto();

    void viewAlbum();

    void cropPhoto(File file);

    void dealWithPhoto(File file);
}
