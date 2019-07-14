package com.luxuan.answersheetscan.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luxuan.answersheetscan.R;
import com.luxuan.answersheetscan.utils.BitmapUtils;
import com.luxuan.answersheetscan.view.PhotoActivity;

import java.util.List;

public class ImageResultAdapter extends RecyclerView.Adapter<ImageResultAdapter.ViewHolder>{

    private final Context mContext;
    private final List<Bitmap> mBitmaps;
    private final List<String> mTitles;

    public ImageResultAdapter(Context context, List<Bitmap> bitmaps, List<String> titles){
        mContext=context;
        mBitmaps=bitmaps;
        mTitles=titles;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.view_item_result, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position){
        final Bitmap bitmap=mBitmaps.get(position);
        final String title=mTitles.get(position);
        final int size= BitmapUtils.dpToPx(mContext, 360F);
        holder.mImageViewResult.setImageBitmap(BitmapUtils.resizeImage(bitmap, size, size));
        holder.mTextViewTitle.setText("STEP"+(position+1)+":"+title);
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                PhotoActivity.startAction(mContext, position);
            }
        });
    }

    @Override
    public int getItemCount(){
        return mBitmaps==null?0:mBitmaps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextViewTitle;
        public ImageView mImageViewResult;

        public ViewHolder(View itemView){
            super(itemView);
            initView(itemView);
        }

        private void initView(View itemView){
            mTextViewTitle=itemView.findViewById(R.id.tv_step);
            mImageViewResult=itemView.findViewById(R.id.iv_result);
        }
    }
}
