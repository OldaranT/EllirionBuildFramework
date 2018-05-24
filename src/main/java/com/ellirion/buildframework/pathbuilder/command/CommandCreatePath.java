package com.ellirion.buildframework.pathbuilder.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.pathbuilder.BuilderManager;
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

        PathBuilder builder = BuilderManager.getBuilderSessions().get(player);

        Location loc = player.getLocation();
        int x = loc.getBlockX(), y = loc.getBlockY() - 1, z = loc.getBlockZ();

        List<Point> points = new LinkedList<>();
        points.add(new Point(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ()));
        for (int i = 0; i < 10; i++) {
            x++;
            z++;
            points.add(new Point(x, y, z));
        }
        for (int i = 0; i < 10; i++) {
            z++;
            points.add(new Point(x, y, z));
        }
        for (int i = 0; i < 10; i++) {
            z++;
            x++;
            points.add(new Point(x, y, z));
        }
        for (int i = 0; i < (20 * builder.getRadius()); i++) {
            x++;
            if (i % builder.getRadius() == 0) {
                y++;
            }
            points.add(new Point(x, y, z));
        }

        BuilderManager.placePath(builder.build(points, player.getWorld()));

        return true;
    }
}
