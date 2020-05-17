package com.gmail.nossr50.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.gmail.nossr50.config.ConfigManager;
import com.gmail.nossr50.core.DynamicSettingsManager;
import com.gmail.nossr50.datatypes.notifications.SensitiveCommandType;
import com.gmail.nossr50.locale.LocaleManager;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.PermissionTools;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.commands.CommandTools;
import com.gmail.nossr50.util.player.NotificationManager;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

// TODO: Review this, I've changed the syntax of this command compared to the old one as it didn't make much sense
@CommandAlias("xprate|mcxprate")
@CommandPermission("mcmmo.commands.xprate")
@Description("%description.xprate")
public class ExperienceRateCommand extends BaseCommand {
    @Dependency
    private mcMMO pluginRef;
    @Dependency
    private ConfigManager configManager;
    @Dependency
    private DynamicSettingsManager dynamicSettingsManager;
    @Dependency
    private LocaleManager localeManager;
    @Dependency
    private NotificationManager notificationManager;

    @Subcommand("end|reset|clear")
    @CommandPermission("mcmmo.commands.xprate.reset")
    public void onReset(CommandSender sender) {
        if (pluginRef.isXPEventEnabled()) {
            if (configManager.getConfigEvent().isSendTitleMessages()) {
                notificationManager.broadcastTitle(pluginRef.getServer(),
                        localeManager.getString("Commands.Event.Stop"),
                        localeManager.getString("Commands.Event.Subtitle"),
                        10, 10 * 20, 20);
            }

            if (configManager.getConfigEvent().isBroadcastXPRateEventMessages()) {
                pluginRef.getServer().broadcastMessage(localeManager.getString("Commands.Event.Stop"));
                pluginRef.getServer().broadcastMessage(localeManager.getString("Commands.Event.Stop.Subtitle"));
            }

            notificationManager.processSensitiveCommandNotification(sender, SensitiveCommandType.XPRATE_END);

            pluginRef.toggleXpEventEnabled();
        }

        dynamicSettingsManager.getExperienceManager().resetGlobalXpMult();
    }

    // TODO: Could have a range completion
    @Subcommand("start|set")
    @CommandPermission("mcmmo.commands.xprate.set")
    public void onModify(CommandSender sender, @Conditions("positive") Integer rate) {
        pluginRef.setXPEventEnabled(true);

        if (configManager.getConfigEvent().isSendTitleMessages()) {
            notificationManager.broadcastTitle(pluginRef.getServer(),
                    localeManager.getString("Commands.Event.Start"),
                    localeManager.getString("Commands.Event.XP", rate),
                    10, 10 * 20, 20);
        }

        if (configManager.getConfigEvent().isBroadcastXPRateEventMessages()) {
            pluginRef.getServer().broadcastMessage(localeManager.getString("Commands.Event.Start"));
            pluginRef.getServer().broadcastMessage(localeManager.getString("Commands.Event.XP", rate));
        }

        notificationManager.processSensitiveCommandNotification(sender, SensitiveCommandType.XPRATE_MODIFY, rate.toString());
    }
}
