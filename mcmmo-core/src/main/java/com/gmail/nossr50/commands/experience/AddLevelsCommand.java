package com.gmail.nossr50.commands.experience;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("addlevels")
@Description("%description.addlevels")
public class AddLevelsCommand extends ExperienceCommand {
    @Override
    protected boolean permissionsCheck(CommandSender sender) {
        return permissionTools.addlevelsOthers(sender);
    }

    @Override
    protected void handleCommand(Player player, PlayerProfile profile, PrimarySkillType skill, int value) {
        double xpRemoved = profile.getSkillXpLevelRaw(skill);
        profile.addLevels(skill, value);

        if (player == null) {
            profile.scheduleAsyncSave();
            return;
        }

        eventManager.tryLevelChangeEvent(player, skill, value, xpRemoved, true, XPGainReason.COMMAND);
    }

    @Override
    protected void handlePlayerMessageAll(Player player, int value) {
        player.sendMessage(localeManager.getString("Commands.addlevels.AwardAll.1", value));
    }

    @Override
    protected void handlePlayerMessageSkill(Player player, int value, PrimarySkillType skill) {
        player.sendMessage(localeManager.getString("Commands.addlevels.AwardSkill.1", value, skillTools.getLocalizedSkillName(skill)));
    }
}
