package com.ellirion.buildframework.templateengine.command;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAddMarker implements CommandExecutor {

    private enum Markers {DOOR, GROUND, PATH};

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            String markers = "";
            for (Markers m : CommandAddMarker.Markers.values()) {
                markers += "§l" + m.name().toLowerCase();
                if (m != Markers.values()[Markers.values().length - 1]) {
                    markers += "§r, ";
                }
            }

            //check if a name was entered
            if (strings.length == 0) {
                player.sendMessage(ChatColor.DARK_RED + "Select one of the following markers: " + markers);
                return false;
            }
            markers = markers.substring(0, markers.length() - 3);
            player.sendMessage("Possible markers are: " + markers);
            return true;
        }
        return false;
    }
}
