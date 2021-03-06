package dev.foodcans.enhancedhealth.settings;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Config
{
    public static boolean MIGRATE;
    public static StorageType STORAGE_TYPE;

    public static String DB_URL;
    public static String DB_USERNAME;
    public static String DB_PASSWORD;
    public static boolean DB_ALLOW_PUBLIC_KEY_RETRIEVAL;
    public static boolean DB_USE_SSL;

    public static boolean SHOW_LOW_HEALTH;
    public static int LOW_HEALTH_THRESHOLD;
    public static boolean SCALE_HEALTH;
    public static double HEALTH_SCALE_VALUE;
    public static double MAX_EXTRA_HEALTH_ALLOWED;
    public static double BASE_HEALTH;
    public static Map<String, Double> PERMISSION_GROUP_MAP;

    public static void load(FileConfiguration config)
    {
        MIGRATE = config.getBoolean("Migrate");
        STORAGE_TYPE = StorageType.valueOf(config.getString("Storage-Type").toUpperCase(Locale.ROOT));

        ConfigurationSection mySQLSection = config.getConfigurationSection("MySQL");
        DB_URL = mySQLSection.getString("Url");
        DB_USERNAME = mySQLSection.getString("Username");
        DB_PASSWORD = mySQLSection.getString("Password");
        DB_ALLOW_PUBLIC_KEY_RETRIEVAL = mySQLSection.getBoolean("Allow-Public-Key-Retrieval");
        DB_USE_SSL = mySQLSection.getBoolean("Use-SSL");

        SHOW_LOW_HEALTH = config.getBoolean("Show-Low-Health-Indicator");
        LOW_HEALTH_THRESHOLD = config.getInt("Low-Health-Threshold");
        SCALE_HEALTH = config.getBoolean("Scale-Health");
        HEALTH_SCALE_VALUE = config.getDouble("Health-Scale-Value");
        MAX_EXTRA_HEALTH_ALLOWED = config.getDouble("Max-Extra-Health-Allowed");
        BASE_HEALTH = config.getDouble("Base-Health");

        PERMISSION_GROUP_MAP = new HashMap<>();
        ConfigurationSection permissionSection = config.getConfigurationSection("Permission-Extra-Health");
        for (String group : permissionSection.getKeys(false))
        {
            PERMISSION_GROUP_MAP.put(group, permissionSection.getDouble(group));
        }
    }

    public enum StorageType
    {
        JSON, MYSQL
    }
}
