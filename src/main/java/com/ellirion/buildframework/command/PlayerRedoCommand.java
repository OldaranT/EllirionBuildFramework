package com.ellirion.buildframework.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.util.TransactionManager;
import com.ellirion.buildframework.util.async.Promise;

public class PlayerRedoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You need to be a player to use this command.");
            return true;
        }

        Player player = (Player) commandSender;

        Promise p = TransactionManager.redoLastTransaction(player);

        if (!p.await()) {
            player.sendMessage(ChatColor.DARK_RED + p.getException().toString());
        }
        return true;
    }
}
