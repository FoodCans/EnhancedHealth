package dev.foodcans.enhancedhealth.command.admin;

import dev.foodcans.enhancedhealth.command.HealthCommand;
import dev.foodcans.enhancedhealth.data.HealthData;
import dev.foodcans.enhancedhealth.data.HealthDataManager;
import dev.foodcans.enhancedhealth.settings.lang.Lang;
import dev.foodcans.enhancedhealth.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.UUID;

public class ResetComand extends HealthCommand
{
    public ResetComand(HealthDataManager healthDataManager)
    {
        super(healthDataManager, "reset", "enhancedhealth.command.reset", Collections.singletonList("<player>"));
    }

    @Override
    public void onCommand(CommandSender sender, String... args)
    {
        String playerName = args[0];
        UUID uuid = UUIDFetcher.getUUID(playerName);
        HealthData healthData = healthDataManager.getHealthData(uuid);
        if (healthData == null)
        {
            Lang.PLAYER_NOT_FOUND.sendMessage(sender, playerName);
            return;
        }
        healthDataManager.removeExtraHealth(uuid, healthData.getExtraHealth());
        Player player = Bukkit.getPlayer(playerName);
        if (player != null)
        {
            healthDataManager.applyMaxHealthToPlayer(player, true);
            Lang.PLAYER_RESET.sendMessage(sender, player.getName());
        } else
        {
            Lang.PLAYER_RESET.sendMessage(sender, playerName);
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
