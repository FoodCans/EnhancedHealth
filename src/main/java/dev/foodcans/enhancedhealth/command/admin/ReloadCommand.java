package dev.foodcans.enhancedhealth.command.admin;

import dev.foodcans.enhancedhealth.EnhancedHealth;
import dev.foodcans.enhancedhealth.data.HealthDataManager;
import dev.foodcans.enhancedhealth.settings.Config;
import dev.foodcans.enhancedhealth.settings.lang.Lang;
import dev.foodcans.pluginutils.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class ReloadCommand extends SubCommand
{
    private final HealthDataManager healthDataManager;

    public ReloadCommand(HealthDataManager healthDataManager)
    {
        super("reload", "enhancedhealth.command.reload", Collections.emptyList());
        this.healthDataManager = healthDataManager;
    }

    @Override
    public void onCommand(CommandSender sender, String... args)
    {
        EnhancedHealth.getInstance().reloadConfig();
        EnhancedHealth.getInstance().getLangFile().reload();
        Config.load(EnhancedHealth.getInstance().getConfig());
        for (Player player : Bukkit.getOnlinePlayers())
        {
            healthDataManager.applyMaxHealthToPlayer(player, true);
            healthDataManager.applyHealthToPlayer(player);
        }
        Lang.CONFIG_RELOADED.sendMessage(sender);
    }

    @Override
    public int getMinArgs()
    {
        return 0;
    }

    @Override
    public int getMaxArgs()
    {
        return 0;
    }

    @Override
    public boolean allowConsoleSender()
    {
        return true;
    }
}
