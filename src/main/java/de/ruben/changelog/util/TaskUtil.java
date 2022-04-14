package de.ruben.changelog.util;

import de.ruben.changelog.changelog.ChangeLog;

import java.util.List;
import java.util.concurrent.*;

public class TaskUtil {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    public TaskUtil() {
    }



    /** Running task every 'interval' milliseconds */
    public void runTaskAtFixedTimeRate(Runnable runnable, long interval){
        scheduledExecutorService.scheduleAtFixedRate(runnable, 0, interval, TimeUnit.MILLISECONDS);
    }

    /** Running task */
    public void runTask(Runnable runnable){
        scheduledExecutorService.execute(runnable);
    }

    /** Running task after 'delay' milliseconds */
    public void runTaskWithDelay(Runnable runnable, int delay){
        scheduledExecutorService.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    /** Shutting down the executorservice */
    public void shutdown(){
        scheduledExecutorService.shutdownNow();
    }

    public Future<?> submitTask(Runnable runnable) {
        return scheduledExecutorService.submit(runnable);
    }

    public Future<?> submitTask(Callable<?> callable) {
        return scheduledExecutorService.submit(callable);
    }

    public CompletableFuture<List<ChangeLog>> submitTaskListChangeLog(List<ChangeLog> changeLogs) {
        return CompletableFuture.supplyAsync(() -> changeLogs);
    }


}