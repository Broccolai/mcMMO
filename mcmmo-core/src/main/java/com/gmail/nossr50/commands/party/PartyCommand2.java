package com.gmail.nossr50.commands.party;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import com.gmail.nossr50.config.ConfigManager;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.party.ShareMode;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent;
import com.gmail.nossr50.locale.LocaleManager;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.StringUtils;
import org.bukkit.entity.Player;

@CommandAlias("party")
@CommandPermission("mcmmo.commands.party")
public class PartyCommand2 extends BaseCommand {
    @Dependency
    private LocaleManager localeManager;
    @Dependency
    private ConfigManager configManager;
    @Dependency
    private PartyManager partyManager;

    @Subcommand("create")
    @CommandPermission("mcmmo.commands.party.create")
    public void onCreate(McMMOPlayer mcMMOPlayer, String name, @Optional String password) {
        if (partyManager.checkPartyExistence(mcMMOPlayer.getPlayer(), name)) {
            return;
        }

        if (!partyManager.changeOrJoinParty(mcMMOPlayer, name)) {
            return;
        }

        partyManager.createParty(mcMMOPlayer, name, password);
    }

    @Subcommand("join")
    @CommandPermission("mcmmo.commands.party.join")
    @CommandCompletion("@Players")
    public void onJoin(McMMOPlayer mcMMOPlayer, McMMOPlayer target, @Optional String password) {
        if (!target.inParty()) {
            throw new InvalidCommandArgument(localeManager.getString("Party.PlayerNotInParty", target.getPlayerName()));
        }

        Party targetParty = target.getParty();

        if (mcMMOPlayer == target || (mcMMOPlayer.inParty() && mcMMOPlayer.getParty().equals(targetParty))) {
            throw new InvalidCommandArgument(localeManager.getString("Party.Join.Self"));
        }

        if (partyManager.checkPartyPassword(mcMMOPlayer.getPlayer(), targetParty, password)) {
            return;
        }

        String partyName = targetParty.getName();

        if (partyManager.changeOrJoinParty(mcMMOPlayer, partyName)) {
            return;
        }

        if (configManager.getConfigParty().getPartyGeneral().isPartySizeCapped() && partyManager.isPartyFull(mcMMOPlayer.getPlayer(), targetParty)) {
            throw new InvalidCommandArgument(localeManager.getString("Commands.Party.PartyFull", targetParty.toString()));
        }

        partyManager.addToParty(mcMMOPlayer, targetParty);
        mcMMOPlayer.getPlayer().sendMessage(localeManager.getString("Commands.Party.Join", partyName));
    }
    
    @Subcommand("quit")
    @CommandPermission("mcmmo.commands.party.quit")
    public void onQuit(McMMOPlayer mcMMOPlayer) {
        Party playerParty = mcMMOPlayer.getParty();

        if (!partyManager.handlePartyChangeEvent(mcMMOPlayer.getPlayer(), playerParty.getName(), null, McMMOPartyChangeEvent.EventReason.LEFT_PARTY)) {
            return;
        }

        partyManager.removeFromParty(mcMMOPlayer);
        mcMMOPlayer.getPlayer().sendMessage(localeManager.getString("Commands.Party.Leave"));
    }

    @Subcommand("accept")
    @CommandPermission("mcmmo.commands.party.accept")
    public void onAccept(McMMOPlayer mcMMOPlayer) {
        if (!mcMMOPlayer.hasPartyInvite()) {
            throw new InvalidCommandArgument(localeManager.getString("mcMMO.NoInvites"));
        }

        if (partyManager.changeOrJoinParty(mcMMOPlayer, mcMMOPlayer.getPartyInvite().getName())) {
            return;
        }

        partyManager.joinInvitedParty(mcMMOPlayer);
    }

    @Subcommand("xpshare")
    @CommandPermission("mcmmo.commands.party.xpshare")
    @CommandCompletion("@ShareMode:exclude=random")
    public void onXpshare(McMMOPlayer mcMMOPlayer, ShareMode mode) {
        Party party = mcMMOPlayer.getParty();

        if (party.getLevel() < partyManager.getPartyFeatureUnlockLevel(PartyFeature.XP_SHARE)) {
            throw new InvalidCommandArgument(localeManager.getString("Party.Feature.Disabled.5"));
        }

        party.setXpShareMode(mode);

        String changeModeMessage = localeManager.getString("Commands.Party.SetSharing", localeManager.getString("Party.ShareType.Xp"), localeManager.getString("Party.ShareMode." + StringUtils.getCapitalized(mode.toString())));

        for (Player member : party.getOnlineMembers()) {
            member.sendMessage(changeModeMessage);
        }
    }

    @Subcommand("itemshare")
    @CommandPermission("mcmmo.commands.party.itemshare")
    @CommandCompletion("@ShareMode")
    public void onItemshare(McMMOPlayer mcMMOPlayer) {}
}
