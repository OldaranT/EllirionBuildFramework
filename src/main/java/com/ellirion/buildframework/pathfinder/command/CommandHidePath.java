package com.ellirion.buildframework.pathfinder.command;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.pathfinder.PathingManager;
import com.ellirion.buildframework.pathfinder.model.PathingSession;

import java.util.List;

public class CommandHidePath implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        PathingSession session = PathingManager.getSession(player);
        session.setPath(null);

        List<Point> visited = session.getVisited();
        World world = player.getWorld();
        if (visited != null) {
            for (Point point : visited) {
                Location location = point.toLocation(world);
                Block block = world.getBlockAt(location);
                player.sendBlockChange(location, block.getType(), block.getData());
            }
        }

        return true;
    }
}
