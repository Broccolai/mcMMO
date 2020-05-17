package com.gmail.nossr50.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.gmail.nossr50.config.ConfigManager;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.database.FlatFileDatabaseManager;
import com.gmail.nossr50.database.SQLDatabaseManager;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.commands.CommandTools;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

@CommandAlias("mhd")
@CommandPermission("mcmmo.commands.mhd")
// TODO: Needs Localisation
@Description("Resets all mob health bar settings for all players to the default")
public class ResetUserHealthBarSettingsCommand extends BaseCommand {
    @Dependency
    private UserManager userManager;
    @Dependency
    private ConfigManager configManager;
    @Dependency
    private DatabaseManager databaseManager;

    @Default
    public void onCommand(CommandSender sender) {
        if (databaseManager instanceof SQLDatabaseManager) {
            SQLDatabaseManager sqlDatabaseManager = (SQLDatabaseManager) databaseManager;
            sqlDatabaseManager.resetMobHealthSettings();

            for (McMMOPlayer player : userManager.getPlayers()) {
                player.getProfile().setMobHealthbarType(configManager.getConfigMobs().getCombat().getHealthBars().getDisplayBarType());
            }

            sender.sendMessage("Mob health reset");
        } else if (databaseManager instanceof FlatFileDatabaseManager) {
            FlatFileDatabaseManager flatFileDatabaseManager = (FlatFileDatabaseManager) databaseManager;
            flatFileDatabaseManager.resetMobHealthSettings();

            for (McMMOPlayer player : userManager.getPlayers()) {
                player.getProfile().setMobHealthbarType(configManager.getConfigMobs().getCombat().getHealthBars().getDisplayBarType());
            }

            sender.sendMessage("Mob health reset");
        }
    }
}
