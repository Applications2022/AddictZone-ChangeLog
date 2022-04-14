package de.ruben.changelog;

import de.ruben.changelog.changelog.ChangeLog;
import de.ruben.changelog.changelog.ChangeLogCommand;
import de.ruben.changelog.changelog.ChangeLogInventory;
import de.ruben.changelog.changelog.ChangeLogService;
import de.ruben.changelog.util.TaskUtil;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.SmartInvsPlugin;
import fr.minuskube.inv.content.InventoryContents;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class ChangeLogPlugin extends JavaPlugin {

    public List<ChangeLog> changeLogs = new ArrayList<>();

    public Date lastUpdated = new Date();

    public InventoryManager inventoryManager;

    public ClickableItem[] clickableItems;

    @Override
    public void onEnable() {
        this.inventoryManager = new InventoryManager(this);
        this.inventoryManager.init();

        getCommand("changelog").setExecutor(new ChangeLogCommand(this));

        new ChangeLogService(this).startChangeLogUpdater(120);



    }

    @Override
    public void onDisable() {
        new ChangeLogService(this).shutdownSchedulerExecutorService();
        new TaskUtil().shutdown();
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public ClickableItem[] getClickableItems() {
        return clickableItems;
    }

    public void setClickableItems(ClickableItem[] clickableItems) {
        this.clickableItems = clickableItems;
    }
}
