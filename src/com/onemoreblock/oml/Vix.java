/*
 *__________     _________     _________     ___    ___    ___
/           \   /   ___   \   /   ___   \   /   \  /   \  /   \
|     ______/   |  /   \  |   |  /   \  |   |    | |    | |   |
|     |_        |  \___/  |   |  |   |  |   |    | |    | |   |
|      _|       |   ______/   |  |   |  |   |    |_|    |_|   |
|     |_____    |   |         |  |   |  |   |                 |
|           \   |   |         |  \   /  |   |                 |
\___________/   \___/         \_________/   \_________________/
 */
//Created by 
//Epowerj
//Wowfunhappy

package com.onemoreblock.oml;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Vix extends JavaPlugin {

    static public EventManager eventman;
    static public DatabaseManager dbman;
    static public WorldManager worldman;
    public static Permission perms = null;
    static Plugin plugin;
    static Logger log;
    static Location leave;

    public static WorldManager getWorldManager() {
        return worldman;
    }

    public static DatabaseManager getDBManager() {
        return dbman;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static Logger getLog() {
        return log;
    }

    public static Location getLeave() {
        return leave;
    }

    public static Permission getPerms() {
        return perms;
    }

    public static Boolean isNameValid(String name) {
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

    public static void sendMessage(CommandSender sender, String string) {
        sender.sendMessage(string);
    }

    public static void exit(Player player) {
        // TODO co-op
        Vix.getPerms()
                .playerRemove((String) null, player.getName(), "Vix.edit");
        Vix.getPerms().playerRemove((String) null, player.getName(),
                "voxelsniper.litesniper");
        Inventory invent = player.getInventory();
        invent.clear();
        String defaultworld = "world";
        if (!worldman.areWorldsEqual(player.getWorld().getName(), defaultworld)) {
            if (!isNameValid(player.getWorld().getName())) {
                worldman.deleteWorld(player.getWorld().getName());
            } else {
                worldman.unloadWorld(player.getWorld());
            }
        }
    }

    // ---End Getters---

    @Override
    public void onEnable() {
        log = getLogger();
        worldman = new WorldManager();
        eventman = new EventManager();
        dbman = new DatabaseManager();
        plugin = this;
        getServer().getPluginManager().registerEvents(eventman, this);
        leave = Bukkit.getWorld("world").getSpawnLocation();

        dbman.loadDB();

        if (!setupPermissions()) {
            log.severe(String.format(
                    "[%s] - Disabled due to no Vault dependency found!",
                    getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLog().info("[Vix]Vix enabled!");
    }

    @Override
    public void onDisable() {
        dbman.closeDB();
        getLog().info("[Vix]Vix Disabled!");
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer()
                .getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public boolean isPlayerInHub(Player player) {
        if (player.getWorld().getName().equalsIgnoreCase("world")) {
            return true;
        }
        return false;
    }

    public boolean isPlayerEditing(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
                             String[] args) {
        if (cmd.getName().equalsIgnoreCase("create")) {
            if (args.length == 1) {
                String arg = args[0].toLowerCase();
                if (isNameValid(arg)) {
                    if (dbman.levelExists(arg) != true) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            exit(player);
                            Level level = new Level(arg, player);
                            player.teleport(worldman
                                    .getSpawnLocation(level.name));
                            return true;
                        } else {
                            sendMessage(sender, "Must be ingame to use this!");
                        }
                    } else {
                        sendMessage(sender,
                                "A level with that name already exists!");
                    }
                } else {
                    sendMessage(sender,
                            "Invalid name! Don't use any strange characters.");
                }
            } else {
                sendMessage(sender, "Creates a new level, ready for editing!");
                sendMessage(sender, "�eUsage: �3/create �7<Level Name>");
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("edit")) {
            if (args.length == 1) {
                String arg = args[0].toLowerCase();
                if (isNameValid(arg)) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        exit(player);
                        if (dbman.levelExists(arg)) {
                            if (dbman.isDesigner(arg, player.getName())) {
                                Level level = new Level(arg);
                                player.teleport(level.getSpawnLocation());
                                player.setGameMode(org.bukkit.GameMode.CREATIVE);
                                perms.playerAdd((String) null,
                                        player.getName(), "Vix.edit");
                                perms.playerAdd((String) null,
                                        player.getName(),
                                        "voxelsniper.litesniper");
                                sendMessage(sender, "Now editing �n" + arg
                                        + "�r�a!");
                                return true;
                            } else {
                                sendMessage(sender,
                                        "You don't have edit rights for �n"
                                                + arg + "�r�c!");
                            }
                        } else {
                            sendMessage(sender,
                                    "A level with that name does not exist!");
                        }
                    } else {
                        sender.sendMessage("This command must be used in-game");
                    }
                }
            } else {
                sendMessage(sender, "Edits an existing level.");
                sendMessage(sender, "�eUsage: �3/edit �7<Level Name>");
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("publish")) {
            if (args.length == 1) {
                String arg = args[0].toLowerCase();
                if (isNameValid(arg)) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (dbman.isDesigner(arg, player.getName())) {
                            dbman.publishLevel(arg);
                            sendMessage(sender,
                                    "Your level has been published!");
                        } else {
                            sendMessage(sender,
                                    "You don't have publish rights for �n"
                                            + arg + "�r�c!");
                        }
                    } else {
                        sender.sendMessage("This command must be used in-game");
                    }
                }
                return true;
            } else {
                Player player = (Player) sender; // Assume you guys won't run
                // from console. <3
                if (isPlayerInHub(player)) {
                    sendMessage(sender,
                            "Publishes a level, and allows it to be played.");
                    sendMessage(sender, "�eUsage: �3/publish �7<Level Name>");
                } else {
                    if (isPlayerEditing(player)) {
                        player.chat("/publish "
                                + player.getWorld().getName().toLowerCase());
                    } else {
                        sendMessage(sender,
                                "�cYou must be in edit mode to publish levels!");
                    }
                }
            }
            return true;
        }

        // TODO adddesigner
        if (cmd.getName().equalsIgnoreCase("adddesigner")) {
            if (args[0] != "") {
                String arg = args[0].toLowerCase();
                if (isNameValid(arg)) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        exit(player);
                        if (dbman.levelExists(arg)) {
                            if (dbman.isDesigner(arg, player.getName())) {

                            } else {
                                player.sendMessage("You are not the designer of this level!");
                            }
                        } else {
                            sender.sendMessage("This level does not exist");
                        }
                    } else {
                        sender.sendMessage("This command must be used in-game");
                    }
                }
            } else {
                sender.sendMessage("Usage: /edit <name>");
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("delete")) {
            if (args.length == 1) {
                String arg = args[0].toLowerCase();
                if (dbman.levelExists(arg)) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        exit(player);

                        if (dbman.isDesigner(arg, player.getName())
                                || perms.has(sender, "Vix.admin")) {
                            worldman.deleteWorld(arg);
                            dbman.deleteLevel(arg);
                            sendMessage(sender, "Level deleted!");
                            return true;
                        } else {
                            sendMessage(sender,
                                    "�cYou must be the designer of a level to delete it!");
                        }
                    } else {
                        sendMessage(sender, "This command must be used in-game");
                    }
                } else {
                    sendMessage(sender,
                            "A level with that name does not exist!");
                }
            } else {
                Player player = (Player) sender;
                if (isPlayerInHub(player)) {
                    sendMessage(sender, "Deletes a level/draft.");
                    sendMessage(sender, "�eUsage: �3/delete �7<Level Name>");
                } else {
                    if (isPlayerEditing(player)) {
                        player.chat("/delete "
                                + player.getWorld().getName().toLowerCase());
                    } else {
                        sendMessage(sender,
                                "�cYou must be in edit mode to delete levels!");
                    }
                }
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("play")) {
            if (args.length == 1) {
                String arg = args[0].toLowerCase();
                if (isNameValid(arg)) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        exit(player);
                        if (dbman.levelExists(arg)) {
                            if (dbman.isLevelPublished(arg)
                                    || dbman.isDesigner(arg, player.getName())) {
                                if (dbman.isDesigner(arg, player.getName())
                                        && !dbman.isLevelPublished(arg)) {
                                    sendMessage(sender,
                                            "You are now playtesting your level!");
                                    sendMessage(sender,
                                            "When you're done, you can �e/publish�b your level!");
                                } else {
                                    sendMessage(sender,
                                            "You are now playing �n" + args
                                                    + "�r�b!");
                                    sendMessage(sender,
                                            "You can exit at anytime with �e/leave�b!");
                                }
                                Level level = new Level(arg);
                                World world = level.createClone();
                                player.teleport(world.getSpawnLocation());
                                player.setGameMode(org.bukkit.GameMode.SURVIVAL);
                                return true;
                            } else {
                                sendMessage(sender,
                                        "This level has not been published by the designer!");
                            }
                        } else {
                            sendMessage(sender,
                                    "A level with that name does not exist!");
                        }
                    } else {
                        sender.sendMessage("This command must be used in-game");
                    }
                } else {
                    sendMessage(sender,
                            "Invalid name! Don't use any strange characters.");
                }
            } else {
                sendMessage(sender, "Opens a level in play mode.");
                sendMessage(sender, "�eUsage: �3/play �7<Level Name>");
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("leave")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!isPlayerInHub(player)) {
                    exit(player);
                    player.teleport(leave);
                    sendMessage(sender, "You're now back in the hub!");
                    return true;
                } else {
                    sendMessage(sender, "You're already in the hub!");
                }
            } else {
                sender.sendMessage("This command must be used in-game");
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("setstart")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.getGameMode() == org.bukkit.GameMode.CREATIVE) {
                    Double dx = player.getLocation().getX();
                    Integer x = dx.intValue();
                    Double dy = player.getLocation().getY();
                    Integer y = dy.intValue();
                    Double dz = player.getLocation().getZ();
                    Integer z = dz.intValue();
                    worldman.getWorld("world").setSpawnLocation(x, y, z);
                    sendMessage(sender, "Your start point has been uploaded!");
                    return true;
                } else {
                    sendMessage(sender, "You don't have edit rights for �n"
                            + player.getWorld().getName() + "�r�c!");
                }
            } else {
                sender.sendMessage("This command must be used in-game");
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("setleavepoint")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                leave = player.getLocation();
                return true;
            } else {
                sender.sendMessage("This command must be used in-game");
            }
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("cmdblock")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.getGameMode() == GameMode.CREATIVE) {
                    Inventory invent = player.getInventory();
                    // ItemStack item = new ItemStack(137);
                    invent.addItem(new ItemStack(org.bukkit.Material.COMMAND));
                    return true;
                } else {
                    sender.sendMessage("You must be editing this level to use this command");
                }
            } else {
                sender.sendMessage("This command must be used in-game");
            }
            return true;
        }
        /*
         * if (cmd.getName().equalsIgnoreCase("reset")) { try {
		 * db.executeUpdate("drop table if exists levels"); db.executeUpdate(
		 * "create table levels (name string, designer string, edit boolean)");
		 * } catch (SQLException e) { e.printStackTrace(); } return true; }
		 */
        return false;
    }
}