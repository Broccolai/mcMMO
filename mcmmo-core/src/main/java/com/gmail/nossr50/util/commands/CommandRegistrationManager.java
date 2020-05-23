package com.gmail.nossr50.util.commands;

import co.aikar.commands.*;
import com.gmail.nossr50.chat.ChatManager;
import com.gmail.nossr50.commands.*;
import com.gmail.nossr50.commands.admin.NBTToolsCommand;
import com.gmail.nossr50.commands.admin.PlayerDebugCommand;
import com.gmail.nossr50.commands.admin.ReloadLocaleCommand;
import com.gmail.nossr50.commands.chat.AdminChatCommand;
import com.gmail.nossr50.commands.chat.ChatSpyCommand;
import com.gmail.nossr50.commands.chat.PartyChatCommand;
import com.gmail.nossr50.commands.database.McremoveCommand;
import com.gmail.nossr50.commands.database.PurgeCommand;
import com.gmail.nossr50.commands.database.ShowDatabaseCommand;
import com.gmail.nossr50.commands.experience.AddLevelsCommand;
import com.gmail.nossr50.commands.experience.AddXPCommand;
import com.gmail.nossr50.commands.experience.SkillEditCommand;
import com.gmail.nossr50.commands.experience.SkillResetCommand;
import com.gmail.nossr50.commands.party.PartyCommand;
import com.gmail.nossr50.commands.party.teleport.PtpCommand;
import com.gmail.nossr50.commands.player.*;
import com.gmail.nossr50.commands.server.ReloadPluginCommand;
import com.gmail.nossr50.commands.skills.*;
import com.gmail.nossr50.config.ConfigManager;
import com.gmail.nossr50.config.scoreboard.ConfigScoreboard;
import com.gmail.nossr50.core.DynamicSettingsManager;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.datatypes.database.DatabaseType;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.locale.LocaleManager;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.party.PartyManager;
import com.gmail.nossr50.util.EventManager;
import com.gmail.nossr50.util.PermissionTools;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.TextComponentFactory;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.scoreboards.ScoreboardManager;
import com.gmail.nossr50.util.skills.SkillTools;
import org.bukkit.command.PluginCommand;

import java.util.ArrayList;
import java.util.Locale;

public final class CommandRegistrationManager {
    private final mcMMO pluginRef;
    private String permissionsMessage;
    //NOTE: Does not actually require paper, will work for bukkit
    private PaperCommandManager commandManager;

    public CommandRegistrationManager(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        permissionsMessage = pluginRef.getLocaleManager().getString("mcMMO.NoPermission");
        commandManager = new PaperCommandManager(pluginRef);
    }

    /**
     * Register ACF Commands
     */
    public void registerACFCommands() {
        // Generic Commands
        registerMcMMOCommand();
        registerMmoInfoCommand();
        registerAbilityToggleCommand();
        registerGodModeCommand();
        registerChatSpyCommand();
        registerNotifyCommand();
        registerRefreshCommand();
        registerScoreboardCommand();
        registerMHDCommand();
        registerXprateCommand();

        // Database Commands
        registerMcpurgeCommand();
        registerMcremoveCommand();
        registerMmoshowdbCommand();
        registerMcconvertCommand();

        // Chat Commands
        registerPartyChatCommand();
        registerAdminChatCommand();

        // Experience Commands
        registerAddlevelsCommand();
        registerAddxpCommand();
        registerMmoeditCommand();
        registerSkillresetCommand();

        //Config Commands
        registerMcmmoReloadCommand();

        registerNBTToolsCommand();
        registerMmoDebugCommand();
    }

    /**
     * Register contexts for ACF
     */
    public void registerACFContexts() {
        commandManager.getCommandContexts().registerOptionalContext(McMMOPlayer.class, c -> {
            String name = c.popFirstArg();

            String playerName = pluginRef.getCommandTools().getMatchedPlayerName(name);
            McMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(playerName);

            if (!pluginRef.getCommandTools().checkPlayerExistence(c.getIssuer().getIssuer(), playerName, mcMMOPlayer)) {
                throw new InvalidCommandArgument();
            }

            return mcMMOPlayer;
        });

        commandManager.getCommandContexts().registerContext(PrimarySkillType.class, c -> {
            String input = c.popFirstArg();

            if (!input.equalsIgnoreCase("all")) {
                pluginRef.getCommandTools().checkForInvalidSkill(input);
            }

            return pluginRef.getSkillTools().matchSkill(c.popFirstArg());
        });
    }

