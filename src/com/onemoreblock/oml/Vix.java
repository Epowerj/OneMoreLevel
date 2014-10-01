package com.onemoreblock.oml;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by epowerj on 9/8/14.
 */
public class Vix extends JavaPlugin {
    private static LevelManager lvlman = new LevelManager();
    private EventManager eventman = new EventManager();
    private static Database db = new Database();
    public static Permission permission = null;

    public final String cc = ""; //color code
    public static final String perms = "bukkit.command.gamemode";

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(eventman, this);
        setupPermissions();
        db.load();
    }

    @Override
    public void onDisable() {
        db.close();
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (cmd.getName().equalsIgnoreCase("exit")) {
                exitCommand(player);
                return true;
            }

            if (args.length > 0) {
                if (cmd.getName().equalsIgnoreCase("create")) {
                    createCommand(player, args[0]);
                    return true;
                }

                if (cmd.getName().equalsIgnoreCase("edit")) {
                    editCommand(player, args[0]);
                    return true;
                }

                if (cmd.getName().equalsIgnoreCase("play")) {
                    playCommand(player, args[0]);
                    return true;
                }
            } else {
                return false;
            }
        } else {
            sender.sendMessage(cc + "This command cannot be used from the console");
        }
        return true;
    }

    private void createCommand(Player player, String name) {
        if (worldNameLegit(name)) {
            player.sendMessage("Creating " + name);
            World world = lvlman.create(name, player);
            if (world != null) {
                player.teleport(world.getSpawnLocation());
                permission.playerAdd(player, perms);
                player.setGameMode(GameMode.CREATIVE);
            } else {
                player.sendMessage(cc + "The world " + name + " already exists!");
            }
        } else {
            player.sendMessage(cc + "The world name cannot contain '@'");
        }
    }

    private void playCommand(Player player, String name) {
        player.sendMessage(cc + "Loading " + name);
        World world = lvlman.clone(lvlman.getWorld(name), player);
        player.teleport(world.getSpawnLocation());
    }

    private void editCommand(Player player, String name) {
        if (db.levelExists(name) && db.isDesigner(name, player.getDisplayName())) {
            World world = lvlman.getWorld(name);

            player.teleport(world.getSpawnLocation());
            permission.playerAdd(player, perms);
            player.setGameMode(GameMode.CREATIVE);
        } else {
            player.sendMessage(cc + "You are either not the creator of this level or it doesn't exist");
        }
    }

    public static void exitCommand(Player player) {
        permission.playerRemove(player, perms);
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        World world = player.getWorld();
        player.teleport(Bukkit.getWorld("world").getSpawnLocation());
        if (world.getName() != "world") {
            lvlman.unloadWorld(world);
        }
        if (!worldNameLegit(world.getName())) {
            lvlman.deleteWorld(world);
        }
    }

    public static boolean worldNameLegit(String name) {
        return !name.contains("@");
    }

    public boolean isPlayerEditing(Player player) {
        return (player.getWorld().getName() != "world");
    }

    public void log(String message) {
        getLogger().info(cc + message);
    }

    public static Database getDB() {
        return db;
    }
}
