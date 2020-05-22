package com.gmail.nossr50.commands.experience;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.locale.LocaleManager;
import com.gmail.nossr50.util.EventManager;
import com.gmail.nossr50.util.PermissionTools;
import com.gmail.nossr50.util.commands.CommandTools;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.SkillTools;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

// TODO: Review this - removed the self targeting syntax because it doesn't work with an ACF structure
public abstract class ExperienceCommand extends BaseCommand {
    @Dependency
    protected CommandTools commandTools;
    @Dependency
    protected PermissionTools permissionTools;
    @Dependency
    protected SkillTools skillTools;
    @Dependency
    protected UserManager userManager;
    @Dependency
    protected LocaleManager localeManager;
    @Dependency
    protected EventManager eventManager;
    @Dependency
    private DatabaseManager databaseManager;

    @Default
    @CommandCompletion("@Players @Skills")
    public void onCommand(CommandSender sender, OfflinePlayer target, PrimarySkillType skill, Integer level) {
        if (sender instanceof Player && target.getUniqueId() == ((Player) sender).getUniqueId()) {
            commandTools.hasPermission(permissionsCheckSelf(sender));
        } else {
            commandTools.hasPermission(permissionsCheckOthers(sender));
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

            editValues(null, profile, skill, level);
        } else {
            editValues(mcMMOPlayer.getPlayer(), mcMMOPlayer.getProfile(), skill, level);
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

    protected abstract boolean permissionsCheckSelf(CommandSender sender);

    protected abstract boolean permissionsCheckOthers(CommandSender sender);

    protected abstract void handleCommand(Player player, PlayerProfile profile, PrimarySkillType skill, int value);

    protected abstract void handlePlayerMessageAll(Player player, int value);

    protected abstract void handlePlayerMessageSkill(Player player, int value, PrimarySkillType skill);

    protected void editValues(Player player, PlayerProfile profile, PrimarySkillType skill, int value) {
        if (skill == null) {
            for (PrimarySkillType primarySkillType : skillTools.NON_CHILD_SKILLS) {
                handleCommand(player, profile, primarySkillType, value);
            }

            if (player != null) {
                handlePlayerMessageAll(player, value);
            }
        } else {
            handleCommand(player, profile, skill, value);

            if (player != null) {
                handlePlayerMessageSkill(player, value, skill);
            }
        }
    }
}
