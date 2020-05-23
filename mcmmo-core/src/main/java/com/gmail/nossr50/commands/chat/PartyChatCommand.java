package com.gmail.nossr50.commands.chat;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import com.gmail.nossr50.commands.exceptions.ProfileNotLoaded;
import com.gmail.nossr50.datatypes.chat.ChatMode;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("partychat")
@CommandPermission("mcmmo.chat.partychat")
@Description("%description.partychat")
public class PartyChatCommand extends ChatCommand {
    public PartyChatCommand() {
        super(ChatMode.PARTY);
    }

    @Override
    protected void handleChatSending(CommandSender sender, String[] args) {
        Party party;
        String message;

        if (sender instanceof Player) {
            McMMOPlayer mcMMOPlayer = userManager.getPlayer((Player) sender);

            if (mcMMOPlayer == null)
                throw new ProfileNotLoaded(localeManager);

            party = mcMMOPlayer.getParty();

            if (party == null) {
                sender.sendMessage(localeManager.getString("Commands.Party.None"));
                return;
            }

            if (party.getLevel() < partyManager.getPartyFeatureUnlockLevel(PartyFeature.CHAT)) {
                sender.sendMessage(localeManager.getString("Party.Feature.Disabled.1"));
                return;
            }

            message = buildChatMessage(args, 0);
        } else {
            if (args.length < 2) {
                sender.sendMessage(localeManager.getString("Party.Specify"));
                return;
            }

            party = partyManager.getParty(args[0]);

            if (party == null) {
                sender.sendMessage(localeManager.getString("Party.InvalidName"));
                return;
            }

            message = buildChatMessage(args, 1);
        }

        chatManager.processPartyChat(party, getDisplayName(sender), message);
    }
}