    /**
     * Register dependencies for ACF
     */
    public void registerACFInjections() {
        // Register Tools
        commandManager.registerDependency(CommandTools.class, pluginRef.getCommandTools());
        commandManager.registerDependency(PermissionTools.class, pluginRef.getPermissionTools());
        commandManager.registerDependency(SkillTools.class, pluginRef.getSkillTools());

        // Register Managers
        commandManager.registerDependency(UserManager.class, pluginRef.getUserManager());
        commandManager.registerDependency(ConfigManager.class, pluginRef.getConfigManager());
        commandManager.registerDependency(DynamicSettingsManager.class, pluginRef.getDynamicSettingsManager());
        commandManager.registerDependency(LocaleManager.class, pluginRef.getLocaleManager());
        commandManager.registerDependency(DatabaseManager.class, pluginRef.getDatabaseManager());
        commandManager.registerDependency(ScoreboardManager.class, pluginRef.getScoreboardManager());
        commandManager.registerDependency(NotificationManager.class, pluginRef.getNotificationManager());
        commandManager.registerDependency(EventManager.class, pluginRef.getEventManager());
        commandManager.registerDependency(PartyManager.class, pluginRef.getPartyManager());
        commandManager.registerDependency(ChatManager.class, pluginRef.getChatManager());

        // Register Settings
        commandManager.registerDependency(ConfigScoreboard.class, pluginRef.getScoreboardSettings());

        // Register Factories
        commandManager.registerDependency(TextComponentFactory.class, pluginRef.getTextComponentFactory());
    }

    /**
     * Register Completions for ACF
     */
    public void registerACFCompletions() {
        commandManager.getCommandCompletions().registerStaticCompletion("Skills", pluginRef.getSkillTools().LOCALIZED_SKILL_NAMES);

        commandManager.getCommandCompletions().registerStaticCompletion("SubSkills", pluginRef.getSkillTools().EXACT_SUBSKILL_NAMES);

        // TODO: Could be condensed into a stream?
        ArrayList<String> formulaTypes = new ArrayList<>();

        for (FormulaType formulaType : FormulaType.values()) {
            formulaTypes.add(formulaType.toString());
        }

        commandManager.getCommandCompletions().registerStaticCompletion("FormulaTypes", formulaTypes);

        commandManager.getCommandCompletions().registerAsyncCompletion("DatabaseTypes", context -> {
            ArrayList<String> completions = new ArrayList<>();
            DatabaseType[] databaseTypes = DatabaseType.values();

            for (DatabaseType databaseType : databaseTypes) {
                completions.add(databaseType.toString());
            }

            completions.remove(DatabaseType.CUSTOM.toString());

            if (pluginRef.getDatabaseManager().getDatabaseType() == DatabaseType.CUSTOM) {
                completions.add(pluginRef.getDatabaseManagerFactory().getCustomDatabaseManagerClass().getName());
            }

            return completions;
        });
    }

    /**
     * Register Conditions for ACF
     */
    public void registerACFConditions() {
        commandManager.getCommandConditions().addCondition(Integer.class, "positive", ((context, execContext, value) -> {
            if (value == null) {
                return;
            }

            if (value < 0) {
                // TODO: Probably needs to be localised
                throw new ConditionFailedException("Input must be positive");
            }
        }));
    }

    /**
     * Register exception handlers for the ACF commands
     */
    private void registerExceptionHandlers() {
        registerDefaultExceptionHandler();
    }

    /**
     * Register default exception handler
     */
    private void registerDefaultExceptionHandler() {
        commandManager.setDefaultExceptionHandler((command, registeredCommand, sender, args, t) -> {
            pluginRef.getLogger().warning("Error occurred while executing command " + command.getName());
            return false;
        });
    }

    /**
     * Register the mcMMO command
     */
    private void registerMcMMOCommand() {
        commandManager.getCommandReplacements().addReplacement("description.mcmmo", pluginRef.getLocaleManager().getString("Commands.Description.mcmmo"));
        commandManager.registerCommand(new McMMOCommand());
    }

    /**
     * Register MMO Info Command
     */
    private void registerMmoInfoCommand() {
        commandManager.getCommandReplacements().addReplacement("description.mmoinfo", pluginRef.getLocaleManager().getString("Commands.Description.mmoinfo"));
        commandManager.registerCommand(new MmoInfoCommand());
    }

    /**
     * Register Ability Toggle Command
     */
    private void registerAbilityToggleCommand() {
        commandManager.getCommandReplacements().addReplacement("description.mcability", pluginRef.getLocaleManager().getString("Commands.Description.mcability"));
        commandManager.registerCommand(new AbilityToggleCommand());
    }

