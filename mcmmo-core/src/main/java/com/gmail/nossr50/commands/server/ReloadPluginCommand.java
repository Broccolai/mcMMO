package com.gmail.nossr50.commands.server;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.gmail.nossr50.locale.LocaleManager;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.PermissionTools;
import com.gmail.nossr50.util.commands.CommandTools;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@Subcommand("mcmmoreload")
@CommandPermission("mcmmo.commands.reload")
@Description("%description.mcmmoreload")
public class ReloadPluginCommand extends BaseCommand {
    @Dependency
    private mcMMO pluginRef;
    @Dependency
    private CommandTools commandTools;
    @Dependency
    private PermissionTools permissionTools;
    @Dependency
    private LocaleManager localeManager;

    @Default
    public void onCommand(CommandSender sender) {
        commandTools.hasPermission(permissionTools.reload(sender));

        Bukkit.broadcastMessage(localeManager.getString("Commands.Reload.Start"));
        pluginRef.reload();
        Bukkit.broadcastMessage(localeManager.getString("Commands.Reload.Finished"));
    }
}
