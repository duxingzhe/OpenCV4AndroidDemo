package com.luxuan.answersheetscan.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.luxuan.answersheetscan.R;
import com.luxuan.answersheetscan.model.AnswerSheetItemModel;

import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {

    private final Context mContext;
    private final List<AnswerSheetItemModel> mAnswers;

    public AnswerAdapter(Context context, List<AnswerSheetItemModel> answers){
        mContext=context;
        mAnswers=answers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.view_item_answer,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position){
        AnswerSheeItemModel answer=mAnswers.get(position);
        holder.mTvIndex.setText("第"+answer.index+"题");
        StringBuilder answerSb=new StringBuilder();
        if(answer.checkA){
            answerSb.append("A");
        }
        if(answer.checkB){
            answerSb.append("B");
        }
        if(answer.checkC){
            answerSb.append("C");
        }
        if(answer.checkD){
            answerSb.append("D");
        }
        String answerStr=answerSb.toString();
        if(TextUtils.isEmpty(answerStr)){
            holder.mTvAnswer.setTextColor(mContext.getResources().getColor(android.R.color.darker_gray));
            holder.mTvAnswer.setText("无");
        }else{
            holder.mTvAnswer.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_ligth));
            holder.mTvAnswer.setText(answerStr);
        }
        holder.mTvFactorA.setText("A值："+answer.factorA);
        holder.mTvFactorB.setText("B值："+answer.factorB);
        holder.mTvFactorC.setText("C值："+answer.factorC);
        holder.mTvFactorD.setText("D值："+answer.factorD);
    }

    @Override
    public int getItemCount(){
        return mAnswers== null ? 0:mAnswers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTvIndex;
        public TextView mTvAnswer;
        public TextView mTvFactorA;
        public TextView mTvFactorB;
        public TextView mTvFactorC;
        public TextView mTvFactorD;

        public ViewHolder(View itemView){
            super(itemView);
            initView(itemView);
        }

        private void initView(View itemView){
            mTvIndex=itemView.findViewById(R.id.tv_question_index);
            mTvAnswer=itemView.findViewById(R.id.tv_question_answer);
            mTvFactorA=itemView.findViewById(R.id.tv_factor_a);
            mTvFactorB=itemView.findViewById(R.id.tv_factor_b);
            mTvFactorC=itemView.findViewById(R.id.tv_factor_c);
            mTvFactorD=itemView.findViewById(R.id.tv_factor_d);
        }
    }
}
