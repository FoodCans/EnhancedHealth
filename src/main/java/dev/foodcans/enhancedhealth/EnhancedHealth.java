package dev.foodcans.enhancedhealth;

import dev.foodcans.enhancedhealth.command.CommandManager;
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
            case JSON: storage = new JsonStorage(new File(this.getDataFolder(), "players")); break;
            case MYSQL: storage = new MySQLStorage(); break;
            default: setEnabled(false); Bukkit.getLogger().severe("Incorrect storage type. Please use either Json or MySQL! Disabling plugin...."); return;
        }

        this.healthDataManager = new HealthDataManager();

        CommandManager commandManager = new CommandManager();
        commandManager.registerCommand(new AddCommand(healthDataManager), new RemoveCommand(healthDataManager), new SetCommand(healthDataManager), new RefreshCommand(healthDataManager),
            new ReloadCommand(healthDataManager), new ResetComand(healthDataManager), new StatusCommand(healthDataManager), new MigrateCommand(healthDataManager));
        commandManager.sortCommands();
        getCommand("health").setExecutor(commandManager);
        getServer().getPluginManager().registerEvents(new PlayerListener(healthDataManager), this);
    }

    @Override
    public void onDisable()
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
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

    public static EnhancedHealth getInstance()
    {
        return instance;
    }
}
