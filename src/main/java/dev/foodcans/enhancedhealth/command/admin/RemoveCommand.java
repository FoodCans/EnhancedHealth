package dev.foodcans.enhancedhealth.command.admin;

import dev.foodcans.enhancedhealth.data.HealthDataManager;
import dev.foodcans.enhancedhealth.settings.lang.Lang;
import dev.foodcans.pluginutils.command.SubCommand;
import dev.foodcans.pluginutils.mojang.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

import static dev.foodcans.pluginutils.PluginUtils.Numbers.isDouble;

public class RemoveCommand extends SubCommand
{
    private final HealthDataManager healthDataManager;

    public RemoveCommand(HealthDataManager healthDataManager)
    {
        super("remove", "enhancedhealth.command.remove", Arrays.asList("<player>", "<amount>"));
        this.healthDataManager = healthDataManager;
    }

    @Override
    public void onCommand(CommandSender sender, String... args)
    {
        String playerName = args[0];
        String amountStr = args[1];
        if (!isDouble(amountStr))
        {
            Lang.ARGUMENT_MUST_BE_DOUBLE.sendMessage(sender, "<amount>");
            return;
        }
        double amount = Math.abs(Double.parseDouble(amountStr));
        UUID uuid = UUIDFetcher.getUUID(playerName);
        HealthDataManager.Result result = healthDataManager.removeExtraHealth(uuid, amount);
        if (result == HealthDataManager.Result.SUCCESS)
        {
            Lang.EXTRA_HEALTH_REMOVED.sendMessage(sender, Double.toString(amount), playerName);
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
            {
                healthDataManager.applyMaxHealthToPlayer(player, true);
            }
        } else
        {
            Lang.EXTRA_HEALTH_REMOVE_UNDER_ZERO.sendMessage(sender, Double.toString(amount), playerName);
        }
    }

    @Override
    public int getMinArgs()
    {
        return 2;
    }

    @Override
    public int getMaxArgs()
    {
        return 2;
    }

    @Override
    public boolean allowConsoleSender()
    {
        return true;
    }
}
