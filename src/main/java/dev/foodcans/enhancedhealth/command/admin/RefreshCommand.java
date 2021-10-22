package dev.foodcans.enhancedhealth.command.admin;

import dev.foodcans.enhancedhealth.command.HealthCommand;
import dev.foodcans.enhancedhealth.data.HealthDataManager;
import dev.foodcans.enhancedhealth.settings.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class RefreshCommand extends HealthCommand
{
    public RefreshCommand(HealthDataManager healthDataManager)
    {
        super(healthDataManager, "refresh", "enhancedhealth.command.refresh", Collections.singletonList("<player>"));
    }

    @Override
    public void onCommand(CommandSender sender, String... args)
    {
        String playerName = args[0];
        Player player = Bukkit.getPlayer(playerName);
        if (player != null)
        {
            healthDataManager.applyMaxHealthToPlayer(player);
            healthDataManager.applyHealthToPlayer(player);
            Lang.PLAYER_REFRESHED.sendMessage(sender, player.getName());
        } else
        {
            Lang.PLAYER_NOT_FOUND.sendMessage(sender, playerName);
        }
    }

    @Override
    public int getMinArgs()
    {
        return 1;
    }

    @Override
    public int getMaxArgs()
    {
        return 1;
    }

    @Override
    public boolean allowConsoleSender()
    {
        return true;
    }
}
