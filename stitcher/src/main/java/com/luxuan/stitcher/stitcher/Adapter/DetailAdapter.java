package com.luxuan.stitcher.stitcher.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.luxuan.stitcher.R;
import com.luxuan.stitcher.stitcher.Activity.ImageActivity;
import com.luxuan.stitcher.stitcher.Beans.DetailItem;

import java.util.ArrayList;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<DetailItem> mItems;

    public DetailAdapter(Context context, ArrayList<DetailItem> items){
        mContext=context;
        mItems=items;
    }

    @Override
    public int getItemViewType(int position){
        return super.getItemViewType(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        int layout;
        layout=R.layout.item_detail;
        View view= LayoutInflater.from(parent.getContext()).inflate(layout,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        holder.contactPic.setImageBitmap(mItems.get(position).getBitmap());
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Intent intent=new Intent(mContext, ImageActivity.class);
                intent.putExtra("pathKey", mItems.get(position).getPath());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount(){
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView contactPic;

        public ViewHolder(View itemView){
            super(itemView);
            contactPic=(ImageView)itemView.findViewById(R.id.iv1);
        }
    }

}
