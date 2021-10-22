package dev.foodcans.enhancedhealth.data;

import dev.foodcans.enhancedhealth.EnhancedHealth;
import dev.foodcans.enhancedhealth.settings.Config;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HealthDataManager
{
    private final Map<UUID, HealthData> healthDataMap;

    public HealthDataManager()
    {
        this.healthDataMap = new HashMap<>();
    }

    public void addHealthData(HealthData healthData)
    {
        healthDataMap.put(healthData.getUuid(), healthData);
    }

    public void removeHealthData(UUID uuid)
    {
        healthDataMap.remove(uuid);
    }

    public Result addExtraHealth(UUID uuid, double extraHealth)
    {
        extraHealth = round(extraHealth);
        HealthData healthData = healthDataMap.getOrDefault(uuid, new HealthData(uuid));
        double combined = healthData.getExtraHealth() + extraHealth;
        if (combined > Config.MAX_EXTRA_HEALTH_ALLOWED)
        {
            return Result.OVER_MAX;
        }
        healthData.setExtraHealth(Math.min(2048, combined));
        healthDataMap.put(uuid, healthData);
        EnhancedHealth.getInstance().getStorage().saveStorage(healthData);
        return Result.SUCCESS;
    }

    public Result removeExtraHealth(UUID uuid, double extraHealth)
    {
        extraHealth = round(extraHealth);
        HealthData healthData = healthDataMap.getOrDefault(uuid, new HealthData(uuid));
        double combined = healthData.getExtraHealth() - extraHealth;
        if (combined < 0)
        {
            return Result.LESS_THAN_ZERO;
        }
        healthData.setExtraHealth(combined);
        healthDataMap.put(uuid, healthData);
        EnhancedHealth.getInstance().getStorage().saveStorage(healthData);
        return Result.SUCCESS;
    }

    public Result setExtraHealth(UUID uuid, double extraHealth)
    {
        extraHealth = round(extraHealth);
        HealthData healthData = healthDataMap.getOrDefault(uuid, new HealthData(uuid));
        if (extraHealth < 0)
        {
            return Result.LESS_THAN_ZERO;
        } else if (extraHealth > Config.MAX_EXTRA_HEALTH_ALLOWED)
        {
            return Result.OVER_MAX;
        }
        healthData.setExtraHealth(extraHealth);
        healthDataMap.put(uuid, healthData);
        EnhancedHealth.getInstance().getStorage().saveStorage(healthData);
        return Result.SUCCESS;
    }

    public void setCurrentHealth(UUID uuid, double health)
    {
        health = round(health);
        HealthData healthData = healthDataMap.getOrDefault(uuid, new HealthData(uuid));
        healthData.setHealth(health);
        healthDataMap.put(uuid, healthData);
        EnhancedHealth.getInstance().getStorage().saveStorage(healthData);
    }

    public void applyMaxHealthToPlayer(Player player)
    {
        HealthData healthData = healthDataMap.get(player.getUniqueId());
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(getPermissiveBonus(player) + Config.BASE_HEALTH + healthData.getExtraHealth());
        if (player.getHealth() > player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue())
        {
            setCurrentHealth(player.getUniqueId(), player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        }
        healthData.setHealth(player.getHealth());
    }

    public void applyHealthToPlayer(Player player)
    {
        HealthData healthData = healthDataMap.get(player.getUniqueId());
        player.setHealth(healthData.getHealth());
        setCurrentHealth(player.getUniqueId(), player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
        if (Config.SCALE_HEALTH)
        {
            player.setHealthScale(Config.HEALTH_SCALE_VALUE);
        }
    }

    public void resetPlayerHealth(Player player)
    {
        double defaultValue = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue();
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(defaultValue);
        player.setHealthScale(20);
        player.setHealthScaled(false);
    }

    public HealthData getHealthData(UUID uuid)
    {
        return healthDataMap.get(uuid);
    }

    public double getPermissiveBonus(Player player)
    {
        double bonus = 0;
        for (String group : Config.PERMISSION_GROUP_MAP.keySet())
        {
            double amount = Config.PERMISSION_GROUP_MAP.get(group);
            if (player.isPermissionSet("group." + group) && amount > bonus)
            {
                bonus = amount;
            }
        }
        return bonus;
    }

    private double round(double input)
    {
        return new BigDecimal(input).setScale(1, RoundingMode.HALF_EVEN).doubleValue();
    }

    public enum Result
    {
        OVER_MAX, LESS_THAN_ZERO, SUCCESS
    }
}
