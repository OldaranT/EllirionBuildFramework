package com.ellirion.buildframework.templateengine.command;

import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.util.WorldEditHelper;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCreateTemplate implements CommandExecutor {
    private enum Markers { DOOR, GROUND, PATH };

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            //check if a name was entered
            if (strings.length == 0) {
                player.sendMessage(ChatColor.DARK_RED + "Please give the template a name");
                return false;
            }

            StringBuilder sb = new StringBuilder();
            for (String s1 : strings) {
                sb.append(s1);
                if (!s1.equals(strings[strings.length - 1])) {
                    sb.append(' ');
                }
            }
            String name = sb.toString();

            //check if player is in template manager list
            if (TemplateManager.getSelectedTemplates().get(player) != null) {
                TemplateManager.getSelectedTemplates().remove(player);
            }

            //get selection
            Selection sel = WorldEditHelper.getSelection(player);
            if (!(sel instanceof CuboidSelection)) {
                player.sendMessage(ChatColor.DARK_RED + "Invalid Selection!");
                return false;
            }

            //create template
            Template template = new Template(strings[0], sel);

            //add player to template manager list
            TemplateManager.getSelectedTemplates().put(player, template);

            player.sendMessage("Template with name " + name + " started");
            player.sendMessage("Add markers before saving your template");
            StringBuilder sbMarkers = new StringBuilder();
            for (Markers m : CommandCreateTemplate.Markers.values()) {
                sbMarkers.append("§l");
                sbMarkers.append(m.name().toLowerCase());
                if (m != Markers.values()[Markers.values().length - 1]) {
                        sbMarkers.append("§r, ");
                }
            }
            String markers = sbMarkers.toString();
            markers = markers.substring(0, markers.length() - 3);
            player.sendMessage("Possible markers are: " + markers);

            return true;
        }
        return false;
    }
}
