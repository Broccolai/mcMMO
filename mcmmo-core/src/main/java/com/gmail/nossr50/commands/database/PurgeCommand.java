package com.gmail.nossr50.commands.database;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.locale.LocaleManager;
import com.gmail.nossr50.mcMMO;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

@CommandAlias("mcpurge")
@CommandPermission("mcmmo.commands.mcpurge")
@Description("%description.mcpurge")
public class PurgeCommand extends BaseCommand {
    @Dependency
    private mcMMO pluginRef;
    @Dependency
    private LocaleManager localeManager;
    @Dependency
    private DatabaseManager databaseManager;

    @Default
    public void onCommand(CommandSender sender) {
        databaseManager.purgePowerlessUsers();

        if (pluginRef.getDatabaseCleaningSettings().getOldUserCutoffMonths() != -1) {
            databaseManager.purgeOldUsers();
        }

        sender.sendMessage(localeManager.getString("Commands.mcpurge.Success"));
    }
}
