package com.gmail.nossr50.commands.database;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.gmail.nossr50.commands.exceptions.ProfileNotLoaded;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.locale.LocaleManager;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.commands.CommandTools;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@CommandAlias("mcremove")
@CommandPermission("mcmmo.commands.mcremove")
@Description("%description.mcremove")
public class McremoveCommand extends BaseCommand {
    @Dependency
    private mcMMO pluginRef;
    @Dependency
    private CommandTools commandTools;
    @Dependency
    private LocaleManager localeManager;
    @Dependency
    private DatabaseManager databaseManager;

    @Default
    @CommandCompletion("@Players")
    public void onCommand(CommandSender sender, OfflinePlayer offlinePlayer) {
        if (commandTools.unloadedProfile(sender, pluginRef.getDatabaseManager().loadPlayerProfile(offlinePlayer.getName(), offlinePlayer.getUniqueId(), false))) {
            throw new ProfileNotLoaded(localeManager);
        }

        if (databaseManager.removeUser(offlinePlayer.getName(), offlinePlayer.getUniqueId())) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.mcremove.Success", offlinePlayer.getName()));
        } else {
            sender.sendMessage(offlinePlayer.getName() + " could not be removed from the database."); // Pretty sure this should NEVER happen.
        }
    }
}
