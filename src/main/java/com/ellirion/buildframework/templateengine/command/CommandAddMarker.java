package com.ellirion.buildframework.templateengine.command;

import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.model.TemplateSession;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAddMarker implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You need to be a player to use this command.");
            return true;
        }

        Player player = (Player) commandSender;

        double playerX = player.getLocation().getX();
        double playerY = player.getLocation().getY();
        double playerZ = player.getLocation().getZ();

        TemplateSession ts = TemplateManager.getSelectedTemplateSession().get(player);
        String markers = Template.markersToString();

        if (ts == null) {
            player.sendMessage(ChatColor.DARK_RED + "You have no template currently selected");
            return true;
        }

        if (strings.length == 0 || strings.length > 1) {
            player.sendMessage(ChatColor.DARK_RED + "Select one of the following markers: " + markers);
            return true;
        }

        if (!Template.getFinalMarkerList().contains(strings[0].toUpperCase())) {
            player.sendMessage(ChatColor.DARK_RED + "Select one of the following markers: " + markers);
            return true;
        }

        String marker = strings[0];

        Point markerPoint = new Point(playerX, playerY - 1, playerZ);
        Point templateWorldPoint = TemplateManager.getSelectedTemplateSession().get(player).getPoint();

        if (templateWorldPoint == null) {
            player.sendMessage(ChatColor.DARK_RED + "You can only add markers on creations of a template");
            return true;
        }

        ts.removeMarkersHologram(player);

        if (!ts.getTemplate().addMarker(marker, markerPoint, templateWorldPoint)) {
            player.sendMessage(ChatColor.DARK_RED + "This position is not within the template selection");
            ts.placeMarkersHologram(player);
            return true;
        }

        ts.placeMarkersHologram(player);

        player.sendMessage(
                ChatColor.GREEN + "The following marker has been added: " + marker);
        return true;
    }
}

