package com.onemoreblock.oml;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Created by epowerj on 9/8/14.
 */
public class EventManager implements Listener {

    @EventHandler
    public void stopHungerChange(FoodLevelChangeEvent event) {
        if ((event.getEntity() instanceof Player)) {
            Player player = (Player) event.getEntity();
            event.setCancelled(true);
            player.setFoodLevel(6);
        }
    }
}
