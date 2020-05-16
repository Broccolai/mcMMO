package com.gmail.nossr50.commands.chat;

import com.gmail.nossr50.commands.ToggleCommandTODO;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import org.bukkit.command.CommandSender;

public class ChatSpyCommand extends ToggleCommandTODO {
    @Override
    protected boolean hasOtherPermission(CommandSender sender) {
        return permissionTools.adminChatSpyOthers(sender);
    }

    @Override
    protected boolean hasSelfPermission(CommandSender sender) {
        return permissionTools.adminChatSpy(sender);
    }

    @Override
    protected void applyCommandAction(McMMOPlayer mcMMOPlayer) {
        mcMMOPlayer.getPlayer().sendMessage(localeManager.getString("Commands.AdminChatSpy." + (mcMMOPlayer.isPartyChatSpying() ? "Disabled" : "Enabled")));
        mcMMOPlayer.togglePartyChatSpying();
    }

    @Override
    protected void sendSuccessMessage(CommandSender sender, String playerName) {
        sender.sendMessage(localeManager.getString("Commands.AdminChatSpy.Toggle", playerName));
    }
}
