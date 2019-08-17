package com.maff.codingstatisticmanager.data;

import java.util.HashMap;
import java.util.Map;

public class SqlString {
    public static final Map<Period, String> PERIOD_INSERT_SQL = new HashMap<>();
    public static final Map<Period, String> PERIOD_SELECT_SQL = new HashMap<>();
    public static final Map<Period, String> PERIOD_UPDATE_SQl = new HashMap<>();
    public static final Map<Period, String> PERIOD_LAST_STATS_SQl = new HashMap<>();

    public static final String UPDATE_LAST_EVENT_TIME = "update day_coding_count set last_event_time=? where id=?";

    private static final String USAGE_INSERT_FIELDS =
            "(total_coding_count, valid_coding_count, type_coding_count, back_del_coding_count, " +
                    "cut_coding_count, paste_coding_count, remove_coding_count, insert_coding_count, date) ";
    private static final String USAGE_UPDATE_FIELDS =
            "total_coding_count=?, valid_coding_count=?, type_coding_count=?, back_del_coding_count=?," +
                    "cut_coding_count=?, paste_coding_count=?, remove_coding_count=?, insert_coding_count=?";

    public static final String[] TODAY_QSL_LIST = {
            "insert into day_coding_count " + USAGE_INSERT_FIELDS + "values (?, ?, ?, ?, ?, ?, ?, ?, ?)",
            "update day_coding_count set " + USAGE_UPDATE_FIELDS + " where id=?",
            "select * from day_coding_count where id=?",
            "select * from day_coding_count order by date DESC limit 1"
    };

    public static final String[] WEEK_QSL_LIST = {
            "insert into week_coding_count " + USAGE_INSERT_FIELDS + "values (?, ?, ?, ?, ?, ?, ?, ?, ?)",
            "update week_coding_count set " + USAGE_UPDATE_FIELDS + " where id=?",
            "select * from week_coding_count where id=?",
            "select * from week_coding_count order by date DESC limit 1"
    };

    public static final String[] MOTH_QSL_LIST = {
            "insert into moth_coding_count " + USAGE_INSERT_FIELDS + "values (?, ?, ?, ?, ?, ?, ?, ?, ?)",
            "update moth_coding_count set " + USAGE_UPDATE_FIELDS + " where id=?",
            "select * from moth_coding_count where id=?",
            "select * from moth_coding_count order by date DESC limit 1"
    };

    static {
        PERIOD_INSERT_SQL.put(Period.今天, TODAY_QSL_LIST[0]);
        PERIOD_INSERT_SQL.put(Period.本周, WEEK_QSL_LIST[0]);
        PERIOD_INSERT_SQL.put(Period.本月, MOTH_QSL_LIST[0]);
    }

    static {
        PERIOD_UPDATE_SQl.put(Period.今天, TODAY_QSL_LIST[1]);
        PERIOD_UPDATE_SQl.put(Period.本周, WEEK_QSL_LIST[1]);
        PERIOD_UPDATE_SQl.put(Period.本月, MOTH_QSL_LIST[1]);
    }

    static {
        PERIOD_SELECT_SQL.put(Period.今天, TODAY_QSL_LIST[2]);
        PERIOD_SELECT_SQL.put(Period.本周, WEEK_QSL_LIST[2]);
        PERIOD_SELECT_SQL.put(Period.本月, MOTH_QSL_LIST[2]);
    }

    static {
        PERIOD_LAST_STATS_SQl.put(Period.今天, TODAY_QSL_LIST[3]);
        PERIOD_LAST_STATS_SQl.put(Period.本周, WEEK_QSL_LIST[3]);
        PERIOD_LAST_STATS_SQl.put(Period.本月, MOTH_QSL_LIST[3]);
    }
}
