package dev.foodcans.enhancedhealth.listener;

import dev.foodcans.enhancedhealth.EnhancedHealth;
import dev.foodcans.enhancedhealth.data.HealthDataManager;
import dev.foodcans.enhancedhealth.settings.lang.Lang;
import dev.foodcans.enhancedhealth.util.StringUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener
{
    private final HealthDataManager healthDataManager;

    public PlayerListener(HealthDataManager healthDataManager)
    {
        this.healthDataManager = healthDataManager;
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event)
    {
        if (EnhancedHealth.getInstance().isMigrating())
        {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, StringUtil.translate(Lang.MIGRATION_IN_PROGRESS.getValue()));
        }
    }

    @EventHandler(priority= EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        EnhancedHealth.getInstance().getStorage().loadStorage(player.getUniqueId(), (healthData) ->
        {
            healthDataManager.addHealthData(healthData);
            healthDataManager.applyMaxHealthToPlayer(player, false);
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
