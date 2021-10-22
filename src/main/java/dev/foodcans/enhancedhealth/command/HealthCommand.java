package dev.foodcans.enhancedhealth.command;

import dev.foodcans.enhancedhealth.data.HealthDataManager;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class HealthCommand
{
    protected final HealthDataManager healthDataManager;
    private final String name;
    private final String permission;
    private final List<String> args;

    protected HealthCommand(HealthDataManager healthDataManager, String name, String permission, List<String> args)
    {
        this.healthDataManager = healthDataManager;
        this.name = name;
        this.permission = permission;
        this.args = args;
    }

    public abstract void onCommand(CommandSender sender, String... args);

    String getName()
    {
        return name;
    }

    String getPermission()
    {
        return permission;
    }

    List<String> getArgs()
    {
        return args;
    }

    public abstract int getMinArgs();

    public abstract int getMaxArgs();

    public abstract boolean allowConsoleSender();

    protected boolean isInt(String input)
    {
        try
        {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e)
        {
            return false;
        }
    }

    protected boolean isDouble(String input)
    {
        try
        {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e)
        {
            return false;
        }
    }
}
