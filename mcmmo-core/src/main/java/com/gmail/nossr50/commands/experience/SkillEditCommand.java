package com.gmail.nossr50.commands.experience;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("mmoedit")
@Description("%description.mmoedit")
public class SkillEditCommand extends ExperienceCommand {
    @Override
    protected boolean permissionsCheck(CommandSender sender) {
        return permissionTools.mmoeditOthers(sender);
    }

    @Override
    protected void handleCommand(Player player, PlayerProfile profile, PrimarySkillType skill, int value) {
        int skillLevel = profile.getSkillLevel(skill);
        double xpRemoved = profile.getSkillXpLevelRaw(skill);

        profile.modifySkill(skill, value);

        if (player == null) {
            profile.scheduleAsyncSave();
            return;
        }

        if (value == skillLevel) {
            return;
        }

        eventManager.tryLevelEditEvent(player, skill, value, xpRemoved, value > skillLevel, XPGainReason.COMMAND, skillLevel);
    }

    @Override
    protected void handlePlayerMessageAll(Player player, int value) {
        player.sendMessage(localeManager.getString("Commands.mmoedit.AllSkills.1", value));
    }

    @Override
    protected void handlePlayerMessageSkill(Player player, int value, PrimarySkillType skill) {
        player.sendMessage(localeManager.getString("Commands.mmoedit.Modified.1", skillTools.getLocalizedSkillName(skill), value));
    }
}
