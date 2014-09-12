package com.onemoreblock.oml;

import org.bukkit.*;
import org.bukkit.entity.Player;

/**
 * Created by epowerj on 9/8/14.
 */
public class LevelManager {

    public World create(String name, Player creator) {
        //TODO check if exists
        if (!levelExists(name)) {
            WorldCreator wc = new WorldCreator(name);
            wc.generator("VoidGenerator");
            wc.generateStructures(false);
            wc.environment(org.bukkit.World.Environment.NORMAL);
            Vix.getDB().register(name, creator.getDisplayName());
            World world = wc.createWorld();
            makeBlock(world);
            return world;
        } else {
            return null;
        }
    }

    public World getWorld(String name) {
        World world = Bukkit.getWorld(name);
        return Bukkit.getServer().createWorld(new WorldCreator("WorldName"));
    }

    public void unloadWorld(World world) {
        Bukkit.getServer().unloadWorld(world, true);
    }

    private void makeBlock(World world) {
        Location loc = world.getSpawnLocation();
        world.getBlockAt(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ()).setType(Material.BEDROCK);
    }

    public boolean levelExists(String name) {
        return Vix.getDB().levelExists(name);
    }
}
