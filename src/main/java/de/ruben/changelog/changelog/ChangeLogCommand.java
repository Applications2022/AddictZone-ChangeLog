package de.ruben.changelog.changelog;

import de.ruben.changelog.ChangeLogPlugin;
import de.ruben.changelog.util.TaskUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChangeLogCommand implements CommandExecutor {

    private ChangeLogPlugin changeLogPlugin;

    public ChangeLogCommand(ChangeLogPlugin changeLogPlugin) {
        this.changeLogPlugin = changeLogPlugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;

        new ChangeLogInventory(changeLogPlugin).openInventory(player, 0);


        return false;
    }
}
