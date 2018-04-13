package com.ellirion.buildframework.templateengine.command;

import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.Template;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

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

            String name = "";
            for (String s1 : strings) {
                name += s1;
                if (s1 != strings[strings.length - 1]) {
                    name += " ";
                }
            }

            //check if player is in template manager list
            if (TemplateManager.selectedTemplates.get(player) != null) {
                TemplateManager.selectedTemplates.remove(player);
            }

            //get selection
            Selection sel = getSelection(player);
            if (!(sel instanceof CuboidSelection)) {
                player.sendMessage(ChatColor.DARK_RED + "Invalid Selection!");
                return false;
            }

            //create template
            Template template = new Template(strings[0], sel);

            //add player to template manager list
            TemplateManager.selectedTemplates.put(player, template);

            player.sendMessage("Template with name " + name + " started");
            player.sendMessage("Add markers before saving your template");
            String markers = "";
            for (Markers m : CommandCreateTemplate.Markers.values()) {
                markers += "§l" + m.name().toLowerCase();
                if (m != Markers.values()[Markers.values().length - 1]) {
                        markers += "§r, ";
                }
            }
            markers.substring(0, markers.length() - 3);
            player.sendMessage("Possible markers are: " + markers);
        }

        return false;
    }

    private Selection getSelection(Player player) {
        WorldEditPlugin worldEditPlugin = null;
        worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEditPlugin == null) {
            player.sendMessage("Error with region undoing! Error: WorldEdit is null.");
        }
        Selection sel = worldEditPlugin.getSelection(player);

        return sel;
    }
}
