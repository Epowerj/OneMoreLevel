package com.onemoreblock.oml;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EventManager implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDisconnect(PlayerKickEvent event) {
        Player player = event.getPlayer();
        Vix.getPerms()
                .playerRemove((String) null, player.getName(), "Vix.edit");
        Vix.getPerms().playerRemove((String) null, player.getName(),
                "voxelsniper.litesniper");
        Inventory invent = player.getInventory();
        invent.clear();
        World world = player.getLocation().getWorld();
        if (Vix.worldman.areWorldsEqual(world.getName(), "world")) {
            if (Vix.isNameValid(world.getName()) == false) {
                Vix.worldman.deleteWorld(world.getName());
            } else {
                Vix.worldman.removeWorldFromConfig(world.getName());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Vix.getPerms()
                .playerRemove((String) null, player.getName(), "Vix.edit");
        Vix.getPerms().playerRemove((String) null, player.getName(),
                "voxelsniper.litesniper");
        Inventory invent = player.getInventory();
        invent.clear();
        World world = player.getLocation().getWorld();
        if (Vix.worldman.areWorldsEqual(world.getName(), "world")) {
            if (Vix.isNameValid(world.getName()) == false) {
                Vix.worldman.deleteWorld(world.getName());
            } else {
                Vix.worldman.removeWorldFromConfig(world.getName());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        Vix.getPerms()
                .playerRemove((String) null, player.getName(), "Vix.edit");
        Vix.getPerms().playerRemove((String) null, player.getName(),
                "voxelsniper.litesniper");
        Inventory invent = player.getInventory();
        invent.clear();
        player.teleport(Vix.getLeave());
        Bukkit.getServer().getScheduler()
                .runTaskLater(Vix.getPlugin(), new Runnable() {
                    public void run() {
                        Vix.exit(player);
                    }
                }, 5L);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == org.bukkit.GameMode.SURVIVAL) {
            Block block = event.getClickedBlock();
            if (block.getType() == org.bukkit.Material.DISPENSER
                    || block.getType() == org.bukkit.Material.JUKEBOX) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == org.bukkit.GameMode.SURVIVAL) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == org.bukkit.GameMode.SURVIVAL) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == org.bukkit.GameMode.CREATIVE) {
            event.setCancelled(true);
        } else {
            Vix.exit(player);
            player.teleport(Vix.getLeave());
            player.sendMessage("Congrats, you completed the level!");
        }
    }

    @EventHandler
    public void playerJoin(final PlayerJoinEvent event) {
        Bukkit.getServer().getScheduler()
                .runTaskLater(Vix.getPlugin(), new Runnable() {
                    public void run() {
                        change(event.getPlayer());
                    }
                }, 5L);
    }

    @EventHandler
    public void playerRespawn(final PlayerRespawnEvent event) {
        Bukkit.getServer().getScheduler()
                .runTaskLater(Vix.getPlugin(), new Runnable() {
                    public void run() {
                        change(event.getPlayer());
                    }
                }, 5L);
    }

    @EventHandler
    public void changedGameMode(PlayerGameModeChangeEvent event) {
        change(event.getPlayer());
    }

    @EventHandler
    public void stopHungerChange(FoodLevelChangeEvent event) {
        if ((event.getEntity() instanceof Player)) {
            Player player = (Player) event.getEntity();
            event.setCancelled(true);
            player.setFoodLevel(6);
        }
    }

    public void change(Player player) {
        PotionEffect speed = new PotionEffect(PotionEffectType.SPEED,
                Integer.MAX_VALUE, 0);
        PotionEffect jump = new PotionEffect(PotionEffectType.JUMP,
                Integer.MAX_VALUE, 0);
        player.addPotionEffect(speed);
        player.addPotionEffect(jump);
        player.setFoodLevel(6);
    }
}
