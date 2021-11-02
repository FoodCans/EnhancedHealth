package dev.foodcans.enhancedhealth.command;

import dev.foodcans.enhancedhealth.data.HealthDataManager;
import dev.foodcans.enhancedhealth.settings.lang.Lang;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand implements CommandExecutor
{
    private HealthDataManager healthDataManager;

    public HealCommand(HealthDataManager healthDataManager)
    {
        this.healthDataManager = healthDataManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!sender.hasPermission("enhancedhealth.command.heal"))
        {
            Lang.NO_PERMISSION_COMMAND.sendMessage(sender, "enhancedhealth.command.heal");
            return true;
        }
        Player target;
        if (args.length == 0 && !(sender instanceof  Player))
        {
            Lang.COMMAND_ONLY_RUN_BY_PLAYERS.sendMessage(sender);
            return true;
        } else if (args.length == 0)
        {
            target = (Player) sender;
        } else
        {
            if (!sender.hasPermission("enhancedhealth.command.heal.other"))
            {
                Lang.NO_PERMISSION_COMMAND.sendMessage(sender, "enhancedhealth.command.heal.other");
                return true;
            }
            target = Bukkit.getPlayer(args[0]);
            if (target == null)
            {
                Lang.PLAYER_NOT_FOUND.sendMessage(sender, args[0]);
                return true;
            }
        }
        target.setHealth(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        healthDataManager.setLowHealth(target.getUniqueId(), false);
        if (args.length == 0)
        {
            Lang.SELF_HEALED.sendMessage(sender);
        } else
        {
            Lang.PLAYER_HEALED.sendMessage(sender, target.getName());
        }
        return true;
    }
}
