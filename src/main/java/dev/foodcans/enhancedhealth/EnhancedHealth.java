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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class EnhancedHealth extends JavaPlugin
{
    private static EnhancedHealth instance;
    private LangFile langFile;
    private IStorage storage;
    private HealthDataManager healthDataManager;

    private volatile boolean migrating = false;

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
        switch (Config.STORAGE_TYPE)
        {
            case JSON:
                storage = new JsonStorage(new File(this.getDataFolder(), "players"));
                break;
            case MYSQL:
                storage = new MySQLStorage();
                break;
            default:
                setEnabled(false);
                Bukkit.getLogger().severe(
                        "Incorrect storage type. Please use either Json or MySQL! Disabling plugin....");
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

    public void migrate(CommandSender sender)
    {
        migrating = true;
        storage.getAllData((result ->
        {
            Config.StorageType from;
            Config.StorageType to;
            if (storage instanceof JsonStorage)
            {
                // Migrate to MySQL
                storage = new MySQLStorage();
                from = Config.StorageType.JSON;
                to = Config.StorageType.MYSQL;
            } else
            {
                // Migrate to Json
                storage = new JsonStorage(new File(this.getDataFolder(), "players"));
                from = Config.StorageType.MYSQL;
                to = Config.StorageType.JSON;
            }
            result.forEach(storage::saveStorage);
            Lang.DATA_MIGRATED.sendMessage(sender, from.name(), to.name());
            migrating = false;
        }));
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
                new ResetComand(healthDataManager), new StatusCommand(healthDataManager), new MigrateCommand());
        getCommand("health").setExecutor(pluginCommand);
    }

    public boolean isMigrating()
    {
        return migrating;
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
