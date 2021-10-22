package dev.foodcans.enhancedhealth.command.admin;

import dev.foodcans.enhancedhealth.EnhancedHealth;
import dev.foodcans.enhancedhealth.command.HealthCommand;
import dev.foodcans.enhancedhealth.data.HealthDataManager;
import dev.foodcans.enhancedhealth.settings.Config;
import dev.foodcans.enhancedhealth.settings.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class ReloadCommand extends HealthCommand
{
    public ReloadCommand(HealthDataManager healthDataManager)
    {
        super(healthDataManager, "reload", "enhancedhealth.command.reload", Collections.emptyList());
    }

    @Override
    public void onCommand(CommandSender sender, String... args)
    {
        EnhancedHealth.getInstance().reloadConfig();
        Config.load(EnhancedHealth.getInstance().getConfig());
        for (Player player : Bukkit.getOnlinePlayers())
        {
            healthDataManager.applyMaxHealthToPlayer(player);
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
