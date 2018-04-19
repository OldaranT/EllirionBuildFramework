package com.ellirion.buildframework.templateengine.command;


import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.Template;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRemoveMarker implements CommandExecutor {
    /**
     * Enum of markers.
     */
    private Template.Markers marker;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            Template t = TemplateManager.selectedTemplates.get(player);
            if (t == null) {
                player.sendMessage(ChatColor.DARK_RED + "You have no template currently selected");
                return false;
            }

            String markers = "";
            for (Template.Markers m : Template.Markers.values()) {
                markers += ChatColor.RESET;
                markers += ChatColor.BOLD + m.name().toLowerCase();
                if (m != Template.Markers.values()[Template.Markers.values().length - 1]) {
                    markers += ChatColor.RESET + ", ";
                }
            }

            //check if a name was entered
            if (strings.length == 0) {
                player.sendMessage(ChatColor.DARK_RED + "Select one of the following markers: " + markers);
                return false;
            }
            if (strings.length > 1) {
                player.sendMessage(ChatColor.DARK_RED + "Select one of the following markers: " + markers);
                return false;
            }

            try {
                marker = Template.Markers.valueOf(strings[0].toUpperCase());
            } catch (IllegalArgumentException ex) {
                player.sendMessage(ChatColor.DARK_RED + "Select one of the following markers: " + markers);
                return false;
            }

            try {
                switch (marker) {
                    case DOOR:
                        t.removeMarker(Template.Markers.DOOR.toString());
                        player.sendMessage(ChatColor.GREEN + "The following marker has been added: " + Template.Markers.DOOR.toString());
                        break;
                    case GROUND:
                        t.removeMarker(Template.Markers.DOOR.toString());
                        player.sendMessage(ChatColor.GREEN + "The following marker has been added: " + Template.Markers.GROUND.toString());
                        break;
                    case PATH:
                        t.removeMarker(Template.Markers.DOOR.toString());
                        player.sendMessage(ChatColor.GREEN + "The following marker has been added: " + Template.Markers.PATH.toString());
                        break;
                    default:
                        break;
                }
            } catch (NullPointerException ex) {
                player.sendMessage(ex.getMessage());
                return false;
            }
            return true;
        }
        return false;
    }
}
