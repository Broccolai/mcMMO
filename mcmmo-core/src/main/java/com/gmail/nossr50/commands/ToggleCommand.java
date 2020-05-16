package com.gmail.nossr50.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Optional;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleManager;
import com.gmail.nossr50.util.PermissionTools;
import com.gmail.nossr50.util.commands.CommandTools;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class ToggleCommand extends BaseCommand {
    @Dependency
    protected CommandTools commandTools;
    @Dependency
    protected PermissionTools permissionTools;
    @Dependency
    protected UserManager userManager;
    @Dependency
    protected LocaleManager localeManager;


    @Default
    @CommandCompletion("@Players")
    public void onCommand(CommandSender sender, @Optional McMMOPlayer target) {
        if (target == null) {
            Player player = commandTools.getPlayerFromSender(sender);

            commandTools.hasPermission(hasSelfPermission(player));
            commandTools.hasPlayerDataKey2(player);

            applyCommandAction(userManager.getPlayer(sender.getName()));
        } else {
            commandTools.hasPermission(hasOtherPermission(target.getPlayer()));

            applyCommandAction(target);
            sendSuccessMessage(sender, target.getPlayerName());
        }
    }

    protected abstract boolean hasOtherPermission(CommandSender sender);

    protected abstract boolean hasSelfPermission(CommandSender sender);

    protected abstract void applyCommandAction(McMMOPlayer mcMMOPlayer);

    protected abstract void sendSuccessMessage(CommandSender sender, String playerName);
}