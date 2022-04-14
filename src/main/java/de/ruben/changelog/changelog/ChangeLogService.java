package de.ruben.changelog.changelog;

import de.ruben.changelog.ChangeLogPlugin;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ChangeLogService {

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private ChangeLogFetcher changeLogFetcher;

    private ChangeLogPlugin changelogPlugin;


    public ChangeLogService(ChangeLogPlugin changelogPlugin) {
        this.changelogPlugin = changelogPlugin;
        this.changeLogFetcher = new ChangeLogFetcher();
    }

    public List<ChangeLog> getChangeLogs(){
        return changelogPlugin.changeLogs.isEmpty() ? changeLogFetcher.fetchChangeLogs() : changelogPlugin.changeLogs;
    }

    public ChangeLog getChangeLogById(UUID uuid){
        return getChangeLogs().stream().filter(changeLog -> changeLog.getUuid() == uuid).findFirst().get();
    }

    public List<ChangeLog> getChangeLogByCreator(String creator){
        return getChangeLogs().stream().filter(changeLog -> changeLog.getCreator().toLowerCase().contains(creator.toLowerCase())).collect(Collectors.toList());
    }

    public List<ChangeLog> getChangeLogByType(ChangeLogType type){
        return getChangeLogs().stream().filter(changeLog -> changeLog.getChangeLogType() == type).collect(Collectors.toList());
    }

    public List<ChangeLog> getChangeLogByTitle(String title){
        return getChangeLogs().stream().filter(changeLog -> changeLog.getTitle().contains(title)).collect(Collectors.toList());
    }

    public List<ChangeLog> getChangeLogsSortedByDate(){
        return getChangeLogs().stream().sorted((o1, o2) -> o2.getCreateDate().compareTo(o1.getCreateDate())).collect(Collectors.toList());
    }

    public List<ChangeLog> getChangeLogsSortedByDateReverse(){
        return getChangeLogs().stream().sorted(Comparator.comparing(ChangeLog::getCreateDate)).collect(Collectors.toList());
    }

    public List<ChangeLog> getChangeLogsGroupedByType(){
        return getChangeLogs().stream().sorted(Comparator.comparing(ChangeLog::getChangeLogType)).collect(Collectors.toList());
    }

    public List<ChangeLog> getChangeLogsGroupedByCreator(){
        return getChangeLogs().stream().sorted(Comparator.comparing(ChangeLog::getCreator)).collect(Collectors.toList());
    }

    public void startChangeLogUpdater(long interval){


        scheduledExecutorService.scheduleAtFixedRate(() -> {

            changelogPlugin.changeLogs.clear();
            changelogPlugin.changeLogs.addAll(changeLogFetcher.fetchChangeLogs());

            setLastUpdate(new Date(System.currentTimeMillis()));

            ChangeLogInventory inventory = new ChangeLogInventory(changelogPlugin);

            changelogPlugin.inventoryManager.getOpenedPlayers(inventory.getInventory()).forEach(player -> inventory.openInventory(player, 0));


        }, 0, interval, TimeUnit.SECONDS);

    }

    public void shutdownSchedulerExecutorService(){
        scheduledExecutorService.shutdown();
    }

    public void setLastUpdate(Date lastUpdate) {
        changelogPlugin.setLastUpdated(lastUpdate);
    }

    public Date getLastUpdate() {
        return changelogPlugin.getLastUpdated();
    }
}