    /**
     * Register God Mode Command
     */
    private void registerGodModeCommand() {
        commandManager.getCommandReplacements().addReplacement("description.mcgod", pluginRef.getLocaleManager().getString("Commands.Description.mcgod"));
        commandManager.registerCommand(new GodModeCommand());
    }

    /**
     * Register Chat Spy Command
     */
    private void registerChatSpyCommand() {
        commandManager.getCommandReplacements().addReplacement("description.mcchatspy", pluginRef.getLocaleManager().getString("Commands.Description.mcchatspy"));
        commandManager.registerCommand(new ChatSpyCommand());
    }

    /**
     * Register Notify Command
     */
    private void registerNotifyCommand() {
        commandManager.getCommandReplacements().addReplacement("description.mcnotify", pluginRef.getLocaleManager().getString("Commands.Description.mcnotify"));
        commandManager.registerCommand(new ChatNotificationToggleCommand());
    }

    /**
     * Register Refresh Command
     */
    private void registerRefreshCommand() {
        commandManager.getCommandReplacements().addReplacement("description.mcrefresh", pluginRef.getLocaleManager().getString("Commands.Description.mcrefresh"));
        commandManager.registerCommand(new RefreshCooldownsCommand());
    }

    /**
     * Register Scoreboard Command
     */
    private void registerScoreboardCommand() {
        commandManager.getCommandReplacements().addReplacement("description.mcscoreboard", pluginRef.getLocaleManager().getString("Commands.Description.mcscoreboard"));
        commandManager.registerCommand(new ScoreboardCommand());
    }

    /**
     * Register MHD Command
     */
    private void registerMHDCommand() {
        commandManager.registerCommand(new ResetUserHealthBarSettingsCommand());
    }

    /**
     * Register Experience Rate Command
     */
    private void registerXprateCommand() {
        commandManager.getCommandReplacements().addReplacement("description.xprate", pluginRef.getLocaleManager().getString("Commands.Description.xprate"));
        commandManager.registerCommand(new ExperienceRateCommand());
    }

    /**
     * Register Purge Command
     */
    private void registerMcpurgeCommand() {
        commandManager.getCommandReplacements().addReplacement("description.mcpurge", pluginRef.getLocaleManager().getString("Commands.Description.mcpurge"));
        commandManager.registerCommand(new PurgeCommand());
    }

    /**
     * Register Remove Command
     */
    private void registerMcremoveCommand() {
        commandManager.getCommandReplacements().addReplacement("description.mcremove", pluginRef.getLocaleManager().getString("Commands.Description.mcremove"));
        commandManager.registerCommand(new McremoveCommand());
    }

    /**
     * Register Show Db Command
     */
    private void registerMmoshowdbCommand() {
        commandManager.getCommandReplacements().addReplacement("description.mmoshowdb", pluginRef.getLocaleManager().getString("Commands.Description.mmoshowdb"));
        commandManager.registerCommand(new ShowDatabaseCommand());
    }

    /**
     * Register Convert Command
     */
    private void registerMcconvertCommand() {
        commandManager.getCommandReplacements().addReplacement("description.mcconvert", pluginRef.getLocaleManager().getString("Commands.Description.mcconvert"));
        commandManager.registerCommand(new ConvertCommand());
    }

    private void registerAdminChatCommand() {
        commandManager.getCommandReplacements().addReplacement("description.adminchat", pluginRef.getLocaleManager().getString("Commands.Description.adminchat"));
        commandManager.registerCommand(new AdminChatCommand());
    }

    private void registerPartyChatCommand() {
        commandManager.getCommandReplacements().addReplacement("description.partychat", pluginRef.getLocaleManager().getString("Commands.Description.partychat"));
        commandManager.registerCommand(new PartyChatCommand());
    }

    /**
     * Register Add Level Command
     */
    private void registerAddlevelsCommand() {
        commandManager.getCommandReplacements().addReplacement("description.addlevels", pluginRef.getLocaleManager().getString("Commands.Description.addlevels"));
        commandManager.registerCommand(new AddLevelsCommand());
    }

    /**
     * Register Add Xp Command
     */
    private void registerAddxpCommand() {
        commandManager.getCommandReplacements().addReplacement("description.addxp", pluginRef.getLocaleManager().getString("Commands.Description.addxp"));
        commandManager.registerCommand(new AddXPCommand());
    }

