package com.gmail.nossr50.commands.database;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import com.gmail.nossr50.locale.LocaleManager;
import com.gmail.nossr50.mcMMO;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

@CommandAlias("mmoshowdb")
@CommandPermission("mcmmo.commands.mmoshowdb")
@Description("%description.mmoshowdb")
public class ShowDatabaseCommand extends BaseCommand {
    @Dependency
    private mcMMO pluginRef;
    @Dependency
    private LocaleManager localeManager;

    @Default
    public void onCommand(CommandSender sender) {
        // TODO: Is this needed?
        Class<?> clazz = pluginRef.getDatabaseManagerFactory().getCustomDatabaseManagerClass();

        if (clazz != null) {
            throw new InvalidCommandArgument(localeManager.getString("Commands.mmoshowdb", clazz.getName()));
        }

        sender.sendMessage(localeManager.getString("Commands.mmoshowdb", (pluginRef.getMySQLConfigSettings().isMySQLEnabled() ? "sql" : "flatfile")));
    }
}
