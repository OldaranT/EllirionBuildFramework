package com.ellirion.buildframework.templateengine.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.TemplateHologram;

public class CommandRemoveHologram implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You need to be a player to use this command.");
            return true;
        }
        Player player = (Player) commandSender;

        TemplateHologram hologram = TemplateManager.getSELECTEDHOLOGRAMS().get(player);
        if (hologram == null) {
            player.sendMessage(ChatColor.DARK_RED + "No template hologram to remove");
            return true;
        }

        hologram.remove(player);
        TemplateManager.getSELECTEDHOLOGRAMS().remove(player);

        player.sendMessage(ChatColor.GREEN + "Template hologram was successfully removed");

        return true;
    }
}
