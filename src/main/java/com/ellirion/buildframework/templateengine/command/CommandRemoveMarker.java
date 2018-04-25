package com.ellirion.buildframework.templateengine.command;

import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.model.TemplateSession;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRemoveMarker implements CommandExecutor {

    /**
     * Enum of markers.
     */
    @Getter @Setter private Template.Markers marker;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            TemplateSession ts = TemplateManager.getPointOfTemplate().get(player);
            String markers = Template.markersToString();

            if (ts == null) {
                player.sendMessage(ChatColor.DARK_RED + "You have no template currently selected");
                return false;
            }

            try {
                marker = Template.Markers.valueOf(strings[0].toUpperCase());
            } catch (IllegalArgumentException ex) {
                player.sendMessage(ChatColor.DARK_RED + "Select one of the following markers: " + markers);
                return false;
            }

            if (ts.getTemplate().removeMarker(marker.toString())) {
                player.sendMessage(ChatColor.GREEN + "The following marker has been removed: " + marker.toString());
                return true;
            }
        }
        return false;
    }
}
