package com.luxuan.answersheetscan.config;

public class AnswerSheetConfig {

    private static final float TARGET_AREA_WIDTH=1560F;

    private static final float TARGET_AREA_HEIGHT=1686F;

    private static final float OFFSET_LEFT=59.5F;

    private static final float OFFSET_RIGHT=71.5F;

    private static final float OFFSET_TOP=77.5F;

    private static final float OFFSET_BOTTOM=50.5F;

    private static final float EMPTY_COL_WIDTH=60F;

    private static final float EMPTY_ROW_HEIGHT=69F;

    public static final int PER_COL_COUNT=5;

    public static final int PER_ROW_COUNT=4;

    public static final int TOTAL_COL_COUNT=20;

    public static final int TOTAL_ROW_COUNT=28;

    public static final float LIMIT_ACCEPT_MAX_FACTOR=0.40F;

    public static final float LIMIT_ACCEPT_MIN_FACTOR=0.25F;

    public static final float LIMIT_ACCEPT_TOTAL_PERCENT_FACTOR=0.40F;

    public static final float LIMIT_RECHECK_MIN_FACTOR=0.15F;

    public static final float OPTION_SHRINK_FACTOR=0.10F;

    public static final float OFFSET_LEFT_FACTOR_BY_WIDTH=OFFSET_LEFT/TARGET_AREA_WIDTH;

    public static final float OFFSET_RIGHT_FACTOR_BY_WIDTH=OFFSET_RIGHT/TARGET_AREA_WIDTH;

    public static final float OFFSET_TOP_FACTOR_BY_HEIGHT=OFFSET_TOP/TARGET_AREA_HEIGHT;

    public static final float OFFSET_BOTTOM_FACTOR_BY_WIDTH=EMPTY_COL_WIDTH/TARGET_AREA_WIDTH;

    public static final float EMTPY_ROW_HEIGHT_FACTOR_BY_HEIGHT=EMPTY_ROW_HEIGHT/TARGET_AREA_HEIGHT;

}
