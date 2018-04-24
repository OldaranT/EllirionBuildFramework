package com.ellirion.buildframework.pathbuilder.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.pathbuilder.model.PathBuilder;

public class CommandCreatePath implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;

        PathBuilder builder = new PathBuilder("builder");
        builder.addBlock(Material.WOOD, 1);
        builder.setFenceType(Material.FENCE);
        builder.setRadius(2);

        Point p1 = new Point(player.getLocation()).floor();

        return true;
    }
}
