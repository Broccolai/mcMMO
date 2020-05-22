package com.gmail.nossr50.commands.experience;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.experience.XPGainSource;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("addxp")
@Description("%description.addxp")
public class AddXPCommand extends ExperienceCommand {
    @Override
    protected boolean permissionsCheckSelf(CommandSender sender) {
        return permissionTools.addxp(sender);
    }

    @Override
    protected boolean permissionsCheckOthers(CommandSender sender) {
        return permissionTools.addxpOthers(sender);
    }

    @Override
    protected void handleCommand(Player player, PlayerProfile profile, PrimarySkillType skill, int value) {
        if (player != null) {
            //Check if player profile is loaded
            if (userManager.getPlayer(player) == null)
                return;

            userManager.getPlayer(player).applyXpGain(skill, value, XPGainReason.COMMAND, XPGainSource.COMMAND);
        } else {
            profile.addXp(skill, value);
            profile.scheduleAsyncSave();
        }
    }

    @Override
    protected void handlePlayerMessageAll(Player player, int value) {
        player.sendMessage(localeManager.getString("Commands.addxp.AwardAll", value));
    }

    @Override
    protected void handlePlayerMessageSkill(Player player, int value, PrimarySkillType skill) {
        player.sendMessage(localeManager.getString("Commands.addxp.AwardSkill", value, skillTools.getLocalizedSkillName(skill)));
    }
}
