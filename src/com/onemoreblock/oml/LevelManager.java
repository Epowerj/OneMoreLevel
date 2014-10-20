package com.onemoreblock.oml;

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * Created by epowerj on 9/8/14.
 */
public class LevelManager {

    public World create(String name, Player creator) {
        //TODO check if exists
        if (!levelExists(name)) {
            Vix.getDB().register(name, creator.getDisplayName());
            World world = createWorld(name);
            makeBlock(world);
            return world;
        } else {
            return null;
        }
    }

    private World createWorld(String name) {
        WorldCreator wc = new WorldCreator(name);
        wc.generator("VoidGenerator");
        wc.generateStructures(false);
        wc.environment(org.bukkit.World.Environment.NORMAL);
        World world = wc.createWorld();
        makeBlock(world);
        return world;
    }

    public World clone(World world, Player player) {
        String worldname = (world.getName() + "@" + player.getName());
        return copyWorld(world, worldname);
    }

    private World copyWorld(World source, String copyWorld) {
        WorldCreator wc = new WorldCreator(copyWorld);
        wc.copy(source);
        return wc.createWorld();
    }


    public World getWorld(String name) {
        World world = Bukkit.getWorld(name);
        if (levelExists(name) && world == null) {
            return Bukkit.getServer().createWorld(new WorldCreator(name));
        } else {
            return world;
        }
    }

    public void deleteWorld(World world, Player player) {
        if (player.getWorld().getName() != "world") {
            player.teleport(getWorld("world").getSpawnLocation());
        }
        File worldfolder = world.getWorldFolder();
        unloadWorld(world);
        worldfolder.delete();
    }

    public void unloadWorld(World world) {
        world = Bukkit.getWorld("");
        if (!world.equals(null)) {
            Bukkit.getServer().unloadWorld(world, true);
        }
    }

    private void makeBlock(World world) {
        Location loc = world.getSpawnLocation();
        world.getBlockAt(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ()).setType(Material.BEDROCK);
    }

    public boolean levelExists(String name) {
        return Vix.getDB().levelExists(name);
    }
}
