package de.nihas101.midas.desktop;

import de.nihas101.midas.config.DesktopConfig;
import de.nihas101.midas.config.MidasConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class DesktopLifecycleService {

    private final ApplicationContext context;
    private final DesktopConfig config;
    private final AtomicInteger activeUiCount = new AtomicInteger(0);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> shutdownTask;

    public DesktopLifecycleService(ApplicationContext context, MidasConfig config) {
        this.context = context;
        this.config = config.getDesktop();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (config.isAutoShutdownEnabled()) {
            log.info("Auto-shutdown is enabled with a grace period of {} seconds.", config.getGracePeriodSeconds());
            checkAndScheduleShutdown();
        }
    }

    public void uiAttached() {
        activeUiCount.incrementAndGet();
        cancelShutdown();
    }

    public void uiDetached() {
        if (activeUiCount.decrementAndGet() <= 0) {
            checkAndScheduleShutdown();
        }
    }

    private synchronized void checkAndScheduleShutdown() {
        if (!config.isAutoShutdownEnabled()) {
            return;
        }

        if (activeUiCount.get() <= 0 && (shutdownTask == null || shutdownTask.isDone())) {
            log.info("No active UIs detected. Scheduling shutdown in {} seconds.", config.getGracePeriodSeconds());
            shutdownTask = scheduler.schedule(this::shutdown, config.getGracePeriodSeconds(), TimeUnit.SECONDS);
        }
    }

    private synchronized void cancelShutdown() {
        if (shutdownTask != null && !shutdownTask.isDone()) {
            log.info("Active UI detected. Cancelling scheduled shutdown.");
            shutdownTask.cancel(false);
        }
    }

    private void shutdown() {
        log.info("Grace period expired with 0 active UIs. Shutting down application...");
        SpringApplication.exit(context, () -> 0);
        System.exit(0);
    }
}
