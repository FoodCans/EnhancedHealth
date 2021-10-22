package dev.foodcans.enhancedhealth.command.admin;

import dev.foodcans.enhancedhealth.command.HealthCommand;
import dev.foodcans.enhancedhealth.data.HealthDataManager;
import dev.foodcans.enhancedhealth.settings.Config;
import dev.foodcans.enhancedhealth.settings.lang.Lang;
import dev.foodcans.enhancedhealth.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public class AddCommand extends HealthCommand
{
    public AddCommand(HealthDataManager healthDataManager)
    {
        super(healthDataManager, "add", "enhancedhealth.command.add", Arrays.asList("<player>", "<amount>"));
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
        HealthDataManager.Result result = healthDataManager.addExtraHealth(uuid, amount);
        if (result == HealthDataManager.Result.SUCCESS)
        {
            Lang.EXTRA_HEALTH_ADDED.sendMessage(sender, Double.toString(amount), playerName);
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
            {
                healthDataManager.applyMaxHealthToPlayer(player);
            }
        } else
        {
            Lang.EXTRA_HEALTH_ADD_OVER_MAX.sendMessage(sender, playerName, Double.toString(amount), Double.toString(Config.MAX_EXTRA_HEALTH_ALLOWED));
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
