package com.maff.codingstatisticmanager;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.maff.codingstatisticmanager.data.CodingStats;
import com.maff.codingstatisticmanager.data.DatabaseStatsRepository;
import com.maff.codingstatisticmanager.ui.StatsWindowFactory;

public class AppComponent implements StatsWindowFactory.CallBack {
    private DatabaseStatsRepository dbRepository;
    private IdeActivityTracker ideActivityTracker;

    public AppComponent() {
        System.out.println("init AppComponent");

        this.dbRepository = DatabaseStatsRepository.getInstance();
        this.ideActivityTracker = new IdeActivityTracker(
                ApplicationManager.getApplication(), dbRepository
        );
    }

    public static AppComponent getInstance() {
        return ServiceManager.getService(AppComponent.class);
    }

    @Override
    public CodingStats getStats() {
        return this.ideActivityTracker.getStats();
    }

    @Override
    public void onStatsResetClicked() {
        this.ideActivityTracker.resetStats();
    }
}