    /**
     * Register MMO Edit Command
     */
    private void registerMmoeditCommand() {
        commandManager.getCommandReplacements().addReplacement("description.mmoedit", pluginRef.getLocaleManager().getString("Commands.Description.mmoedit"));
        commandManager.registerCommand(new SkillEditCommand());
    }

    /**
     * Register Reload Command
     */
    private void registerMcmmoReloadCommand() {
        commandManager.getCommandReplacements().addReplacement("description.mcmmoreload", pluginRef.getLocaleManager().getString("Commands.Description.mcmmoreload"));
        commandManager.registerCommand(new ReloadPluginCommand());
    }

    /**
     * Register Skill Reset Command
     */
    private void registerSkillresetCommand() {
        commandManager.getCommandReplacements().addReplacement("description.skillreset", pluginRef.getLocaleManager().getString("Commands.Description.skillreset"));
        commandManager.registerCommand(new SkillResetCommand());
    }

    /**
     * Register the NBT Tools command
     */
    private void registerNBTToolsCommand() {
        commandManager.registerCommand(new NBTToolsCommand());
    }

    /**
     * Register the MMO Debug command
     */
    private void registerMmoDebugCommand() {
        commandManager.registerCommand(new PlayerDebugCommand());
    }

    private void registerSkillCommands() {
        for (PrimarySkillType primarySkillType : PrimarySkillType.values()) {
            String commandName = primarySkillType.toString().toLowerCase(Locale.ENGLISH);
            String localizedName = pluginRef.getSkillTools().getLocalizedSkillName(primarySkillType).toLowerCase(Locale.ENGLISH);

            PluginCommand command;

            command = pluginRef.getCommand(commandName);
            command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.Skill", StringUtils.getCapitalized(localizedName)));
            command.setPermission("mcmmo.commands." + commandName);
            command.setPermissionMessage(permissionsMessage);
            command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.0", localizedName));
            command.setUsage(command.getUsage() + "\n" + pluginRef.getLocaleManager().getString("Commands.Usage.2", localizedName, "?", "[" + pluginRef.getLocaleManager().getString("Commands.Usage.Page") + "]"));

            switch (primarySkillType) {
                case ACROBATICS:
                    command.setExecutor(new AcrobaticsCommand(pluginRef));
                    break;

                case ALCHEMY:
//                    command.setExecutor(new AlchemyCommand());
                    break;

                case ARCHERY:
                    command.setExecutor(new ArcheryCommand(pluginRef));
                    break;

                case AXES:
                    command.setExecutor(new AxesCommand(pluginRef));
                    break;

                case EXCAVATION:
                    command.setExecutor(new ExcavationCommand(pluginRef));
                    break;

                case FISHING:
                    command.setExecutor(new FishingCommand(pluginRef));
                    break;

                case HERBALISM:
                    command.setExecutor(new HerbalismCommand(pluginRef));
                    break;

                case MINING:
                    command.setExecutor(new MiningCommand(pluginRef));
                    break;

                case REPAIR:
                    command.setExecutor(new RepairCommand(pluginRef));
                    break;

                case SALVAGE:
                    command.setExecutor(new SalvageCommand(pluginRef));
                    break;

                case SMELTING:
                    command.setExecutor(new SmeltingCommand(pluginRef));
                    break;

                case SWORDS:
                    command.setExecutor(new SwordsCommand(pluginRef));
                    break;

                case TAMING:
                    command.setExecutor(new TamingCommand(pluginRef));
                    break;

                case UNARMED:
                    command.setExecutor(new UnarmedCommand(pluginRef));
                    break;

                case WOODCUTTING:
                    command.setExecutor(new WoodcuttingCommand(pluginRef));
                    break;

                default:
                    break;
            }
        }
    }

    private void registerInspectCommand() {
        PluginCommand command = pluginRef.getCommand("inspect");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.inspect"));
        command.setPermission("mcmmo.commands.inspect;mcmmo.commands.inspect.far;mcmmo.commands.inspect.offline");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.1", "inspect", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + ">"));
        command.setExecutor(new InspectCommand(pluginRef));
    }

    private void registerMccooldownCommand() {
        PluginCommand command = pluginRef.getCommand("mccooldown");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.mccooldown"));
        command.setPermission("mcmmo.commands.mccooldown");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.0", "mccooldowns"));
        command.setExecutor(new CooldownCommand(pluginRef));
    }

    private void registerMcrankCommand() {
        PluginCommand command = pluginRef.getCommand("mcrank");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.mcrank"));
        command.setPermission("mcmmo.commands.mcrank;mcmmo.commands.mcrank.others;mcmmo.commands.mcrank.others.far;mcmmo.commands.mcrank.others.offline");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.1", "mcrank", "[" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + "]"));
        command.setExecutor(new RankCommand(pluginRef));
    }

