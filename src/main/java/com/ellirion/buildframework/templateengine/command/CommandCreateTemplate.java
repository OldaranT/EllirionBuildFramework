package com.ellirion.buildframework.templateengine.command;

import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.model.TemplateSession;
import com.ellirion.buildframework.util.WorldEditHelper;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCreateTemplate implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;

        // Check if a name was entered
        if (strings.length == 0) {
            player.sendMessage(ChatColor.DARK_RED + "Please give the template a name");
            return false;
        }

        String name = String.join(" ", strings);

        // Remove existing templates from map
        TemplateManager.getSelectedTemplates().remove(player);
        TemplateManager.getPointOfTemplate().remove(player);

        Selection sel = WorldEditHelper.getSelection(player);
        if (!(sel instanceof CuboidSelection)) {
            player.sendMessage(ChatColor.DARK_RED + "Invalid Selection!");
            return false;
        }

        Location min = sel.getMinimumPoint();
        Location max = sel.getMaximumPoint();
        BoundingBox box = new BoundingBox(min.getBlockX(), min.getBlockY(), min.getBlockZ(), max.getBlockX(),
                                          max.getBlockY(), max.getBlockZ());
        Template template = new Template(name, box, sel.getWorld());
        Point p1 = new Point(sel.getMinimumPoint().getX(), sel.getMinimumPoint().getY(), sel.getMinimumPoint().getZ());

        TemplateSession templateSession = new TemplateSession(template, p1);

        // Add player to template manager map so the template can be linked to the player
        TemplateManager.getSelectedTemplates().put(player, template);
        TemplateManager.getPointOfTemplate().put(player, templateSession);

        player.sendMessage("Template with name " + name + " started");
        player.sendMessage("Add markers before saving your template");

        // Put all Marker values in a string
        String markers = Template.markersToString();

        player.sendMessage("Possible markers are: " + markers);

        return true;
    }
}
