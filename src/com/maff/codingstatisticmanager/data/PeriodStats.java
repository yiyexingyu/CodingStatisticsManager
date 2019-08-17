package com.maff.codingstatisticmanager.data;

/**
 * 用来保存各种情况下的字符个数
 */
public class PeriodStats {

    public static final int STATS_NUM = 7;
    public int type; // 键入字符
    public int backDel; // 删除 按下BackSpace删除的字符
    public int backImmediate;
    public int cut; // 剪切掉的字符
    public int paste; // 粘贴
    public int remove; // 移除
    public int insert; // 插入

    public PeriodStats(){}

    public PeriodStats(PeriodStats other){
        this.type = other.type;
        this.backDel = other.backDel;
        this.backImmediate = other.backImmediate;
        this.cut = other.cut;
        this.paste = other.paste;
        this.remove = other.remove;
        this.insert = other.insert;
    }

}
