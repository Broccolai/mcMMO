package com.gmail.nossr50.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import com.gmail.nossr50.config.ConfigManager;
import com.gmail.nossr50.database.DatabaseManager;
import com.gmail.nossr50.datatypes.database.DatabaseType;
import com.gmail.nossr50.datatypes.experience.FormulaType;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.locale.LocaleManager;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.runnables.database.DatabaseConversionTask;
import com.gmail.nossr50.runnables.database.FormulaConversionTask;
import com.gmail.nossr50.runnables.player.PlayerProfileLoadingTask;
import com.gmail.nossr50.util.player.UserManager;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO: Relook at this file and what it replaced
@CommandAlias("mcconvert")
@CommandPermission("mcmmo.commands.mcconvert")
@Description("%description.mcconvert")
public class ConvertCommand extends BaseCommand {
    @Dependency
    private DatabaseManager databaseManager;
    @Dependency
    private UserManager userManager;
    @Dependency
    private ConfigManager configManager;
    @Dependency
    private LocaleManager localeManager;

    private List<String> FORMULA_TYPES;
    private List<String> DATABASE_TYPES;
    private final List<String> CONVERSION_SUBCOMMANDS = ImmutableList.of("database", "experience");
    private CommandExecutor databaseConvertCommand;
    private CommandExecutor experienceConvertCommand;

    private final mcMMO pluginRef;

    public ConvertCommand(mcMMO pluginRef) {
        this.pluginRef = pluginRef;
        initTypes();
    }

    private void initTypes() {
        ArrayList<String> formulaTypes = new ArrayList<>();
        ArrayList<String> databaseTypes = new ArrayList<>();

        for (FormulaType type : FormulaType.values()) {
            formulaTypes.add(type.toString());
        }

        for (DatabaseType type : DatabaseType.values()) {
            databaseTypes.add(type.toString());
        }

        // Custom stuff
        databaseTypes.remove(DatabaseType.CUSTOM.toString());

        if (databaseManager.getDatabaseType() == DatabaseType.CUSTOM) {
            databaseTypes.add(pluginRef.getDatabaseManagerFactory().getCustomDatabaseManagerClass().getName());
        }

        Collections.sort(formulaTypes);
        Collections.sort(databaseTypes);

        FORMULA_TYPES = ImmutableList.copyOf(formulaTypes);
        DATABASE_TYPES = ImmutableList.copyOf(databaseTypes);
    }

    @Default
    public void onCommand(CommandSender sender, String newSystem, @Single String oldSystem) {
        if (newSystem.equalsIgnoreCase("database") || newSystem.equalsIgnoreCase("db")) {
            databaseConvert(sender, oldSystem);
        } else if (newSystem.equalsIgnoreCase("experience") || newSystem.equalsIgnoreCase("xp") || oldSystem.equalsIgnoreCase("exp")) {
            experienceConvert(sender, oldSystem);
        } else {
            throw new InvalidCommandArgument();
        }
    }

    private void databaseConvert(CommandSender sender, @Single String oldSystem) {
        DatabaseType previousType = getDatabaseType(oldSystem);
        DatabaseType newType = databaseManager.getDatabaseType();

        if (previousType == newType || (newType == DatabaseType.CUSTOM && pluginRef.getDatabaseManagerFactory().getCustomDatabaseManagerClass().getSimpleName().equalsIgnoreCase(oldSystem))) {
            throw new InvalidCommandArgument(localeManager.getString("Commands.mcconvert.Database.Same", newType.toString()));
        }

        DatabaseManager oldDatabase = pluginRef.getDatabaseManagerFactory().createDatabaseManager(previousType);

        if (previousType == DatabaseType.CUSTOM) {
            Class<?> clazz;

            try {
                clazz = Class.forName(oldSystem);

                if (!DatabaseManager.class.isAssignableFrom(clazz)) {
                    throw new InvalidCommandArgument(localeManager.getString("Commands.mcconvert.Database.InvalidType", oldSystem));
                }

                oldDatabase = pluginRef.getDatabaseManagerFactory().createCustomDatabaseManager((Class<? extends DatabaseManager>) clazz);
            } catch (Throwable e) {
                e.printStackTrace();
                sender.sendMessage(localeManager.getString("Commands.mcconvert.Database.InvalidType", oldSystem));
                return;
            }
        }

        sender.sendMessage(localeManager.getString("Commands.mcconvert.Database.Start", previousType.toString(), newType.toString()));

        userManager.saveAll();
        userManager.clearAll();

        for (Player player : pluginRef.getServer().getOnlinePlayers()) {
            PlayerProfile profile = oldDatabase.loadPlayerProfile(player.getUniqueId());

            if (profile.isLoaded()) {
                databaseManager.saveUser(profile);
            }

            new PlayerProfileLoadingTask(pluginRef, player).runTaskLaterAsynchronously(pluginRef, 1); // 1 Tick delay to ensure the player is marked as online before we begin loading
        }

        new DatabaseConversionTask(pluginRef, oldDatabase, sender, previousType.toString(), newType.toString()).runTaskAsynchronously(pluginRef);
    }

    private void experienceConvert(CommandSender sender, @Single String oldSystem) {
        for(FormulaType formulaType : FormulaType.values()) {
            if(formulaType.toString().equalsIgnoreCase(oldSystem)) {
                sender.sendMessage(localeManager.getString("Commands.mcconvert.Experience.Start", formulaType.toString(), configManager.getConfigLeveling().getFormulaType().toString()));

                userManager.saveAll();
                userManager.clearAll();

                new FormulaConversionTask(pluginRef, sender, formulaType).runTaskLater(pluginRef, 1);

                for (Player player : pluginRef.getServer().getOnlinePlayers()) {
                    new PlayerProfileLoadingTask(pluginRef, player).runTaskLaterAsynchronously(pluginRef, 1); // 1 Tick delay to ensure the player is marked as online before we begin loading
                }
            }
        }

        sender.sendMessage(localeManager.getString("Commands.mcconvert.Experience.Invalid"));
    }

    private DatabaseType getDatabaseType(String typeName) {
        for (DatabaseType type : DatabaseType.values()) {
            if (type.name().equalsIgnoreCase(typeName)) {
                return type;
            }
        }

        if (typeName.equalsIgnoreCase("file")) {
            return DatabaseType.FLATFILE;
        } else if (typeName.equalsIgnoreCase("mysql")) {
            return DatabaseType.SQL;
        }

        return DatabaseType.CUSTOM;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return StringUtil.copyPartialMatches(args[0], CONVERSION_SUBCOMMANDS, new ArrayList<>(CONVERSION_SUBCOMMANDS.size()));
            case 2:
                if (args[1].equalsIgnoreCase("database") || args[1].equalsIgnoreCase("db")) {
                    return StringUtil.copyPartialMatches(args[0], DATABASE_TYPES, new ArrayList<>(DATABASE_TYPES.size()));
                }

                if (args[1].equalsIgnoreCase("experience") || args[1].equalsIgnoreCase("xp") || args[1].equalsIgnoreCase("exp")) {
                    return StringUtil.copyPartialMatches(args[0], FORMULA_TYPES, new ArrayList<>(FORMULA_TYPES.size()));
                }

                return ImmutableList.of();
            default:
                return ImmutableList.of();
        }
    }
}
