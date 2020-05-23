package com.gmail.nossr50.commands.chat;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import com.gmail.nossr50.datatypes.chat.ChatMode;
import org.bukkit.command.CommandSender;

@CommandAlias("adminchat|ac|a")
@CommandPermission("mcmmo.chat.adminchat")
@Description("%description.adminchat")
public class AdminChatCommand extends ChatCommand {
    public AdminChatCommand() {
        super(ChatMode.ADMIN);
    }

    @Override
    protected void handleChatSending(CommandSender sender, String[] args) {
        chatManager.processAdminChat(sender.getName(), getDisplayName(sender), buildChatMessage(args, 0));
    }
}
