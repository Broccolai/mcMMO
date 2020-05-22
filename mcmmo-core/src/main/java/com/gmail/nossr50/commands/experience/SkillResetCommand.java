package com.gmail.nossr50.commands.experience;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.locale.LocaleManager;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventManager;
import com.gmail.nossr50.util.PermissionTools;
import com.gmail.nossr50.util.commands.CommandTools;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillTools;
import com.google.common.collect.ImmutableList;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@CommandAlias("skillreset")
@Description("%description.skillreset")
public class SkillResetCommand extends BaseCommand {
    @Dependency
    private CommandTools commandTools;
    @Dependency
    private PermissionTools permissionTools;
    @Dependency
    private SkillTools skillTools;
    @Dependency
    private UserManager userManager;
    @Dependency
    private LocaleManager localeManager;
    @Dependency
    private EventManager eventManager;
    @Dependency
    private DatabaseManager databaseManager;

    @Default
    @CommandCompletion("@Players @Skills")
    public void onCommand(CommandSender sender, OfflinePlayer target, PrimarySkillType skill) {
        if (sender instanceof Player && target.getUniqueId() == ((Player) sender).getUniqueId()) {
            commandTools.hasPermission(permissionTools.skillreset(sender));
        } else {
            commandTools.hasPermission(permissionTools.skillresetOthers(sender));
        }

        if (skill != null && skillTools.isChildSkill(skill)) {
            throw new InvalidCommandArgument(localeManager.getString("Commands.Skill.ChildSkill"));
        }

        McMMOPlayer mcMMOPlayer = userManager.getOfflinePlayer(target);

        // If the mcMMOPlayer doesn't exist, create a temporary profile and check if it's present in the database. If it's not, abort the process.
        if (mcMMOPlayer == null) {
            UUID uuid = target.getUniqueId();

            PlayerProfile profile = databaseManager.loadPlayerProfile(target.getName(), uuid, false);

            if (commandTools.unloadedProfile(sender, profile)) {
                return;
            }

            editValues(null, profile, skill);
        } else {
            editValues(mcMMOPlayer.getPlayer(), mcMMOPlayer.getProfile(), skill);
        }

        handleSenderMessage(sender, target.getName(), skill);
    }

    private void handleSenderMessage(CommandSender sender, String playerName, PrimarySkillType skill) {
        if (skill == null) {
            sender.sendMessage(localeManager.getString("Commands.addlevels.AwardAll.2", playerName));
        } else {
            sender.sendMessage(localeManager.getString("Commands.addlevels.AwardSkill.2", skillTools.getLocalizedSkillName(skill), playerName));
        }
    }

    private void handleCommand(Player player, PlayerProfile profile, PrimarySkillType skill) {
        int levelsRemoved = profile.getSkillLevel(skill);
        double xpRemoved = profile.getSkillXpLevelRaw(skill);

        profile.modifySkill(skill, 0);

        if (player == null) {
            profile.scheduleAsyncSave();
            return;
        }

        eventManager.tryLevelChangeEvent(player, skill, levelsRemoved, xpRemoved, false, XPGainReason.COMMAND);
    }

    private void editValues(Player player, PlayerProfile profile, PrimarySkillType skill) {
        if (skill == null) {
            for (PrimarySkillType primarySkillType : skillTools.NON_CHILD_SKILLS) {
                handleCommand(player, profile, primarySkillType);
            }

            if (player != null) {
                player.sendMessage(localeManager.getString("Commands.Reset.All"));
            }
        } else {
            handleCommand(player, profile, skill);

            if (player != null) {
                player.sendMessage(localeManager.getString("Commands.Reset.Single", skillTools.getLocalizedSkillName(skill)));
            }
        }
    }
}
