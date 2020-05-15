package com.gmail.nossr50.commands;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("mcgod")
public class GodModeCommand extends McMMOBaseCommand {
    @Default
    @CommandCompletion("@Players")
    public void onCommand(CommandSender sender, @Optional McMMOPlayer targetPlayer) {
        if (targetPlayer != null) {
            if (!pluginRef.getPermissionTools().mcgod(sender)) {
                throw new InvalidCommandArgument(pluginRef.getLocaleManager().getString("mcMMO.NoPermission", false));
            }

            toggleGodMode(targetPlayer);
            sender.sendMessage(pluginRef.getLocaleManager().getString("Commands.GodMode.Toggle", targetPlayer.getPlayerName()));
        } else {
            Player player = getSenderAsPlayer(sender);
            checkPlayerIsLoaded(player);

            McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(sender.getName());
            toggleGodMode(mcMMOPlayer);
        }
    }

    private void toggleGodMode(McMMOPlayer mcMMOPlayer) {
        mcMMOPlayer.getPlayer().sendMessage(pluginRef.getLocaleManager().getString("Commands.GodMode." + (mcMMOPlayer.getGodMode() ? "Disabled" : "Enabled")));
        mcMMOPlayer.toggleGodMode();
    }
}
