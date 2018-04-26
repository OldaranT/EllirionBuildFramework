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

public class CommandRemoveMarker implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You need to be a player to use this command.");
            return true;
        }

        Player player = (Player) commandSender;

        TemplateSession ts = TemplateManager.getSelectedTemplateSession().get(player);
        String markers = Template.markersToString();

        if (ts == null) {
            player.sendMessage(ChatColor.DARK_RED + "You have no template currently selected");
            return true;
        }

        if (!Template.getFinalMarkerList().contains(strings[0].toUpperCase())) {
            player.sendMessage(ChatColor.DARK_RED + "Select one of the following markers: " + markers);
            return true;
        }

        String marker = strings[0];

        Point templateWorldPoint = TemplateManager.getSelectedTemplateSession().get(player).getPoint();

        if (templateWorldPoint == null) {
            player.sendMessage(ChatColor.DARK_RED + "You can only remove markers on creations of a template");
            return true;
        }

        ts.removeMarkersHologram(player);

        if (ts.getTemplate().removeMarker(marker)) {
            player.sendMessage(ChatColor.GREEN + "The following marker has been removed: " + marker);
            ts.placeMarkersHologram(player);
            return true;
        }

        ts.placeMarkersHologram(player);
        player.sendMessage(
                ChatColor.DARK_RED + "The following marker " + marker + " has not been created yet.");
        return true;
    }
}
