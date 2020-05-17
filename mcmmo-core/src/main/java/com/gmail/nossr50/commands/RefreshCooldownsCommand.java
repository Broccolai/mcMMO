package com.gmail.nossr50.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import org.bukkit.command.CommandSender;

@CommandAlias("mcrefresh")
@Description("%description.mcrefresh")
public class RefreshCooldownsCommand extends ToggleCommand {
    @Override
    protected boolean hasOtherPermission(CommandSender sender) {
        return permissionTools.mcrefreshOthers(sender);
    }

    @Override
    protected boolean hasSelfPermission(CommandSender sender) {
        return permissionTools.mcrefresh(sender);
    }

    @Override
    protected void applyCommandAction(McMMOPlayer mcMMOPlayer) {
        mcMMOPlayer.setRecentlyHurt(0);
        mcMMOPlayer.resetCooldowns();
        mcMMOPlayer.resetToolPrepMode();
        mcMMOPlayer.resetSuperAbilityMode();

        mcMMOPlayer.getPlayer().sendMessage(localeManager.getString("Ability.Generic.Refresh"));
    }

    @Override
    protected void sendSuccessMessage(CommandSender sender, String playerName) {
        sender.sendMessage(localeManager.getString("Commands.mcrefresh.Success", playerName));
    }
}
