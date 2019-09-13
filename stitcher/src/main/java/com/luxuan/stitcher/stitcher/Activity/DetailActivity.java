package com.luxuan.stitcher.stitcher.Activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.luxuan.stitcher.R;
import com.luxuan.stitcher.stitcher.Adapter.DetailAdapter;
import com.luxuan.stitcher.stitcher.Beans.DetailItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private static String filePath="/mnt/sdcard/FirstPdf.pdf";
    private RecyclerView rvPictures;
    private ArrayList<Bitmap> bitmapLists;
    private File[] fileLists;
    private String string;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        string=getIntent().getStringExtra("folder");
        getSupportActionBar().setTitle(string);

        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                onBackPressed();
            }
        });

        bitmapLists=new ArrayList<>();

        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                new PDFAsyncTask().execute();
            }
        });

        rvPictures=(RecyclerView)findViewById(R.id.rvPics);

        new LoadPhotos(string).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        if(id==R.id.action_Select){
            deleteRecursive(file);
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteRecursive(File file){
        if(file.isDirectory()){
            for(File childFile : file.listFiles()){
                deleteRecursive(childFile);
            }
        }

        file.delete();
    }

    private class PDFAsyncTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog loadingDialog=new ProgressDialog(DetailActivity.this);

        @Override
        public void onPreExecute(){
            super.onPreExecute();
            loadingDialog.setMessage("Saving as PDF...");
            loadingDialog.show();
        }

        @Override
        public Void doInBackground(Void... params){
            convertIt(bitmapLists, string);

            file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/DocumentScanner/"+string+"/example.pdf");
            Intent target=new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.fromFile(file), "application/pdf");
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent intent=Intent.createChooser(target, "OpenFile");

            try{
                startActivity(intent);
            }catch(ActivityNotFoundException e){

            }

            return null;
        }

        @Override
        public void onPostExecute(Void result){
            super.onPostExecute(result);

            loadingDialog.dismiss();
        }
    }

    public void getFromSdcard(String s){
        file=new File(Environment.getExternalStorageDirectory(), "/DocumentScanner/"+s);
        ArrayList<DetailItem> iPostParams=new ArrayList<>();
        Log.d("DetailActivity", "Check point 1");

        if(file.isDirectory()){
            Log.d("DetailActivity", "Check point 2");
            fileLists=file.listFiles();

            for(int i=0;i<fileLists.length;i++){
                try{
                    Bitmap bitmap= BitmapFactory.decodeStream(new FileInputStream(fileLists[i]));
                    if(bitmap!=null){
                        DetailItem postEmail=new DetailItem(bitmap, fileLists[i].getAbsolutePath());
                        iPostParams.add(postEmail);
                        bitmapLists.add(bitmap);
                    }
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }
            }

            DetailAdapter adapter=new DetailAdapter(DetailActivity.this, iPostParams);
            rvPictures.setAdapter(adapter);
            rvPictures.setLayoutManager(new GridLayoutManager(this, 2));
        }
    }

    private void convertIt(ArrayList<Bitmap> bitmaps, String s){
        try{
            Document document=new Document();
            String dirPath=Environment.getExternalStorageDirectory().toString();

            PdfWriter.getInstance(document, new FileOutputStream(dirPath+"/DocumentScanner/"+s+"/example.pdf"));
            document.open();
            Bitmap bitmap;

            for(int i=0;i<bitmaps.size();i++){
                bitmap=bitmaps.get(i);
                if(bitmap!=null){
                    ByteArrayOutputStream stream=new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    Image img= Image.getInstance(stream.toByteArray());

                    float scaler=((document.getPageSize().getWidth()-document.leftMargin()
                        -document.rightMargin()-0)/img.getWidth())*100;
                    img.scalePercent(scaler);
                    img.setAlignment(Image.ALIGN_CENTER|Image.ALIGN_TOP);

                    document.add(img);
                }
            }
            document.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public class LoadPhotos extends AsyncTask<String, Void, String>{
        ProgressDialog dialog;
        ArrayList<DetailItem> iPostParams=new ArrayList<>();
        private DetailItem postEmail;
        String s;

        public LoadPhotos(String s){
            this.s=s;
        }

        @Override
        public void onPreExecute(){
            dialog=ProgressDialog.show(DetailActivity.this,"Loading",
                    "Loading...", true);
            dialog.show();
        }

        @Override
        public String doInBackground(String... arg0){
            file=new File(Environment.getExternalStorageDirectory(), "/DocumentScanner/"+s);
            Log.d("DetailActivity", "Check point 1");

            if(file.isDirectory()) {
                Log.d("DetailActivity", "Check point 2");
                fileLists = file.listFiles();

                for (int i = 0; i < fileLists.length; i++) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(fileLists[i]));
                        if (bitmap != null) {
                            postEmail = new DetailItem(bitmap, fileLists[i].getAbsolutePath());
                            iPostParams.add(postEmail);
                            bitmapLists.add(bitmap);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            return String.valueOf(bitmapLists);
        }

        @Override
        public void onPostExecute(String result){
            DetailAdapter adapter=new DetailAdapter(DetailActivity.this, iPostParams);
            rvPictures.setAdapter(adapter);
            rvPictures.setLayoutManager(new GridLayoutManager(DetailActivity.this, 2));
            dialog.dismiss();
        }
    }
}
