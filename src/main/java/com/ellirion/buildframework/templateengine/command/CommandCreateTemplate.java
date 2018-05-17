package com.ellirion.buildframework.templateengine.command;

import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.model.TemplateSession;
import com.ellirion.buildframework.util.WorldEditHelper;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandCreateTemplate implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You need to be a player to use this command.");
            return true;
        }
        Player player = (Player) commandSender;

        // Check if a name was entered
        if (strings.length < 4 || strings.length > 4) {
            player.sendMessage(ChatColor.DARK_RED +
                               "Please give the template a name with the following arguments: <RACE> <TYPE> <LEVEL> <NAME>");
            return true;
        }

        FileConfiguration templateFormatConfig = BuildFramework.getInstance().getTemplateFormatConfig();

        List<String> raceList = templateFormatConfig.getStringList("Races");
        List<String> typeList = templateFormatConfig.getStringList("Types");
        List<String> levelList = templateFormatConfig.getStringList("Levels");

        if (!checkIfStringIsInList(raceList, strings[0], player, "race") ||
            !checkIfStringIsInList(typeList, strings[1], player, "type") ||
            !checkIfStringIsInList(levelList, strings[2], player, "level")) {
            return true;
        }

        String name = String.join("-", strings);

        name = name.replaceAll("[^a-zA-Z0-9\\-]", "").toUpperCase();

        // Remove existing templates from map
        TemplateManager.getTEMPLATESESSIONS().remove(player);

        Selection sel = WorldEditHelper.getSelection(player);
        if (!(sel instanceof CuboidSelection)) {
            player.sendMessage(ChatColor.DARK_RED + "Invalid Selection!");
            return true;
        }

        Template template = new Template(name, sel);
        Point p1 = new Point(sel.getMinimumPoint().getX(), sel.getMinimumPoint().getY(), sel.getMinimumPoint().getZ());

        TemplateSession templateSession = new TemplateSession(template, p1);

        // Add player to template manager map so the template can be linked to the player
        TemplateManager.getTEMPLATESESSIONS().put(player, templateSession);

        player.sendMessage(
                ChatColor.GREEN + "Template with name " + ChatColor.WHITE + ChatColor.BOLD + name + ChatColor.RESET +
                ChatColor.GREEN + " started");
        player.sendMessage(ChatColor.WHITE + "Add markers before saving your template");

        // Put all Marker values in a string
        String markers = Template.markersToString();

        player.sendMessage("Possible markers are: " + markers);

        return true;
    }

    private boolean checkIfStringIsInList(List<String> list, String toCheck, Player player, String nameOfList) {
        if (!list.contains(toCheck.toUpperCase())) {
            String options = String.join(", ", list);
            player.sendMessage(
                    ChatColor.DARK_RED + "The " + nameOfList +
                    " you defined does not exist please use one of the following:");
            player.sendMessage(ChatColor.BOLD + options);
            return false;
        }
        return true;
    }
}
