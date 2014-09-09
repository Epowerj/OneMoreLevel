package com.onemoreblock.oml;

import net.milkbowl.vault.permission.Permission;
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
    public static Permission permission = null;

    @Override
    public void onEnable() {
        setupPermissions();
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
        if (cmd.getName().equalsIgnoreCase("create")) {
            sender.sendMessage("Creating...");
            Player player = (Player) sender;
            player.teleport(lvlman.create(args[0], (Player) sender).getSpawnLocation());
            return true;
        }
        return false;
    }

    public void log(String message) {
        getLogger().info(message);
    }
}
