package com.gmail.nossr50.commands.chat;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Split;
import com.gmail.nossr50.chat.ChatManager;
import com.gmail.nossr50.datatypes.chat.ChatMode;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleManager;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.commands.CommandTools;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class ChatCommand extends BaseCommand {
    @Dependency
    private CommandTools commandTools;
    @Dependency
    protected UserManager userManager;
    @Dependency
    protected LocaleManager localeManager;
    @Dependency
    protected PartyManager partyManager;
    @Dependency
    protected ChatManager chatManager;

    private final ChatMode chatMode;

    ChatCommand(ChatMode chatMode) {
        this.chatMode = chatMode;
    }

    @Default
    public void onCommand(CommandSender sender, @Split(" ") @Optional String[] args) {
        if (args != null) {
            handleChatSending(sender, args);
            return;
        }

        Player player = commandTools.getPlayerFromSender(sender);
        commandTools.hasPlayerDataKey2(player);

        McMMOPlayer mcMMOPlayer = userManager.getPlayer(player.getName());

        if (mcMMOPlayer.isChatEnabled(chatMode)) {
            disableChatMode(mcMMOPlayer, player);
        } else {
            enableChatMode(mcMMOPlayer, player);
        }
    }

    protected String buildChatMessage(String[] args, int index) {
        StringBuilder builder = new StringBuilder();
        builder.append(args[index]);

        for (int i = index + 1; i < args.length; i++) {
            builder.append(" ");
            builder.append(args[i]);
        }

        return builder.toString();
    }

    protected String getDisplayName(CommandSender sender) {
        return (sender instanceof Player) ? ((Player) sender).getDisplayName() : localeManager.getString("Commands.Chat.Console");
    }

    protected abstract void handleChatSending(CommandSender sender, String[] args);

    private void enableChatMode(McMMOPlayer mcMMOPlayer, CommandSender sender) {
        if (chatMode == ChatMode.PARTY && mcMMOPlayer.getParty() == null) {
            throw new InvalidCommandArgument(localeManager.getString("Commands.Party.None"), false);
        }

        if (chatMode == ChatMode.PARTY && (mcMMOPlayer.getParty().getLevel() < partyManager.getPartyFeatureUnlockLevel(PartyFeature.CHAT))) {
            throw new InvalidCommandArgument(localeManager.getString("Party.Feature.Disabled.1"), false);
        }

        mcMMOPlayer.enableChat(chatMode);
        sender.sendMessage(getChatModeEnabledMessage(chatMode, true));
    }

    private void disableChatMode(McMMOPlayer mcMMOPlayer, CommandSender sender) {
        if (chatMode == ChatMode.PARTY && mcMMOPlayer.getParty() == null) {
            throw new InvalidCommandArgument(localeManager.getString("Commands.Party.None"), false);
        }

        mcMMOPlayer.disableChat(chatMode);
        sender.sendMessage(getChatModeEnabledMessage(chatMode, false));
    }

    private String getChatModeEnabledMessage(ChatMode chatMode, boolean enabled) {
        switch(chatMode) {
            case ADMIN:
                return getAdminMessage(enabled);
            default:
                return getPartyMessage(enabled);
        }
    }

    private String getAdminMessage(boolean enabled) {
        if(enabled)
            return localeManager.getString("Commands.AdminChat.On");
        else
            return localeManager.getString("Commands.AdminChat.Off");
    }

    private String getPartyMessage(boolean enabled) {
        if(enabled)
            return localeManager.getString("Commands.Party.Chat.On");
        else
            return localeManager.getString("Commands.Party.Chat.Off");
    }
}
