package com.ellirion.buildframework.templateengine.command;

import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.TemplateSession;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPutTemplate implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = (Player) commandSender;

        TemplateSession ts = TemplateManager.getPointOfTemplate().get(player);
        if (ts == null) {
            player.sendMessage(ChatColor.DARK_RED + "You have no template currently selected");
            return false;
        }

        TemplateManager.getTemplate(player).putTemplateInWorld(player.getLocation());
        return true;
    }
}
