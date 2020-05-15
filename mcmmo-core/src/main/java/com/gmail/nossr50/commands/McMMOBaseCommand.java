package com.gmail.nossr50.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.Dependency;
import com.gmail.nossr50.core.MetadataConstants;
import com.gmail.nossr50.mcMMO;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class McMMOBaseCommand extends BaseCommand {
    @Dependency
    protected mcMMO pluginRef;

    protected Player getSenderAsPlayer(CommandSender sender) {
        if (sender instanceof Player) {
            return (Player) sender;
        }

        throw new InvalidCommandArgument(pluginRef.getLocaleManager().getString("Commands.NoConsole"), false);
    }

    public void checkPlayerIsLoaded(Player player) {
        boolean hasPlayerDataKey = player.hasMetadata(MetadataConstants.PLAYER_DATA_METAKEY);

        if (!hasPlayerDataKey) {
            throw new InvalidCommandArgument(pluginRef.getLocaleManager().getString("Commands.NotLoaded"), false);
        }
    }
}
