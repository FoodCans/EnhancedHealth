package dev.foodcans.enhancedhealth.command.player;

import dev.foodcans.enhancedhealth.data.HealthData;
import dev.foodcans.enhancedhealth.data.HealthDataManager;
import dev.foodcans.enhancedhealth.settings.Config;
import dev.foodcans.enhancedhealth.settings.lang.Lang;
import dev.foodcans.pluginutils.command.SubCommand;
import dev.foodcans.pluginutils.mojang.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.UUID;

public class StatusCommand extends SubCommand
{
    private final HealthDataManager healthDataManager;

    public StatusCommand(HealthDataManager healthDataManager)
    {
        super("status", "enhancedhealth.command.status", Collections.singletonList("[player]"));
        this.healthDataManager = healthDataManager;
    }

    @Override
    public void onCommand(CommandSender sender, String... args)
    {
        String playerName;
        if (args.length > 0)
        {
            if (sender.hasPermission("enhancedhealth.command.status.other"))
            {
                playerName = args[0];
            } else
            {
                if (sender instanceof Player)
                {
                    playerName = sender.getName();
                } else
                {
                    Lang.COMMAND_ONLY_RUN_BY_PLAYERS.sendMessage(sender);
                    return;
                }
            }
        } else
        {
            if (sender instanceof Player)
            {
                playerName = sender.getName();
            } else
            {
                Lang.COMMAND_ONLY_RUN_BY_PLAYERS.sendMessage(sender);
                return;
            }
        }
        UUID uuid = UUIDFetcher.getUUID(playerName);
        HealthData healthData = healthDataManager.getHealthData(uuid);
        if (healthData == null)
        {
            Lang.PLAYER_NOT_FOUND.sendMessage(sender, playerName);
            return;
        }
        Player player = Bukkit.getPlayer(uuid);
        double permissiveBonus = 0;
        if (player != null)
        {
            healthData.setHealth(player.getHealth());
            permissiveBonus = healthDataManager.getPermissiveBonus(player);
        }
        double extra = Math.min(Config.MAX_EXTRA_HEALTH_ALLOWED, permissiveBonus + healthData.getExtraHealth());
        Lang.PLAYER_STATUS.sendMessage(sender, playerName, Double.toString(healthData.getHealth()),
                Double.toString(extra));
    }

    @Override
    public int getMinArgs()
    {
        return 0;
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
