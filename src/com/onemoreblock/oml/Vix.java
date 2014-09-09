package com.onemoreblock.oml;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by epowerj on 9/8/14.
 */
public class Vix extends JavaPlugin {
    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("create")) {
            sender.sendMessage("Test");
        }
        return false;
    }

    public void log(String message) {
        getLogger().info(message);
    }
}
