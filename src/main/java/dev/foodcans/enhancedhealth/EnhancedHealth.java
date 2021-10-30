package dev.foodcans.enhancedhealth;

import dev.foodcans.enhancedhealth.command.admin.*;
import dev.foodcans.enhancedhealth.command.player.StatusCommand;
import dev.foodcans.enhancedhealth.data.HealthDataManager;
import dev.foodcans.enhancedhealth.listener.PlayerListener;
import dev.foodcans.enhancedhealth.settings.Config;
import dev.foodcans.enhancedhealth.settings.lang.Lang;
import dev.foodcans.enhancedhealth.settings.lang.LangFile;
import dev.foodcans.enhancedhealth.storage.IStorage;
import dev.foodcans.enhancedhealth.storage.JsonStorage;
import dev.foodcans.enhancedhealth.storage.MySQLStorage;
import dev.foodcans.pluginutils.command.FailureReason;
import dev.foodcans.pluginutils.command.PluginCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class EnhancedHealth extends JavaPlugin
{
    private static EnhancedHealth instance;
    private LangFile langFile;
    private IStorage storage;
    private HealthDataManager healthDataManager;

    public static EnhancedHealth getInstance()
    {
        return instance;
    }

    @Override
    public void onLoad()
    {
        instance = this;
        saveDefaultConfig();
        Config.load(getConfig());
        this.langFile = new LangFile(this.getDataFolder(), "lang.yml");
    }

    @Override
    public void onEnable()
    {
        if (!setupStorage())
        {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.healthDataManager = new HealthDataManager();

        setupCommands();
        getServer().getPluginManager().registerEvents(new PlayerListener(healthDataManager), this);
    }

    @Override
    public void onDisable()
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            healthDataManager.getHealthData(player.getUniqueId()).setHealth(player.getHealth());
            storage.saveData(healthDataManager.getHealthData(player.getUniqueId()));
        }
    }

    private boolean setupStorage()
    {
        switch (Config.STORAGE_TYPE)
        {
            case JSON:
                storage = new JsonStorage(new File(this.getDataFolder(), "players"));
                if (Config.MIGRATE)
                {
                    try
                    {
                        MySQLStorage mySQLStorage = new MySQLStorage();
                        mySQLStorage.getAllData((result -> result.forEach(storage::saveStorage)));
                        mySQLStorage.deleteStorage();
                        Bukkit.getLogger().warning(ChatColor.GREEN + "MySQL data migrated to Json!");
                    } catch (Exception e)
                    {
                        // MySQL Not available
                        Bukkit.getLogger().warning(ChatColor.RED + "MySQL data not available to migrate to Json!");
                    }
                }
                return true;
            case MYSQL:
                storage = new MySQLStorage();
                if (Config.MIGRATE)
                {
                    try
                    {
                        JsonStorage jsonStorage = new JsonStorage(new File(this.getDataFolder(), "players"));
                        jsonStorage.getAllData((result -> result.forEach(storage::saveStorage)));
                        jsonStorage.deleteStorage();
                        Bukkit.getLogger().warning(ChatColor.GREEN + "Json data migrated to MySQL!");
                    } catch (Exception e)
                    {
                        // Json Not available
                        Bukkit.getLogger().warning(ChatColor.RED + "Json data not available to migrate to MySQL!");
                    }
                }
                return true;
            default:
                // Wrong storage type entered
                Bukkit.getLogger().severe(
                        "Incorrect storage type. Please use either Json or MySQL! Disabling plugin....");
                return false;
        }
    }

    private void setupCommands()
    {
        PluginCommand pluginCommand = new PluginCommand((failureReason, sender, replacements) ->
        {
            if (failureReason == FailureReason.NO_ARGS)
            {
                Lang.LIST_COMMANDS.sendMessage(sender);
            } else if (failureReason == FailureReason.COMMAND_NOT_FOUND)
            {
                Lang.COMMAND_NOT_FOUND.sendMessage(sender);
            } else if (failureReason == FailureReason.NO_PERMISSION)
            {
                Lang.NO_PERMISSION_COMMAND.sendMessage(sender, replacements[0]);
            } else if (failureReason == FailureReason.NOT_ENOUGH_ARGS)
            {
                Lang.NOT_ENOUGH_ARGS.sendMessage(sender, replacements[0]);
            } else if (failureReason == FailureReason.TOO_MANY_ARGS)
            {
                Lang.TOO_MANY_ARGS.sendMessage(sender, replacements[0]);
            } else if (failureReason == FailureReason.ONLY_PLAYERS)
            {
                Lang.COMMAND_ONLY_RUN_BY_PLAYERS.sendMessage(sender);
            } else if (failureReason == FailureReason.HELP)
            {
                Lang.HELP.sendMessage(sender, replacements[0]);
            } else if (failureReason == FailureReason.HELP_NONE)
            {
                Lang.HELP_NONE.sendMessage(sender);
            }
        });
        pluginCommand.registerSubCommand(new AddCommand(healthDataManager),
                new RemoveCommand(healthDataManager), new SetCommand(healthDataManager),
                new RefreshCommand(healthDataManager), new ReloadCommand(healthDataManager),
                new ResetComand(healthDataManager), new StatusCommand(healthDataManager));
        getCommand("health").setExecutor(pluginCommand);
    }

    public LangFile getLangFile()
    {
        return langFile;
    }

    public IStorage getStorage()
    {
        return storage;
    }
}
