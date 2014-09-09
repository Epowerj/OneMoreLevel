package com.onemoreblock.oml;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class WorldManager extends JavaPlugin {
    private World emptyWorld;

    public static void copyFolder(File src, File dest) throws IOException {

        if (src.isDirectory()) {

            if (!dest.exists()) {
                dest.mkdir();
            }

            String files[] = src.list();

            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                copyFolder(srcFile, destFile);
            }

        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
            System.out.println("File copied from " + src + " to " + dest);
        }
    }

    public boolean areWorldsEqual(String world1, String world2) {
        if (Bukkit.getWorld(world1) != null || Bukkit.getWorld(world2) != null) {
            return false;
        }
        if (Bukkit.getWorld(world1).equals(Bukkit.getWorld(world2))) {
            return true;
        }
        return false;
    }

    public World createWorld(String name) {
        WorldCreator wc = new WorldCreator(name);
        wc.generator("VoidGenerator");
        wc.generateStructures(false);
        //TODO Environments could be set by the player
        wc.environment(org.bukkit.World.Environment.NORMAL);
        return wc.createWorld();
    }

    public World cloneWorld(String name) {
        Integer i = 0;
        while ((Vix.getWorldManager().worldExists(name + "@" + i.toString())) == true) {
            i++;
        }
        String clonename = name + "@" + i.toString();
        WorldCreator wc = new WorldCreator(clonename);
        wc.copy(getWorld(name));
        World world = wc.createWorld();

        File srcFolder = new File(name);
        File destFolder = new File(clonename);
        try {
            copyFolder(srcFolder, destFolder);
            File uid = new File(clonename + "/uid.dat");
            uid.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return world;
    }

    public void deleteWorld(String name) {
        //getServer().unloadWorld(name, true);
        File world_files = new File(name + "/");
        world_files.delete();
    }

    public boolean worldExists(String name) {
        if (Bukkit.getWorld(name) != null) {
            return true;
        } else {
            return false;
        }
    }

    public void unloadWorld(World world) {
        this.emptyWorld = Bukkit.getWorld("");
        if (!world.equals(null)) {
            Bukkit.getServer().unloadWorld(world, true);
        }
    }

    public Location getSpawnLocation(String name) {
        return getWorld(name).getSpawnLocation();
    }

    public World getWorld(String name) {
        return Bukkit.getWorld(name);
    }
}
