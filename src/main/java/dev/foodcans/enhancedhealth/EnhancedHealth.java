package dev.foodcans.enhancedhealth;

import dev.foodcans.enhancedhealth.command.CommandManager;
import dev.foodcans.enhancedhealth.command.admin.*;
import dev.foodcans.enhancedhealth.command.player.StatusCommand;
import dev.foodcans.enhancedhealth.data.HealthDataManager;
import dev.foodcans.enhancedhealth.listener.PlayerListener;
import dev.foodcans.enhancedhealth.settings.Config;
import dev.foodcans.enhancedhealth.settings.lang.LangFile;
import dev.foodcans.enhancedhealth.storage.IStorage;
import dev.foodcans.enhancedhealth.storage.JsonStorage;
import dev.foodcans.enhancedhealth.storage.MySQLStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class EnhancedHealth extends JavaPlugin
{
    private static EnhancedHealth instance;
    private LangFile langFile;
    private IStorage storage;
    private HealthDataManager healthDataManager;

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
            new ReloadCommand(healthDataManager), new StatusCommand(healthDataManager));
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
