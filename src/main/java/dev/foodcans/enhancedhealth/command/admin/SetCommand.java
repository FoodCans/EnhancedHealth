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

public class SetCommand extends HealthCommand
{
    public SetCommand(HealthDataManager healthDataManager)
    {
        super(healthDataManager, "set", "enhancedhealth.command.set", Arrays.asList("<player>", "<amount>"));
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
        HealthDataManager.Result result = healthDataManager.setExtraHealth(uuid, amount);
        if (result == HealthDataManager.Result.SUCCESS)
        {
            Lang.Extra_HEALTH_SET.sendMessage(sender, Double.toString(amount), playerName);
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
            {
                healthDataManager.applyMaxHealthToPlayer(player);
            }
        } else if (result == HealthDataManager.Result.OVER_MAX)
        {
            Lang.EXTRA_HEALTH_SET_OVER_MAX.sendMessage(sender, playerName, Double.toString(amount), Double.toString(Config.MAX_EXTRA_HEALTH_ALLOWED));
        } else
        {
            Lang.EXTRA_HEALTH_SET_UNDER_ZERO.sendMessage(sender, playerName, Double.toString(amount));
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
