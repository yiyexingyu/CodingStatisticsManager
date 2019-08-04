package com.maff.codingcounter;

import com.maff.codingcounter.data.Period;
import com.maff.codingcounter.data.PeriodStats;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.maff.codingcounter.data.DatabaseStatsRepository;

import java.util.Calendar;

public class CodingCounter extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
//        DatabaseStatsRepository databaseStatsRepository = DatabaseStatsRepository.getInstance();
//        System.out.println(databaseStatsRepository.getConnectMessage());
//
//        databaseStatsRepository.selectLastPeriodStats(Period.今天);
//
//        databaseStatsRepository.disconnectDatabase();

        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis());
        System.out.println("moth: " + instance.get(Calendar.MONTH));
        System.out.println("week of year: " + instance.get(Calendar.WEEK_OF_YEAR));
        System.out.println("week of moth: " + instance.get(Calendar.WEEK_OF_MONTH));
    }
}
