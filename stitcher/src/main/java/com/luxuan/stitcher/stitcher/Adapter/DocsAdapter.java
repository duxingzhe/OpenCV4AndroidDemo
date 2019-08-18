package com.luxuan.stitcher.stitcher.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luxuan.stitcher.R;
import com.luxuan.stitcher.stitcher.Beans.DocItem;

import java.util.ArrayList;

public class DocsAdapter extends RecyclerView.Adapter<DocsAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<DocItem> mItems;

    public DocsAdapter(Context context, ArrayList<DocItem> items){
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
        layout=R.layout.item_gallery;
        View view= LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        holder.name.setText(mItems.get(position).getName());
        holder.tsText.setText(mItems.get(position).getTimeStamp());
        holder.contactPic.setImageBitmap(mItems.get(position).getBitmap());
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Intent i=new Intent(mContext, DetailActvity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("folder", mItems.get(position).getName());
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount(){
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView name, tsText;
        public ImageView contactPic;

        public ViewHolder(View itemView){
            super(itemView);
            name=(TextView)itemView.findViewById(R.id.doc_name);
            tsText=(TextView)itemView.findViewById(R.id.doc_time_stamp);
            contactPic=(ImageView)itemView.findViewById(R.id.iv1);
        }
    }
}
