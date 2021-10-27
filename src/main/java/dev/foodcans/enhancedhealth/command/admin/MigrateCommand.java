package dev.foodcans.enhancedhealth.command.admin;

import dev.foodcans.enhancedhealth.EnhancedHealth;
import dev.foodcans.enhancedhealth.data.HealthDataManager;
import dev.foodcans.enhancedhealth.settings.lang.Lang;
import dev.foodcans.pluginutils.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class MigrateCommand extends SubCommand
{
    public MigrateCommand()
    {
        super("migrate", "enhancedhealth.command.migrate", Collections.emptyList());
    }

    @Override
    public void onCommand(CommandSender sender, String... args)
    {
        if (Bukkit.getOnlinePlayers().size() > 0)
        {
            Lang.MIGRATE_NO_PLAYERS.sendMessage(sender);
            return;
        }

        EnhancedHealth.getInstance().migrate(sender);
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
