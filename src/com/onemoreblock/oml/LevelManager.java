package com.onemoreblock.oml;

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by epowerj on 9/8/14.
 */
public class LevelManager {
    HashMap<String, Integer> levels = new HashMap<String, Integer>();

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

    public World clone(World world) {
        int cloneNum;
        if (levels.containsValue(world.getName())) {
            cloneNum = levels.get(world.getName()) + 1;
        } else {
            cloneNum = 1;
        }

        levels.put(world.getName(), cloneNum);
        String worldname = (world.getName() + "@" + levels.get(world.getName()));
        copyWorld(world.getWorldFolder(), Bukkit.getWorld(worldname).getWorldFolder());
        return getWorld(worldname);
    }

    private void copyWorld(File source, File target) {
        try {
            ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
            if (!ignore.contains(source.getName())) {
                if (source.isDirectory()) {
                    if (!target.exists())
                        target.mkdirs();
                    String files[] = source.list();
                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyWorld(srcFile, destFile);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public World getWorld(String name) {
        World world = Bukkit.getWorld(name);
        if (levelExists(name) && world == null) {
            return Bukkit.getServer().createWorld(new WorldCreator(name));
        } else {
            return world;
        }
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
