package com.ellirion.buildframework.templateengine.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.model.TemplateHologram;

public class CommandCreateTemplateHologram implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;

        TemplateHologram prevHologram = TemplateManager.getSelectedHolograms().get(player);
        if (prevHologram != null) {
            prevHologram.remove(player);
        }

        Template t = TemplateManager.getSelectedTemplates().get(player);
        if (t == null) {
            player.sendMessage(ChatColor.DARK_RED + "You have no template currently selected");
            return true;
        }

        TemplateHologram hologram = new TemplateHologram(t, new Location(player.getWorld(),
                                                                         player.getLocation().getBlockX(),
                                                                         player.getLocation().getBlockY(),
                                                                         player.getLocation().getBlockZ()));
        TemplateManager.getSelectedHolograms().put(player, hologram);

        hologram.create(player);

        player.sendMessage(ChatColor.GREEN + "Template hologram successfully created");

        return true;
    }
}
