package com.gmail.nossr50.commands.skills;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.subskills.AbstractSubSkill;
import com.gmail.nossr50.listeners.InteractionManager;
import com.gmail.nossr50.locale.LocaleManager;
import com.gmail.nossr50.util.TextComponentFactory;
import com.gmail.nossr50.util.skills.SkillTools;
import org.bukkit.entity.Player;

@CommandAlias("mmoinfo")
public class MmoInfoCommand extends BaseCommand {
    @Dependency
    private SkillTools skillTools;
    @Dependency
    private LocaleManager localeManager;
    @Dependency
    private TextComponentFactory textComponentFactory;

    @Default
    @CommandPermission("mcmmo.commands.mmoinfo")
    @CommandCompletion("@SubSkills")
    public void onCommand(Player player, @Single String skill) {
        if (skill.equals("???")) {
            player.sendMessage(localeManager.getString("Commands.MmoInfo.Header"));
            player.sendMessage(localeManager.getString("Commands.MmoInfo.SubSkillHeader", "???"));
            player.sendMessage(localeManager.getString("Commands.MmoInfo.DetailsHeader"));
            player.sendMessage(localeManager.getString("Commands.MmoInfo.Mystery"));
        } else if (InteractionManager.getAbstractByName(skill) != null || skillTools.isSubSkillNameExact(skill)) {
            displayInfo(player, skill);
        } else {
            player.sendMessage(localeManager.getString("Commands.MmoInfo.NoMatch"));
        }
    }

    private void displayInfo(Player player, String subSkillName) {
        //Check to see if the skill exists in the new system
        AbstractSubSkill abstractSubSkill = InteractionManager.getAbstractByName(subSkillName);
        if (abstractSubSkill != null) {
            /* New System Skills are programmable */
            abstractSubSkill.printInfo(player);
            //pluginRef.getTextComponentFactory().sendPlayerUrlHeader(player);
        } else {
            /*
             * Skill is only in the old system
             */
            player.sendMessage(localeManager.getString("Commands.MmoInfo.Header"));
            player.sendMessage(localeManager.getString("Commands.MmoInfo.SubSkillHeader", subSkillName));
            player.sendMessage(localeManager.getString("Commands.MmoInfo.DetailsHeader"));
            player.sendMessage(localeManager.getString("Commands.MmoInfo.OldSkill"));
        }

        for (SubSkillType subSkillType : SubSkillType.values()) {
            if (subSkillType.getNiceNameNoSpaces(subSkillType).equalsIgnoreCase(subSkillName))
                subSkillName = subSkillType.getWikiName(subSkillType.toString());
        }

        //Send Player Wiki Link
        textComponentFactory.sendPlayerSubSkillWikiLink(player, subSkillName);
    }
}
