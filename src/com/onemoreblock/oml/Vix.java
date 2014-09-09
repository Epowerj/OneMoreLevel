package com.onemoreblock.oml;

import net.milkbowl.vault.permission.Permission;
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
    private LevelManager lvlman = new LevelManager();
    private EventManager eventman = new EventManager();
    private Database db = new Database();
    public static Permission permission = null;

    public final String cc = ""; //color code

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(eventman, this);
        setupPermissions();
        db.load();
    }

    @Override
    public void onDisable() {

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

            if (cmd.getName().equalsIgnoreCase("create")) {
                createCommand(player, args[0]);
                return true;
            }
        } else {
            sender.sendMessage(cc + "This command cannot be used from the console");
        }
        return true;
    }

    private void createCommand(Player player, String name) {
        //TODO check if already exists
        player.sendMessage("Creating " + name);
        World world = lvlman.create(name, player);
        player.teleport(world.getSpawnLocation());
        player.setBedSpawnLocation(world.getSpawnLocation());
        permission.playerAdd(player, "bukkit.command.gamemode");
        player.setGameMode(GameMode.CREATIVE);
    }

    public void log(String message) {
        getLogger().info(cc + message);
    }
}
