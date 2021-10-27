package dev.foodcans.enhancedhealth.settings.lang;

import dev.foodcans.enhancedhealth.EnhancedHealth;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

import static dev.foodcans.pluginutils.PluginUtils.Strings.translateColor;

public enum Lang
{
    EXTRA_HEALTH_ADD_OVER_MAX("Extra-Health-Add-Over-Max",
            "&8<&cHealth&8> &7Unable to give &f{0} {1} &7extra max health as it would put them over the max extra health limit of &f{2} &7as defined in the config!"),
    EXTRA_HEALTH_ADDED("Extra-Health-Added",
            "&8<&9Health&8> &f{0} &7extra max health has been given to &f{1}"),

    EXTRA_HEALTH_REMOVED("Extra-Health-Removed",
            "&8<&9Health&8> &f{0} &7extra max health has been taken from &f{1}"),
    EXTRA_HEALTH_REMOVE_UNDER_ZERO("Extra-Health-Remove-Under-Zero",
            "&8<&cHealth&8> &7Unable to take &f{0} &7extra max health from &f{1} &7as it would put them under zero!"),

    EXTRA_HEALTH_SET_OVER_MAX("Extra-Health-Set-Over-Max",
            "&8<&cHealth&8> &7Unable to set extra max health for &f{0} &7to &f{1} &7as it would put them over the max extra health limit of &f{2} &7as defined in the config!"),
    EXTRA_HEALTH_SET_UNDER_ZERO("Extra-Health-Set-Under-Zero",
            "&8<&cHealth&8> &7Unable to set extra max health for &f{0} &7to &f{1} &7as it would put them under zero!"),
    Extra_HEALTH_SET("Extra-Health-Set",
            "&8<&9Health&8> &7Extra max health has been set to &f{0} &7for &f{1}"),

    PLAYER_REFRESHED("Player-Refreshed", "&8<&9Health&8> &7Health data refreshed for &f{0}&7!"),
    PLAYER_RESET("Player-Reset", "&8<&9Health&8> &7Health data reset for &f{0}&7!"),

    CONFIG_RELOADED("Config-Reloaded", "&8<&9Health&8> &7Config and players reloaded!"),

    PLAYER_STATUS("Player-Status",
            "&8<&9Health&8> &7Status for &f{0}\n&8<&9Health&8> &7Current health: &f{1}\n&8<&9Health&8> &7Extra max health: &f{2}"),

    MIGRATION_IN_PROGRESS("Migration-In-Progress", "&cData migration in progress!"),
    MIGRATE_NO_PLAYERS("Migrate-No-Players",
            "&8<&cHealth&8> &7This command may only be run with no players online. Please put your server into maintenance mode to prevent player's from joining while migration is in progress!"),
    DATA_MIGRATED("Data-Migrated",
            "&8<&9Health&8> &7Successfully migrated data from &f{0} &7to &f{1}&7. Please set your desired storage type in the config before restarting."),

    LOW_HEALTH("Low-Health", "&cLow Health!"),

    PLAYER_NOT_FOUND("Player-Not-Found", "&8<&cHealth&8> &7Player &f{0} &7not found!"),
    HELP("Help", "&8<&9Health&8> &7Available commands: &f/health {0}"),
    HELP_NONE("Help-None", "&8<&cHealth&8> &7No available commands!"),
    NOT_ENOUGH_ARGS("Not-Enough-Args", "&8<&cHealth&8> &7Not enough args: &f{0}"),
    TOO_MANY_ARGS("Too-Many-Args", "&8<&cHealth&8> &7Too many args: &f{0}"),
    COMMAND_ONLY_RUN_BY_PLAYERS("Command-Only-Run-By-Players",
            "&8<&cHealth&8> &7This command can only be run by players."),
    LIST_COMMANDS("List-Commands",
            "&8<&cHealth&8> &7To view a list of commands type: &f/health help"),
    COMMAND_NOT_FOUND("Command-Not-Found",
            "&8<&cHealth&8> &7Command not found, to view a list of commands type: &f/health help"),
    ARGUMENT_MUST_BE_INTEGER("Argument-Must-Be-Integer",
            "&8<&cHealth&8> &f{0} &7must be an integer."),
    ARGUMENT_MUST_BE_DOUBLE("Argument-Must-Be-Double",
            "&8<&cHealth&8> &f{0} &7must be an number with or without decimal places."),
    NO_PERMISSION_COMMAND("No-Permission-Command",
            "&8<&cHealth&8> &7You do not have the required permission to perform this command: &f{0}");

    private final String path;
    private final String def;

    Lang(String path, String def)
    {
        this.path = path;
        this.def = def;
    }

    public void sendMessage(CommandSender sender, String... replacements)
    {
        String[] messages = replace(replacements).split("\\n");
        Arrays.stream(messages).forEach((message) -> sender.sendMessage(translateColor(message)));
    }

    private String replace(String... replacements)
    {
        String message = getValue();
        for (int i = 0; i < replacements.length; i++)
        {
            message = message.replace("{" + i + "}", replacements[i]);
        }
        return message;
    }

    public String getValue()
    {
        return EnhancedHealth.getInstance().getLangFile().getValue(path, def);
    }

    public String getPath()
    {
        return path;
    }

    public String getDefault()
    {
        return def;
    }
}
