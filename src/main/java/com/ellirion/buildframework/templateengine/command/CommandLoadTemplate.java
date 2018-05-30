package com.ellirion.buildframework.templateengine.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.templateengine.TemplateLoadMenu;

public class CommandLoadTemplate implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command");
            return false;
        }
        Player player = (Player) commandSender;

        new TemplateLoadMenu(BuildFramework.getInstance(), player);

        return true;
    }
}