    private void registerMcstatsCommand() {
        PluginCommand command = pluginRef.getCommand("mcstats");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.mcstats"));
        command.setPermission("mcmmo.commands.mcstats");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.0", "mcstats"));
        command.setExecutor(new SkillStatsCommand(pluginRef));
    }

    private void registerMctopCommand() {
        PluginCommand command = pluginRef.getCommand("mctop");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.mctop"));
        command.setPermission("mcmmo.commands.mctop"); // Only need the main one, not the individual skill ones
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.2", "mctop", "[" + pluginRef.getLocaleManager().getString("Commands.Usage.Skill") + "]", "[" + pluginRef.getLocaleManager().getString("Commands.Usage.Page") + "]"));
        command.setExecutor(new LeaderboardCommand(pluginRef));
    }

    private void registerPartyCommand() {
        PluginCommand command = pluginRef.getCommand("party");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.party"));
        command.setPermission("mcmmo.commands.party;mcmmo.commands.party.accept;mcmmo.commands.party.create;mcmmo.commands.party.disband;" +
                "mcmmo.commands.party.xpshare;mcmmo.commands.party.invite;mcmmo.commands.party.itemshare;mcmmo.commands.party.join;" +
                "mcmmo.commands.party.kick;mcmmo.commands.party.lock;mcmmo.commands.party.owner;mcmmo.commands.party.password;" +
                "mcmmo.commands.party.quit;mcmmo.commands.party.rename;mcmmo.commands.party.unlock");
        command.setPermissionMessage(permissionsMessage);
        command.setExecutor(new PartyCommand(pluginRef));
    }

    private void registerPtpCommand() {
        PluginCommand command = pluginRef.getCommand("ptp");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.ptp"));
        command.setPermission("mcmmo.commands.ptp"); // Only need the main one, not the individual ones for toggle/accept/acceptall
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.1", "ptp", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Player") + ">"));
        command.setUsage(command.getUsage() + "\n" + pluginRef.getLocaleManager().getString("Commands.Usage.1", "ptp", "<toggle|accept|acceptall>"));
        command.setExecutor(new PtpCommand(pluginRef));
    }

    /*private void registerHardcoreCommand() {
        PluginCommand command = mcMMO.p.getCommand("hardcore");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.hardcore"));
        command.setPermission("mcmmo.commands.hardcore;mcmmo.commands.hardcore.toggle;mcmmo.commands.hardcore.modify");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.1", "hardcore", "[on|off]"));
        command.setUsage(command.getUsage() + "\n" + pluginRef.getLocaleManager().getString("Commands.Usage.1", "hardcore", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Rate") + ">"));
        command.setExecutor(new HardcoreCommand());
    }

    private void registerVampirismCommand() {
        PluginCommand command = mcMMO.p.getCommand("vampirism");
        command.setDescription(pluginRef.getLocaleManager().getString("Commands.Description.vampirism"));
        command.setPermission("mcmmo.commands.vampirism;mcmmo.commands.vampirism.toggle;mcmmo.commands.vampirism.modify");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.1", "vampirism", "[on|off]"));
        command.setUsage(command.getUsage() + "\n" + pluginRef.getLocaleManager().getString("Commands.Usage.1", "vampirism", "<" + pluginRef.getLocaleManager().getString("Commands.Usage.Rate") + ">"));
        command.setExecutor(new VampirismCommand());
    }*/

    private void registerReloadLocaleCommand() {
        PluginCommand command = pluginRef.getCommand("mcmmoreloadlocale");
        command.setDescription("Reloads locale"); // TODO: Localize
        command.setPermission("mcmmo.commands.reloadlocale");
        command.setPermissionMessage(permissionsMessage);
        command.setUsage(pluginRef.getLocaleManager().getString("Commands.Usage.0", "mcmmoreloadlocale"));
        command.setExecutor(new ReloadLocaleCommand(pluginRef));
    }

    public void registerCommands() {
        // Hardcore Commands
        /*registerHardcoreCommand();
        registerVampirismCommand();*/

        // Party Commands
        registerPartyCommand();
        registerPtpCommand();

        // Player Commands
        registerInspectCommand();
        registerMccooldownCommand();
        registerMcrankCommand();
        registerMcstatsCommand();
        registerMctopCommand();

        // Skill Commands
        registerSkillCommands();

        // Admin commands
        registerReloadLocaleCommand();
    }
}
