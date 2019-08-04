package com.maff.codingcounter.ui;

import com.maff.codingcounter.data.Period;

import java.util.HashMap;
import java.util.Map;

public class UiString {

    public static final Map<Period, String> PERIOD_LABELS = new HashMap<>();

    static {
        PERIOD_LABELS.put(Period.今天, "今天");
        PERIOD_LABELS.put(Period.本周, "本周");
        PERIOD_LABELS.put(Period.本月, "本月");
        PERIOD_LABELS.put(Period.所有, "所有");
    }

    // 不同 计数的label
    public static String LABEL_STAT_TYPE = "键入字符";
    public static String LABEL_STAT_BACK_DEL = "按下Backspace或del";
    public static String LABEL_STAT_BACK_IMMEDIATE = "Backspace corrections";
    public static String LABEL_STAT_CUT = "剪切的字符";
    public static String LABEL_STAT_PASTE = "粘贴的字符";
    public static String LABEL_STAT_REMOVE = "removed字符总数";
    public static String LABEL_STAT_INSERTED = "added字符总数";

    // 表头label
    public static final String COLUMN_LABEL = "计数";
    public static final String COLUMN_VALUE = "值";

    public static final String WARNING_UI_PERIOD = "请注意: 组件会5s更新一次数据!";
}
