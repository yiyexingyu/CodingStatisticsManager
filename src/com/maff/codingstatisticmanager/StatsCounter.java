package com.maff.codingstatisticmanager;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actions.*;
import com.intellij.openapi.ide.CopyPasteManager;
import com.maff.codingstatisticmanager.data.CodingStats;
import com.maff.codingstatisticmanager.data.DatabaseStatsRepository;
import com.maff.codingstatisticmanager.data.Period;
import com.maff.codingstatisticmanager.data.PeriodStats;

import java.awt.datatransfer.DataFlavor;
import java.util.Calendar;

public class StatsCounter {
    private static final int IMMEDIATE_BACKSPACE_THRESHOLD = 1000; // Ms
    private CodingStats stats;
    private Calendar lastEventTime;
    private long lastTypeTime;
    private boolean isDirty = false;

    private DatabaseStatsRepository dbRepository;

    private final Object statsMutex = new Object();  // 互斥锁
    public StatsCounter(DatabaseStatsRepository dbRepository) {
        this.dbRepository = dbRepository;
        this.stats = this.dbRepository.loadLastCodingStats();

        this.preFillStats();
        lastEventTime = Calendar.getInstance();
        if (stats.lastEventTime > 0) {
            lastEventTime.setTimeInMillis(stats.lastEventTime);
        } else {
            stats.lastEventTime = lastEventTime.getTimeInMillis();
        }

        this.ensureTimePeriods();
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    public boolean isDirty() {
        return isDirty;
    }


    /**
     * 如果周期（今天，本周， 本月）中那个不在CodingStats
     * 就添加一个空的 都为0，PeriodStats
     */
    private void preFillStats() {
        for (Period period : Period.values()) {
            if (!stats.periods.containsKey(period)) {
                stats.periods.put(period, this.createEmptyPeriod());
            }
        }
    }

    private PeriodStats createEmptyPeriod() {
        // 如果需要，保留在将来实现更复杂的逻辑
        return new PeriodStats();
    }

    /**
     * 如果我们输入新的时间段，则重置时间段
     */
    private void ensureTimePeriods() {
        Calendar newEventTime = Calendar.getInstance();

        // 更新 月
        if (newEventTime.get(Calendar.MONTH) != lastEventTime.get(Calendar.MONTH)) {
            stats.periods.put(Period.本月, new PeriodStats());
            dbRepository.insertDataToDb(Period.本月, stats.periods.get(Period.本月));
        }

        // 更新 周
        if (newEventTime.get(Calendar.WEEK_OF_YEAR) != lastEventTime.get(Calendar.WEEK_OF_YEAR)) {
            stats.periods.put(Period.本周, new PeriodStats());
            dbRepository.insertDataToDb(Period.本周, stats.periods.get(Period.本周));
        }

        // 更新 日
        if (newEventTime.get(Calendar.DAY_OF_YEAR) != lastEventTime.get(Calendar.DAY_OF_YEAR)) {
            stats.periods.put(Period.今天, new PeriodStats());
            dbRepository.insertDataToDb(Period.今天, stats.periods.get(Period.今天));
        }

        lastEventTime = newEventTime;
        stats.lastEventTime = lastEventTime.getTimeInMillis();
        dbRepository.updateLastEventTime(stats.lastEventTime);
    }

    // 键入字符 线程安全
    public void onType(char c, DataContext dataContext) {
        this.isDirty = true;
        ensureTimePeriods();
        Editor editor = TextComponentEditorAction.getEditorFromContext(dataContext);
        assert editor != null;
        int caretCount = editor.getCaretModel().getCaretCount();

        synchronized (statsMutex) {
            for (PeriodStats period : stats.periods.values()) {
                period.type++;
                period.insert += caretCount;
            }
        }
        lastTypeTime = System.currentTimeMillis();
    }

    // 键入其他字符 线程安全
    public void onAction(AnAction action, DataContext dataContext, AnActionEvent event) {
        if (action == null) {
            return;
        }

        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        if (editor == null) {
            // The event is related to another IDE input rather than editor
            return;
        }

        String selectedText = editor.getSelectionModel().getSelectedText(true);
        int selectedCount = selectedText != null ? selectedText.length() : 0;

        if (action instanceof BackspaceAction || action instanceof DeleteAction) {
            this.isDirty = true;
            ensureTimePeriods();
            int caretCount = editor.getCaretModel().getCaretCount();
            boolean isImmediate = (System.currentTimeMillis() - lastTypeTime) < IMMEDIATE_BACKSPACE_THRESHOLD;

            synchronized (this.statsMutex) {
                for (PeriodStats period : stats.periods.values()) {
                    period.backDel += 1;
                    period.remove += selectedCount > 0 ? selectedCount : caretCount;

                    if (isImmediate) {
                        period.backImmediate += 1;
                    }
                }
            }
        } else if (action instanceof CutAction) {
            this.isDirty = true;
            ensureTimePeriods();

            synchronized (statsMutex) {
                for (PeriodStats period : stats.periods.values()) {
                    period.cut += selectedCount;
                    period.remove += selectedCount;
                }
            }
        } else if (action instanceof PasteAction) {
            this.isDirty = true;
            ensureTimePeriods();
            int pasteCount = 0;

            CopyPasteManager copyPasteManager = CopyPasteManager.getInstance();

            // 如果粘贴的String类型的
            if (copyPasteManager.areDataFlavorsAvailable(DataFlavor.stringFlavor)) {
                pasteCount = ((String) copyPasteManager.getContents(DataFlavor.stringFlavor)).length();
            }

            synchronized (statsMutex) {
                for (PeriodStats period : stats.periods.values()) {
                    period.remove += selectedCount;
                    period.paste += pasteCount;
                }
            }

        }
    }

    /**
     * Thread safe, returns a copy!
     * @return Copy instance of the latest stats
     */
    public CodingStats getStats(){
        synchronized (statsMutex){
            return new CodingStats(this.stats);
        }
    }
}
