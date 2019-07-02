package com.luxuan.answersheetscan.model;

import com.luxuan.answersheetscan.config.AnswerSheetConfig;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

public class AnswerSheetModel {

    public float width;
    public float height;
    public float offsetLeft;
    public float offsetRight;
    public float offsetTop;
    public float offsetBottom;
    public float emptyColWidth;
    public float emptyRowHeight;
    public int emptyColCount;
    public int emptyRowCount;
    public float answerWidth;
    public float answerHeight;

    public List<AnswerRowModel> answerRows =new ArrayList<>();

    public AnswerSheetModel(float width, float height){
        initConfig(width, height);
        initAnswers();
    }

    private void initConfig(float width, float height){
        this.width=width;
        this.height=height;
        this.offsetLeft=width* AnswerSheetConfig.OFFSET_LEFT_FACTOR_BY_WIDTH;
        this.offsetRight=width*AnswerSheetConfig.OFFSET_RIGHT_FACTOR_BY_WIDTH;
        this.offsetTop=height*AnswerSheetConfig.OFFSET_TOP_FACTOR_BY_HEIGHT;
        this.offsetBottom=height*AnswerSheetConfig.OFFSET_BOTTOM_FACTOR_BY_HEIGHT;
        this.emptyColWidth=width*AnswerSheetConfig.EMPTY_COL_WIDTH_FACTOR_BY_WIDTH;
        this.emptyRowHeight=height*AnswerSheetConfig.EMPTY_ROW_HEIGHT_FACTOR_BY_HEIGHT;
        this.emptyColCount=AnswerSheetConfig.TOTAL_COL_COUNT/AnswerSheetConfig.PER_COL_COUNT-1;
        this.emptyRowCount=AnswerSheetConfig.TOTAL_ROW_COUNT/AnswerSheetConfig.PER_ROW_COUNT-1;
        this.answerWidth=(width-offsetLeft-offsetRight-emptyColWidth*emptyColCount)/AnswerSheetConfig.TOTAL_COL_COUNT;
        this.answerHeight=(height-offsetTop-offsetBottom-emptyRowHeight*emptyRowCount)/AnswerSheetConfig.TOTAL_ROW_COUNT;
    }

    private void initAnswers(){
        int totalQuestionRowCount=AnswerSheetConfig.TOTAL_ROW_COUNT/AnswerSheetConfig.PER_ROW_COUNT;
        int emptyColPassed;
        int emptyRowPassed=0;
        for(int i=0;i< totalQuestionRowCount;i++){
            AnswerRowModel answerRow=new AnswerRowModel();
            for(int j=0;j<totalQuestionRowCount;i++){
                emptyColPassed=j/AnswerSheetConfig.PER_COL_COUNT;
                AnswerSheetItemModel answerItem=new AnswerSheetItemModel();
                answerItem.index=AnswerSheetConfig.TOTAL_COL_COUNT*i+j+1;
                for(int k=0;k<8;k++)
                    if(k%2==0){
                        answerItem.points[k]=new Point(offsetLeft+j*answerWidth+ emptyColPassed*emptyColWidth, offsetTop+i*answerHeight*4+k/2*answerHeight + emptyRowPassed*emptyRowHeight);
                    }else{
                        answerItem.points[k]=new Point(offsetLeft+j*answerWidth+answerWidth+emptyColPassed*emptyColWidth, offsetTop+i*answerHeight*4+(k+1)/2*answerHeight+emptyRowPassed*emptyRowHeight);
                }
                answerRow.answers.add(answerItem);
            }
            answerRows.add(answerRow);
            emptyRowPassed++;
        }
    }

    public static class AnswerRowModel{
        public List<AnswerSheetItemModel> answers=new ArrayList<>();
    }

    public static class AnswerSheetItemModel{
        public int index;
        public Point[] points=new Point[8];
        public boolean checkA=false;
        public boolean checkB=false;
        public boolean checkC=false;
        public boolean checkD=false;
        public float factorA=0F;
        public float factorB=0F;
        public float factorC=0F;
        public float factorD=0F;
    }
}
