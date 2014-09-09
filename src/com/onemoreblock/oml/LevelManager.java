package com.onemoreblock.oml;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

/**
 * Created by epowerj on 9/8/14.
 */
public class LevelManager {

    public World create(String name, Player creator) {
        WorldCreator wc = new WorldCreator(name);
        wc.generator("VoidGenerator");
        wc.generateStructures(false);
        wc.environment(org.bukkit.World.Environment.NORMAL);
        //TODO register level and creator to database
        World world = wc.createWorld();
        makeBlock(world);
        return world;
    }

    private void makeBlock(World world) {
        Location loc = world.getSpawnLocation();
        world.getBlockAt(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ()).setType(Material.BEDROCK);
    }
}
