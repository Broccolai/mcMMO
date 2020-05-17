package com.gmail.nossr50.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.gmail.nossr50.commands.party.PartySubcommandType;
import com.gmail.nossr50.mcMMO;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("mcmmo")
@Description("%description.mcmmo")
public class McMMOCommand extends BaseCommand {
    @Dependency
    private mcMMO pluginRef;

    @Default
    @CommandPermission("mcmmo.commands.mcmmo.description")
    public void onDefault(CommandSender sender) {
        String[] description = pluginRef.getLocaleManager().getString("mcMMO.Description").split(",");
        sender.sendMessage(description);
        sender.sendMessage(pluginRef.getLocaleManager().getString("mcMMO.Description.FormerDevs"));

        if (pluginRef.getConfigManager().getConfigAds().isShowDonationInfo()) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("MOTD.Donate"));
            sender.sendMessage(ChatColor.GOLD + " - " + ChatColor.GREEN + "nossr50@gmail.com" + ChatColor.GOLD + " Paypal");
        }

        sender.sendMessage(pluginRef.getLocaleManager().getString("MOTD.Version", pluginRef.getDescription().getVersion()));
    }

    @Subcommand("help|?|commands")
    @CommandPermission("mcmmo.commands.mcmmo.help")
    public void onHelp(CommandSender sender) {
        sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.mcc.Header"));
        displayGeneralCommands(sender);
        displayOtherCommands(sender);
        displayPartyCommands(sender);
    }

    private void displayGeneralCommands(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_AQUA + " /mcstats " + pluginRef.getLocaleManager().getString("Commands.Stats"));
        sender.sendMessage(ChatColor.DARK_AQUA + " /<skill>" + pluginRef.getLocaleManager().getString("Commands.SkillInfo"));
        sender.sendMessage(ChatColor.DARK_AQUA + " /mctop " + pluginRef.getLocaleManager().getString("Commands.Leaderboards"));

        if (pluginRef.getPermissionTools().inspect(sender)) {
            sender.sendMessage(ChatColor.DARK_AQUA + " /inspect " + pluginRef.getLocaleManager().getString("Commands.Inspect"));
        }

        if (pluginRef.getPermissionTools().mcability(sender)) {
            sender.sendMessage(ChatColor.DARK_AQUA + " /mcability " + pluginRef.getLocaleManager().getString("Commands.ToggleAbility"));
        }
    }

    private void displayOtherCommands(CommandSender sender) {
        //Don't show them this category if they have none of the permissions
        if (!pluginRef.getPermissionTools().skillreset(sender) && !pluginRef.getPermissionTools().mmoedit(sender) && !pluginRef.getPermissionTools().adminChat(sender) && !pluginRef.getPermissionTools().mcgod(sender))
            return;

        sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Other"));

        if (pluginRef.getPermissionTools().skillreset(sender)) {
            sender.sendMessage(ChatColor.DARK_AQUA + " /skillreset <skill|all> " + pluginRef.getLocaleManager().getString("Commands.Reset"));
        }

        if (pluginRef.getPermissionTools().mmoedit(sender)) {
            sender.sendMessage(ChatColor.DARK_AQUA + " /mmoedit " + pluginRef.getLocaleManager().getString("Commands.mmoedit"));
        }

        if (pluginRef.getPermissionTools().adminChat(sender)) {
            sender.sendMessage(ChatColor.DARK_AQUA + " /adminchat " + pluginRef.getLocaleManager().getString("Commands.AdminToggle"));
        }

        if (pluginRef.getPermissionTools().mcgod(sender)) {
            sender.sendMessage(ChatColor.DARK_AQUA + " /mcgod " + pluginRef.getLocaleManager().getString("Commands.mcgod"));
        }
    }

    private void displayPartyCommands(CommandSender sender) {
        if (pluginRef.getPermissionTools().party(sender)) {
            sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.Party.Commands"));
            sender.sendMessage(ChatColor.DARK_AQUA + " /party create <" + pluginRef.getLocaleManager().getString("Commands.Usage.PartyName") + "> " + pluginRef.getLocaleManager().getString("Commands.Party1"));
            sender.sendMessage(ChatColor.DARK_AQUA + " /party join <" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + "> " + pluginRef.getLocaleManager().getString("Commands.Party2"));
            sender.sendMessage(ChatColor.DARK_AQUA + " /party quit " + pluginRef.getLocaleManager().getString("Commands.Party.Quit"));

            if (pluginRef.getPermissionTools().partyChat(sender)) {
                sender.sendMessage(ChatColor.DARK_AQUA + " /party chat " + pluginRef.getLocaleManager().getString("Commands.Party.Toggle"));
            }

            sender.sendMessage(ChatColor.DARK_AQUA + " /party invite <" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + "> " + pluginRef.getLocaleManager().getString("Commands.Party.Invite"));
            sender.sendMessage(ChatColor.DARK_AQUA + " /party accept " + pluginRef.getLocaleManager().getString("Commands.Party.Accept"));

            if (pluginRef.getPermissionTools().partySubcommand(sender, PartySubcommandType.TELEPORT)) {
                sender.sendMessage(ChatColor.DARK_AQUA + " /party teleport <" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + "> " + pluginRef.getLocaleManager().getString("Commands.Party.Teleport"));
            }
        }
    }
}