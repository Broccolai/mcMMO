package com.gmail.nossr50.commands;

import co.aikar.commands.annotation.CommandAlias;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import org.bukkit.command.CommandSender;

@CommandAlias("mcgod")
public class GodModeCommand extends ToggleCommandTODO {
    @Override
    protected boolean hasOtherPermission(CommandSender sender) {
        return permissionTools.mcgodOthers(sender);
    }

    @Override
    protected boolean hasSelfPermission(CommandSender sender) {
        return permissionTools.mcgod(sender);
    }

    @Override
    protected void applyCommandAction(McMMOPlayer mcMMOPlayer) {
        mcMMOPlayer.getPlayer().sendMessage(localeManager.getString("Commands.GodMode." + (mcMMOPlayer.getGodMode() ? "Disabled" : "Enabled")));
        mcMMOPlayer.toggleGodMode();
    }

    @Override
    protected void sendSuccessMessage(CommandSender sender, String playerName) {
        sender.sendMessage(localeManager.getString("Commands.GodMode.Toggle", playerName));
    }
}
