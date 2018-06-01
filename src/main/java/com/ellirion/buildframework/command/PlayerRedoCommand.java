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
        new Promise<>(finisher -> {
            TransactionManager.redoLastTransaction(player).except(ex -> {
                player.sendMessage(ChatColor.DARK_RED + ex.toString());
            });
            finisher.resolve(null);
        }, true);

        return true;
    }
}
