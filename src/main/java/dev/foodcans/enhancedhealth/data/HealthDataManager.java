package dev.foodcans.enhancedhealth.data;

import dev.foodcans.enhancedhealth.EnhancedHealth;
import dev.foodcans.enhancedhealth.settings.Config;
import dev.foodcans.enhancedhealth.settings.lang.Lang;
import dev.foodcans.enhancedhealth.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class HealthDataManager
{
    private final Map<UUID, HealthData> healthDataMap;
    private final Set<UUID> lowHealthSet;

    public HealthDataManager()
    {
        this.healthDataMap = new HashMap<>();
        this.lowHealthSet = new HashSet<>();
        this.startLowHealthTask();
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
        HealthData healthData = healthDataMap.get(uuid);
        double combined = healthData.getExtraHealth() + extraHealth;
        if (combined > Config.MAX_EXTRA_HEALTH_ALLOWED)
        {
            return Result.OVER_MAX;
        }
        healthData.setExtraHealth(Math.min(2048, combined));
        EnhancedHealth.getInstance().getStorage().saveStorage(healthData);
        return Result.SUCCESS;
    }

    public Result removeExtraHealth(UUID uuid, double extraHealth)
    {
        extraHealth = round(extraHealth);
        HealthData healthData = healthDataMap.get(uuid);
        double combined = healthData.getExtraHealth() - extraHealth;
        if (combined < 0)
        {
            return Result.LESS_THAN_ZERO;
        }
        healthData.setExtraHealth(combined);
        EnhancedHealth.getInstance().getStorage().saveStorage(healthData);
        return Result.SUCCESS;
    }

    public Result setExtraHealth(UUID uuid, double extraHealth)
    {
        extraHealth = round(extraHealth);
        HealthData healthData = healthDataMap.get(uuid);
        if (extraHealth < 0)
        {
            return Result.LESS_THAN_ZERO;
        } else if (extraHealth > Config.MAX_EXTRA_HEALTH_ALLOWED)
        {
            return Result.OVER_MAX;
        }
        healthData.setExtraHealth(extraHealth);
        EnhancedHealth.getInstance().getStorage().saveStorage(healthData);
        return Result.SUCCESS;
    }

    public void setCurrentHealth(UUID uuid, double health)
    {
        health = round(health);
        HealthData healthData = healthDataMap.get(uuid);
        healthData.setHealth(health);
        EnhancedHealth.getInstance().getStorage().saveStorage(healthData);
    }

    public void applyMaxHealthToPlayer(Player player, boolean resetHealth)
    {
        HealthData healthData = healthDataMap.get(player.getUniqueId());
        double extra = Math.min(Config.MAX_EXTRA_HEALTH_ALLOWED, getPermissiveBonus(player) + healthData.getExtraHealth());
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(Config.BASE_HEALTH + extra);
        if (player.getHealth() > player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue())
        {
            setCurrentHealth(player.getUniqueId(), player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        }
        if (resetHealth)
        {
            healthData.setHealth(player.getHealth());
        }
    }

    public void applyHealthToPlayer(Player player)
    {
        HealthData healthData = healthDataMap.get(player.getUniqueId());
        if (healthData.getHealth() > player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue())
        {
            healthData.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        }
        player.setHealth(healthData.getHealth());
        if (Config.SCALE_HEALTH)
        {
            player.setHealthScale(Config.HEALTH_SCALE_VALUE);
        } else
        {
            player.setHealthScaled(false);
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

    public void setLowHealth(UUID uuid, boolean hasLowHealth)
    {
        if (hasLowHealth)
        {
            lowHealthSet.add(uuid);
        } else
        {
            lowHealthSet.remove(uuid);
        }
    }

    protected static double round(double input)
    {
        return new BigDecimal(input).setScale(1, RoundingMode.HALF_EVEN).doubleValue();
    }

    private void startLowHealthTask()
    {
        Bukkit.getScheduler().runTaskTimer(EnhancedHealth.getInstance(), () ->
        {
            if (!Config.SHOW_LOW_HEALTH)
            {
                return;
            }
            for (UUID uuid : healthDataMap.keySet())
            {
                if (lowHealthSet.contains(uuid))
                {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null)
                    {
                        continue;
                    }
                    player.sendTitle(" ", StringUtil.translate(Lang.LOW_HEALTH.getValue()), -1, -1, -1);
                }
            }
        }, 0L, 20L);
    }

    public enum Result
    {
        OVER_MAX, LESS_THAN_ZERO, SUCCESS
    }
}
