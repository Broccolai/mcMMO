package com.gmail.nossr50.commands;

import co.aikar.commands.annotation.CommandAlias;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import org.bukkit.command.CommandSender;

@CommandAlias("mcability")
public class AbilityToggleCommand extends ToggleCommand {
    @Override
    protected boolean hasOtherPermission(CommandSender sender) {
        return permissionTools.mcabilityOthers(sender);
    }

    @Override
    protected boolean hasSelfPermission(CommandSender sender) {
        return permissionTools.mcability(sender);
    }

    @Override
    protected void applyCommandAction(McMMOPlayer mcMMOPlayer) {
        mcMMOPlayer.getPlayer().sendMessage(localeManager.getString("Commands.Ability." + (mcMMOPlayer.getAllowAbilityUse() ? "Off" : "On")));
        mcMMOPlayer.toggleAbilityUse();
    }

    @Override
    protected void sendSuccessMessage(CommandSender sender, String playerName) {
        sender.sendMessage(localeManager.getString("Commands.Ability.Toggle", playerName));
    }
}
