package com.gmail.nossr50.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import com.gmail.nossr50.commands.exceptions.CommandDisabled;
import com.gmail.nossr50.config.scoreboard.ConfigScoreboard;
import com.gmail.nossr50.locale.LocaleManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import org.bukkit.entity.Player;

@CommandAlias("mcscoreboard")
public class ScoreboardCommand extends BaseCommand {
    @Dependency
    private LocaleManager localeManager;
    @Dependency
    private ConfigScoreboard scoreboardSettings;
    @Dependency
    private ScoreboardManager scoreboardManager;

    @Subcommand("clear|reset")
    public void onClear(Player player) {
        scoreboardManager.clearBoard(player.getName());
        player.sendMessage(localeManager.getString("Commands.Scoreboard.Clear"));
    }

    @Subcommand("keep")
    public void onKeep(Player player) {
        if (!scoreboardSettings.getScoreboardsEnabled()) {
            throw new CommandDisabled(localeManager);
        }

        if (!scoreboardManager.isBoardShown(player.getName())) {
            throw new InvalidCommandArgument(localeManager.getString("Commands.Scoreboard.NoBoard"));
        }

        scoreboardManager.keepBoard(player.getName());
        player.sendMessage(localeManager.getString("Commands.Scoreboard.Keep"));
    }

    @Subcommand("timer|time")
    public void onTimer(Player player, Integer time) {
        scoreboardManager.setRevertTimer(player.getName(), time);
        player.sendMessage(localeManager.getString("Commands.Scoreboard.Timer", time));
    }
}
