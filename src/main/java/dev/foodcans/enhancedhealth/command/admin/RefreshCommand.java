package dev.foodcans.enhancedhealth.command.admin;

import dev.foodcans.enhancedhealth.data.HealthDataManager;
import dev.foodcans.enhancedhealth.settings.lang.Lang;
import dev.foodcans.pluginutils.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class RefreshCommand extends SubCommand
{
    private final HealthDataManager healthDataManager;

    public RefreshCommand(HealthDataManager healthDataManager)
    {
        super("refresh", "enhancedhealth.command.refresh", Collections.singletonList("<player>"));
        this.healthDataManager = healthDataManager;
    }

    @Override
    public void onCommand(CommandSender sender, String... args)
    {
        String playerName = args[0];
        Player player = Bukkit.getPlayer(playerName);
        if (player != null)
        {
            healthDataManager.applyMaxHealthToPlayer(player, true);
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
