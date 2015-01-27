package com.onemoreblock.oml;

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

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
        copyWorld(world.getWorldFolder(), createWorld(worldname).getWorldFolder());
        return getWorld(worldname);
    }

    public void copyWorld(File source, File target) {
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

    public void deleteWorld(World world, Player player) {
        if (!player.getWorld().getName().equals("world")) {
            player.teleport(getWorld("world").getSpawnLocation());
        }
        File worldfolder = world.getWorldFolder();
        deleteWorld(worldfolder);
    }

    public boolean deleteWorld(File path) {
        if (path.exists()) {
            File files[] = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteWorld(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
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
