package com.ellirion.buildframework.templateengine.command;

import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.Template;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAddMarker implements CommandExecutor {

    /**
     * Enum of markers.
     */
    @Getter
    @Setter
    private Template.Markers marker;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            double playerX = player.getLocation().getX();
            double blockUnderPlayer = player.getLocation().getY() - 1;
            double playerZ = player.getLocation().getZ();

            Template t = TemplateManager.getSelectedTemplates().get(player);
            String markers = Template.markersToString();

            if (t == null) {
                player.sendMessage(ChatColor.DARK_RED + "You have no template currently selected");
                return false;
            }

            try {
                marker = Template.Markers.valueOf(strings[0].toUpperCase());
            } catch (IllegalArgumentException ex) {
                player.sendMessage(ChatColor.DARK_RED + "Select one of the following markers: " + markers);
                return false;
            }

            Point p = new Point(playerX, blockUnderPlayer, playerZ);
            t.addMarker(Template.Markers.DOOR.toString(), p);
            player.sendMessage(ChatColor.GREEN + "The following marker has been added: " + Template.Markers.DOOR.toString());
            return true;
        }
        return false;
    }
}
