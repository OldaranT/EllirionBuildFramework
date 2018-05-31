package com.ellirion.buildframework.templateengine.command;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.TemplateHologram;
import com.ellirion.buildframework.templateengine.model.TemplateSession;
import com.ellirion.buildframework.templateengine.util.PlayerTemplateGuiSession;

public class CommandPutTemplate implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You need to be a player to use this command.");
            return true;
        }

        Player player = (Player) commandSender;

        TemplateSession ts = TemplateManager.getTemplateSessions().get(player);
        TemplateHologram th = TemplateManager.getSelectedHolograms().get(player);
        if (th == null) {
            player.sendMessage(
                    ChatColor.DARK_RED + "You need to create a hologram first before you can put the template.");
            return true;
        }

        ts.getTemplate().putTemplateInWorld(th.getLocation());
        PlayerTemplateGuiSession.quitSession(th, player);
        return true;
    }
}
