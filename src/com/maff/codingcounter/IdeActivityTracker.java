package com.maff.codingcounter;

import com.intellij.concurrency.JobScheduler;
import com.intellij.ide.util.projectWizard.actions.ProjectSpecificAction;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.impl.ProjectWindowAction;
import com.intellij.platform.AttachProjectAction;
import com.maff.codingcounter.data.CodingStats;
import com.maff.codingcounter.data.DatabaseStatsRepository;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class IdeActivityTracker implements Disposable {

    private static final int STATS_SAVE_PERIOD = 300;
    private Logger log = Logger.getInstance(IdeActivityTracker.class);
    //    private StatsRepository repository;
    private StatsCounter statsCounter;
    private ScheduledFuture saveFuture;
    private DatabaseStatsRepository dbRepository;

    public IdeActivityTracker(Disposable parentDisposable, DatabaseStatsRepository dbRepository) {
        this.dbRepository = dbRepository;
        this.statsCounter = new StatsCounter(dbRepository);
        this.startActionListener(parentDisposable);
        this.startPeriodSave();
        Disposer.register(parentDisposable, this);

        System.out.println("created IdeActivityTracker");
    }

    private void saveStats() {
        if (statsCounter.isDirty()) {
            System.out.println("about to save stats to database");
            dbRepository.updateAllDataToDb(this.statsCounter.getStats());
            this.statsCounter.setDirty(false);
        }
    }

    /*
    * 周期性的保存数据到数据库， 初始延迟300秒 每180秒/3分保存一次
    */
    private void startPeriodSave() {
        this.saveFuture = JobScheduler.getScheduler().scheduleWithFixedDelay(
                this::saveStats, 300L, 180L, TimeUnit.SECONDS);
    }

    private void startActionListener(Disposable parentDisposable) {
        ActionManager.getInstance().addAnActionListener(new AnActionListener() {
            @Override
            public void afterActionPerformed(AnAction action, DataContext dataContext, AnActionEvent event) {
                IdeActivityTracker.this.statsCounter.onAction(action, dataContext, event);
            }

            @Override
            public void beforeEditorTyping(char c, DataContext dataContext) {
                IdeActivityTracker.this.statsCounter.onType(c, dataContext);
            }

            @Override
            public void beforeActionPerformed(AnAction anAction, DataContext dataContext, AnActionEvent anActionEvent) {
                if (anAction instanceof AttachProjectAction) {
                    IdeActivityTracker.this.notifyError(new Throwable("AttachProjectAction"));
                }
                if (anAction instanceof ProjectSpecificAction) {
                    IdeActivityTracker.this.notifyError(new Throwable("ProjectSpecificAction"));
                }
                if (anAction instanceof ProjectWindowAction) {
                    IdeActivityTracker.this.notifyError(new Throwable("ProjectWindowAction"));
                }
            }
        }, parentDisposable);
    }

    private void notifyError(Throwable error) {
        ApplicationManager.getApplication().invokeLater(() -> {
            String messageString = error.getMessage();
            String title = "Coding Counter";
            String groupDisplayId = "Coding Counter";
            Notification notification = new Notification(groupDisplayId, title, messageString, NotificationType.ERROR);
            ((Notifications) ApplicationManager.getApplication().getMessageBus().
                    syncPublisher(Notifications.TOPIC)).notify(notification);
        });
    }

    @Override
    public void dispose() {
        this.saveFuture.cancel(false);
        this.saveStats();
        this.dbRepository.disconnectDatabase();
        System.out.println("disposed IdeActivityTracker");
    }

    public CodingStats getStats() {
        return this.statsCounter.getStats();
    }

    public void resetStats() { }
}
