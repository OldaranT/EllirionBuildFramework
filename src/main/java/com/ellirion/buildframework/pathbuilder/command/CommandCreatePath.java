package com.ellirion.buildframework.pathbuilder.command;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.pathbuilder.model.PathBuilder;

import java.util.LinkedList;
import java.util.List;

public class CommandCreatePath implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;

        PathBuilder builder = new PathBuilder("builder");
        builder.addBlock(Material.DIRT, 1);
        builder.addBlock(Material.DIRT, 1);
        builder.addBlock(Material.GRAVEL, 1);
        builder.addBlock(Material.COBBLESTONE, 0.4);
        builder.addBlock(Material.STONE, 0.1);
        builder.setFenceType(Material.FENCE);
        builder.setRadius(3);

        Location loc = player.getLocation();

        List<Point> points = new LinkedList<>();
        //        points.add(new Point(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ()));
        for (int i = 0; i < 10; i++) {
            points.add(new Point(loc.getBlockX() + i, loc.getBlockY() - 1, loc.getZ() + i));
        }
        for (int i = 0; i < 10; i++) {
            points.add(new Point(loc.getBlockX() + 10, loc.getBlockY() - 1, loc.getZ() + 10 + i));
        }
        for (int i = 0; i < 10; i++) {
            points.add(new Point(loc.getBlockX() + 10 - i, loc.getBlockY() - 1, loc.getZ() + 20 + i));
        }
        for (int i = 0; i < 20; i++) {
            points.add(new Point(loc.getBlockX(), loc.getBlockY() - 1, loc.getZ() + 30 + i));
        }

        builder.build(points, player.getWorld());

        return true;
    }
}
