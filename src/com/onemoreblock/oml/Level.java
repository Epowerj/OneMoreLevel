package com.onemoreblock.oml;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Level {

    public String name;
    public String designer;
    public String time = "day";
    boolean published = false;

    public Level(String levelname, Player leveldesigner) {
        World world = Vix.getWorldManager().createWorld(levelname);
        world.setAmbientSpawnLimit(0);
        world.setAnimalSpawnLimit(0);
        world.setAutoSave(true);
        // TODO get spawn location and biome also difficulty
        // setGameRules()
        world.setBiome(0, 0, org.bukkit.block.Biome.PLAINS);
        world.setDifficulty(org.bukkit.Difficulty.EASY);
        world.setPVP(false);
        Vix.getWorldManager().unloadWorld(world);
        name = levelname;
        designer = leveldesigner.getName();

        saveLevel();

        leveldesigner.sendMessage("Level Created!");
        leveldesigner.sendMessage("Use /edit <level>");
    }

    public Level(String s) {
        if (isNameValid(s) && Vix.getDBManager().levelExists(s)) {
            name = s;
            designer = Vix.getDBManager().getDesigner(s);
            published = Vix.getDBManager().isLevelPublished(s);
        }
    }

    public World createClone() {
        return Vix.getWorldManager().cloneWorld(name);

		/*Integer i = 0;
        while ((Vix.worldman.worldExists(name + "@" + i.toString())) == true) {
			i++;
		}
		String clonename = name + "@" + i.toString();
		File srcFolder = new File(name);
		File destFolder = new File(clonename);
		try {
			copyFolder(srcFolder, destFolder);
			File uid = new File(clonename + "/uid.dat");
			uid.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Vix.worldman.createWorld(clonename);
		World world = Bukkit.getWorld(clonename);
		world.setBiome(0, 0, org.bukkit.block.Biome.PLAINS);
		world.setDifficulty(org.bukkit.Difficulty.EASY);
		world.setPVP(false);
		world.setGameRuleValue("mobGriefing", "false");
		world.setGameRuleValue("doFireTick", "false");
		world.setGameRuleValue("doDaylightCycle", "false");
		world.setDifficulty(Difficulty.NORMAL);
		return world;*/
    }

    public boolean isPublished() {
        return Vix.getDBManager().isLevelPublished(name);
    }

    public boolean isDesigner(String player) {
        return designer.equals(player);
    }

    public Boolean isNameValid(String name) {
        Boolean bool;
        int i = name.indexOf('@');
        if (i != -1) {
            bool = false;
        } else {
            bool = true;
        }
        if (bool && name != "world") {
            return true;
        } else {
            return false;
        }
    }

    public World getWorld() {
        if (Vix.getDBManager().levelExists(name)) {
            Vix.getWorldManager().createWorld(name);
            World world = Vix.getWorldManager().getWorld(name);
            world.setBiome(0, 0, org.bukkit.block.Biome.PLAINS);
            world.setDifficulty(org.bukkit.Difficulty.EASY);
            world.setPVP(false);
            world.setGameRuleValue("mobGriefing", "false");
            world.setGameRuleValue("doFireTick", "false");
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setDifficulty(Difficulty.NORMAL);
            return world;
        }
        return null;
    }

    public Location getSpawnLocation() {
        return getWorld().getSpawnLocation();
    }

    public void saveLevel() {
        Vix.getDBManager().saveLevel(this);
    }

    void generateCube(Location loc) {
        World w = loc.getWorld();
        Block blockToChange = w.getBlockAt(loc);
        blockToChange.setType(org.bukkit.Material.BED_BLOCK);
    }
    // TODO
	/*
	 * public MultiverseWorld getWorld() { return world; }
	 */
}
