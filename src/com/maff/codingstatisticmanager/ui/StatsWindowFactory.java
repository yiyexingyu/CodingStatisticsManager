package com.maff.codingstatisticmanager.ui;

import com.intellij.concurrency.JobScheduler;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.JBTable;
import com.maff.codingstatisticmanager.AppComponent;
import com.maff.codingstatisticmanager.data.CodingStats;
import com.maff.codingstatisticmanager.data.Period;
import com.maff.codingstatisticmanager.data.PeriodStats;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class StatsWindowFactory implements com.intellij.openapi.wm.ToolWindowFactory {

    private final static int UI_UPDATE_PERIOD = 5;

    private ToolWindow window;
    private JComponent windowContent;

    private JButton clearStatsButton;
    private ScheduledFuture updateTask;

    private Map<Period, DefaultTableModel> tables = new HashMap<>();
    private CallBack callback;

    public interface CallBack {
        CodingStats getStats();
        void onStatsResetClicked();
    }

    private void createUi() {
        JComponent contentWrapper = new JPanel();
        contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.PAGE_AXIS));

        windowContent = new JBScrollPane(
                contentWrapper,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        // Box.createRigidArea 创建一个总是具有指定大小的不可见组件。充当布局组件键的间隙
        contentWrapper.add(Box.createRigidArea(new Dimension(1, 8)));

        JLabel waringLabel = new JLabel(UiString.WARNING_UI_PERIOD);
        waringLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentWrapper.add(waringLabel);

        Font captionFont = UIManager.getFont("Label.font").deriveFont(Font.BOLD);

        for (Period period : Period.values()) {

            contentWrapper.add(Box.createRigidArea(new Dimension(1, 12)));
            // 今天 本周 本月 所有
            JLabel periodNameLabel = new JLabel(UiString.PERIOD_LABELS.get(period));
            periodNameLabel.setFont(captionFont);
            periodNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentWrapper.add(periodNameLabel);
            contentWrapper.add(Box.createRigidArea(new Dimension(1, 12)));

            DefaultTableModel tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            tableModel.addColumn(UiString.COLUMN_LABEL);
            tableModel.addColumn(UiString.COLUMN_VALUE);

            JBTable table = new JBTable(tableModel);
            table.setStriped(true);
            table.setFocusable(true);
            table.setRowSelectionAllowed(true);
            tables.put(period, tableModel);

            contentWrapper.add(table.getTableHeader());
            contentWrapper.add(table);
        }

        clearStatsButton = new JButton("重置数据");
        clearStatsButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentWrapper.add(Box.createRigidArea(new Dimension(1, 12)));
        contentWrapper.add(clearStatsButton);
        contentWrapper.add(Box.createRigidArea(new Dimension(1, 8)));
    }

    // 大数缩写
    private String prettifyLong(long val) {
        if (val < 1000L) {
            return String.valueOf(val);
        } else {
            return val < 1000000L ? String.format("%.2fK", (double)val / 1000.0D) : String.format("%.2fM", (double)val / 1000000.0D);
        }
    }

    public void updateData(){
        CodingStats stats = this.callback.getStats();

        for(Map.Entry<Period, PeriodStats> entry : stats.periods.entrySet()){

            DefaultTableModel tableModel = tables.get(entry.getKey());
            if(tableModel == null){
                continue;
            }

            if(tableModel.getRowCount() != PeriodStats.STATS_NUM){
                tableModel.setRowCount(PeriodStats.STATS_NUM);

                // 设置表格左边的文字
                tableModel.setValueAt(UiString.LABEL_STAT_TYPE, 0, 0);
                tableModel.setValueAt(UiString.LABEL_STAT_BACK_DEL, 1, 0);
                tableModel.setValueAt(UiString.LABEL_STAT_BACK_IMMEDIATE, 2, 0);
                tableModel.setValueAt(UiString.LABEL_STAT_CUT, 3,0);
                tableModel.setValueAt(UiString.LABEL_STAT_PASTE, 4, 0);
                tableModel.setValueAt(UiString.LABEL_STAT_REMOVE, 5, 0);
                tableModel.setValueAt(UiString.LABEL_STAT_INSERTED, 6, 0);
            }

            // Fill with data
            PeriodStats periodStats = entry.getValue();
            tableModel.setValueAt(prettifyLong(periodStats.type), 0, 1);
            tableModel.setValueAt(prettifyLong(periodStats.backDel), 1, 1);
            tableModel.setValueAt(prettifyLong(periodStats.backImmediate), 2, 1);
            tableModel.setValueAt(prettifyLong(periodStats.cut), 3, 1);
            tableModel.setValueAt(prettifyLong(periodStats.paste), 4, 1);
            tableModel.setValueAt(prettifyLong(periodStats.remove), 5, 1);
            tableModel.setValueAt(prettifyLong(periodStats.insert), 6, 1);
        }
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        System.out.println("creating tool window ");

        this.window = toolWindow;
        createUi();

        // 创建content
        SimpleToolWindowPanel toolWindowPanel = new SimpleToolWindowPanel(true);
        toolWindowPanel.setContent(windowContent);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(toolWindowPanel, "", false);
        window.getContentManager().addContent(content);

        System.out.println("createToolWindowContent");
        callback = ServiceManager.getService(AppComponent.class);

        clearStatsButton.addActionListener((actionEvent -> {
            callback.onStatsResetClicked();
            this.updateData();
        }));

        // 周期性更新数据
        updateTask = JobScheduler.getScheduler().scheduleWithFixedDelay(
                () -> {
                    if(window.isVisible()){
                        // Avoid updating UI from the background thread
                        ApplicationManager.getApplication().invokeLater(this::updateData);
                    }
                },
                0,
                UI_UPDATE_PERIOD,
                TimeUnit.SECONDS
        );
    }
}
