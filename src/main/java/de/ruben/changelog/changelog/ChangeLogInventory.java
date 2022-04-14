package de.ruben.changelog.changelog;

import com.google.common.base.Stopwatch;
import de.ruben.changelog.ChangeLogPlugin;
import de.ruben.changelog.util.ItemBuilder;
import de.ruben.changelog.util.TaskUtil;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.*;
import org.apache.commons.lang.time.StopWatch;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangeLogInventory implements InventoryProvider {


    private ChangeLogPlugin changeLogPlugin;
    private ChangeLogService changeLogService;
    private TaskUtil taskUtil;
    private Boolean reversed;

    private final SimpleDateFormat yearMonthDay = new SimpleDateFormat("dd.MM.yyyy");
    private final SimpleDateFormat hourSecondMillisecond = new SimpleDateFormat("hh:mm:ss");


    public ChangeLogInventory(ChangeLogPlugin changeLogPlugin) {
        this.taskUtil = new TaskUtil();
        this.changeLogPlugin = changeLogPlugin;
        this.changeLogService = new ChangeLogService(changeLogPlugin);
        this.reversed = false;
    }

    public void openInventory(Player player, int page) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        getInventory().open(player, page);
        stopwatch.stop();
        System.out.println("2. Time elapsed: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();

        contents.fillBorders(ClickableItem.empty(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("").build()));


        List<ChangeLog> defaultSortedChangelogs = reversed ? changeLogService.getChangeLogsSortedByDateReverse() : changeLogService.getChangeLogsSortedByDate();


        ClickableItem[] changeLogItems = new ClickableItem[defaultSortedChangelogs.size()];

        for (int i = 0; i < defaultSortedChangelogs.size(); i++) {
            ChangeLog changeLog = defaultSortedChangelogs.get(i);

            changeLogItems[i] = ClickableItem.empty(
                    new ItemBuilder(Material.PAPER)
                            .setDisplayName(formatColors(changeLog.getTitle()))
                            .setLore(getLore(changeLog))
                            .build()
            );
        }


        pagination.setItems(changeLogItems);

        pagination.setItemsPerPage(changeLogItems.length < 21 ? changeLogItems.length : 21);

        for (int i = 0; i < pagination.getPageItems().length; ++i) {
            contents.add(pagination.getPageItems()[i]);
        }

        if (!pagination.isFirst()) {
            contents.set(4, 3, ClickableItem.of(new ItemStack(Material.ARROW),
                    e -> openInventory(player, pagination.previous().getPage())));
        }

        if (!pagination.isLast()) {
            contents.set(4, 5, ClickableItem.of(new ItemStack(Material.ARROW),
                    e -> openInventory(player, pagination.next().getPage())));
        }

        contents.set(4, 4, ClickableItem.empty(new ItemBuilder(Material.TORCH)
                .setDisplayName("§6§lInfo")
                .setLore(Arrays.asList(" ", "§7Letzte Aktualisierung vor §b" + getTimeBetweenDateAndNow(changeLogService.getLastUpdate())))
                .build()
        ));

        contents.set(4, 1, ClickableItem.of(
                new ItemBuilder(Material.MAP)
                        .setDisplayName("§bSortierung")
                        .setLore(Arrays.asList(reversed ? "§7Neuste zuerst" : "§bNeuste zuerst", reversed ? "§bÄlteste zuerst" : "§7Älteste zuerst"))
                        .build(),
                inventoryClickEvent -> {
                    reversed = reversed ? false : true;
                    openInventory(player, pagination.getPage());
                }
        ));
    };

    private String getFormattedType(ChangeLogType changeLogType) {
        if (changeLogType == ChangeLogType.NEW) {
            return ChatColor.GREEN + "§lNeu";
        } else if (changeLogType == ChangeLogType.CHANGE) {
            return ChatColor.BLUE + "§lÄnderung";
        } else if (changeLogType == ChangeLogType.BUG_FIX) {
            return ChatColor.RED + "§lBug-Fix";
        } else {
            return ChatColor.DARK_RED + "§lKein Typ!";
        }
    }

    private List<String> getLore(ChangeLog changeLog) {


        List<String> lore = new ArrayList<>();

        lore.add(" ");
        lore.add("§7Ersteller: §b§l" + changeLog.getCreator());
        lore.add("§7Typ: " + getFormattedType(changeLog.getChangeLogType()));
        lore.add(" ");

        String[] rows = changeLog.getMessage().split("\n");

        for (String row : rows) {
            lore.add("§7" + formatColors(row));
        }

        lore.add(" ");

        lore.add("§7Erstellt vor §b" + getTimeBetweenDateAndNow(changeLog.getCreateDate()));

//        lore.add("§7Erstellt am §b" + yearMonthDay.format(changeLog.getCreateDate()));
//        lore.add("§7um §b" + hourSecondMillisecond.format(changeLog.getCreateDate()) + " §7Uhr");

        return lore;
    }

    public SmartInventory getInventory() {
        return SmartInventory.builder()
                .manager(changeLogPlugin.inventoryManager)
                .id("changeLogInventory")
                .provider(this)
                .size(5, 9)
                .title("§b§lChangeLogs")
                .build();
    }

    private String formatColors(String message) {
        Pattern pattern = Pattern.compile("#[A-Fa-f0-9]{6}");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            message = message.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
            matcher = pattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private String getTimeBetweenDateAndNow(Date date) {
        Duration duration = Duration.between(date.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDateTime(), LocalDateTime.now(ZoneId.of("Europe/Berlin")));

        long durationInSeconds = duration.getSeconds();

        String days = (durationInSeconds / 60 / 60 / 24) > 0 ? (durationInSeconds / 60 / 60 / 24) + "d " : "";
        durationInSeconds -= (durationInSeconds / 60 / 60 / 24) * (60 * 60 * 24);
        String hours = (durationInSeconds / 60 / 60) > 0 ? (durationInSeconds / 60 / 60) + "h " : "";
        durationInSeconds -= (durationInSeconds / 60 / 60) * (60 * 60);
        String minutes = (durationInSeconds / 60) > 0 ? (durationInSeconds / 60) + "min" : "";
        durationInSeconds -= (durationInSeconds / 60) * (60);

        return (days + hours + minutes).equals("") ? durationInSeconds + "s" : (days + hours + minutes);
    }
}
