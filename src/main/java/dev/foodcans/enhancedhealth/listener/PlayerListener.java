package dev.foodcans.enhancedhealth.listener;

import dev.foodcans.enhancedhealth.EnhancedHealth;
import dev.foodcans.enhancedhealth.data.HealthDataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener
{
    private final HealthDataManager healthDataManager;

    public PlayerListener(HealthDataManager healthDataManager)
    {
        this.healthDataManager = healthDataManager;
    }

    @EventHandler(priority= EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        EnhancedHealth.getInstance().getStorage().loadStorage(player.getUniqueId(), (healthData) ->
        {
            healthDataManager.addHealthData(healthData);
            healthDataManager.applyMaxHealthToPlayer(player);
            healthDataManager.applyHealthToPlayer(player);
        });
    }

    @EventHandler(priority= EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        healthDataManager.setCurrentHealth(player.getUniqueId(), player.getHealth());
        healthDataManager.resetPlayerHealth(player);
        healthDataManager.removeHealthData(player.getUniqueId());
    }
}
