package com.gmail.nossr50.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.locale.LocaleManager;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.entity.Player;

@CommandAlias("mcnotify")
public class ChatNotificationToggleCommand extends BaseCommand {
    @Dependency
    private UserManager userManager;
    @Dependency
    private LocaleManager localeManager;

    @Default
    public void onCommand(Player player) {
        McMMOPlayer mcMMOPlayer = userManager.getPlayer(player);

        //Not Loaded yet
        if (mcMMOPlayer == null) {
            throw new InvalidCommandArgument(localeManager.getString("Profile.PendingLoad"));
        }

        player.sendMessage(localeManager.getString("Commands.Notifications." + (mcMMOPlayer.useChatNotifications() ? "Off" : "On")));
        mcMMOPlayer.toggleChatNotifications();
    }
}
