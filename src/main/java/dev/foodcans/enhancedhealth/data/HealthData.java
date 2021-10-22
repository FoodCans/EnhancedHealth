package dev.foodcans.enhancedhealth.data;

import dev.foodcans.enhancedhealth.settings.Config;

import java.util.UUID;

import static dev.foodcans.enhancedhealth.data.HealthDataManager.round;

public class HealthData
{
    private final UUID uuid;
    private double extraHealth;
    private double health;

    public HealthData(UUID uuid)
    {
        this(uuid, 0, Config.BASE_HEALTH);
    }

    public HealthData(UUID uuid, double extraHealth, double health)
    {
        this.uuid = uuid;
        this.extraHealth = extraHealth;
        this.health = health;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public double getExtraHealth()
    {
        return round(extraHealth);
    }

    public void setExtraHealth(double extraHealth)
    {
        this.extraHealth = extraHealth;
    }

    public double getHealth()
    {
        return round(health);
    }

    public void setHealth(double health)
    {
        this.health = health;
    }
}
