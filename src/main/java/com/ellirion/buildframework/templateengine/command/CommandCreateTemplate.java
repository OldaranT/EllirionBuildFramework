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
import org.bukkit.entity.Player;

import java.util.List;

public class CommandCreateTemplate implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        Player player = (Player) commandSender;

        // Check if a name was entered
        if (strings.length < 3 || strings.length > 3) {
            player.sendMessage(ChatColor.DARK_RED +
                               "Please give the template a name with the following arguments: <RACE> <TYPE> <LEVEL>");
            return true;
        }

        List<String> raceList = (List<String>) BuildFramework.getInstance().getTemplateFormatConfig().getList("Races");
        List<String> typeList = (List<String>) BuildFramework.getInstance().getTemplateFormatConfig().getList("Types");
        List<String> levelList = (List<String>) BuildFramework.getInstance().getTemplateFormatConfig().getList(
                "Levels");

        boolean argCheck = true;
        if (!raceList.contains(strings[0].toUpperCase())) {
            String raceOptions = String.join(", ", raceList);
            player.sendMessage(
                    ChatColor.DARK_RED + "The race you defined does not exist please use one of the following:");
            player.sendMessage(ChatColor.BOLD + raceOptions);
            argCheck = false;
        }

        if (!typeList.contains(strings[1].toUpperCase())) {
            String typeOptions = String.join(", ", typeList);
            player.sendMessage(
                    ChatColor.DARK_RED + "The type you defined does not exist please use one of the following:");
            player.sendMessage(ChatColor.BOLD + typeOptions);
            argCheck = false;
        }

        if (!levelList.contains(strings[2].toUpperCase())) {
            String levelOptions = String.join(", ", levelList);
            player.sendMessage(
                    ChatColor.DARK_RED + "The level you defined does not exist please use one of the following:");
            player.sendMessage(ChatColor.BOLD + levelOptions);
            argCheck = false;
        }

        if (!argCheck) {
            return true;
        }

        String name = String.join(" ", strings);

        name = name.replaceAll("[^a-zA-Z0-9]", "");

        // Remove existing templates from map
        TemplateManager.getPointOfTemplate().remove(player);

        Selection sel = WorldEditHelper.getSelection(player);
        if (!(sel instanceof CuboidSelection)) {
            player.sendMessage(ChatColor.DARK_RED + "Invalid Selection!");
            return true;
        }

        Template template = new Template(name, sel);
        Point p1 = new Point(sel.getMinimumPoint().getX(), sel.getMinimumPoint().getY(), sel.getMinimumPoint().getZ());

        TemplateSession templateSession = new TemplateSession(template, p1);

        // Add player to template manager map so the template can be linked to the player
        TemplateManager.getPointOfTemplate().put(player, templateSession);

        player.sendMessage("Template with name " + ChatColor.BOLD + name + ChatColor.RESET + " started");
        player.sendMessage("Add markers before saving your template");

        // Put all Marker values in a string
        String markers = Template.markersToString();

        player.sendMessage("Possible markers are: " + markers);

        return true;
    }
}
