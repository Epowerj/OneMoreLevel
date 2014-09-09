package com.onemoreblock.oml;

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
        return wc.createWorld();
    }
}
