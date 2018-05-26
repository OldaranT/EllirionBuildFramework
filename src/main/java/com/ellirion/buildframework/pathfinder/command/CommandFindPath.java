package com.ellirion.buildframework.pathfinder.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.pathfinder.AStar;
import com.ellirion.buildframework.pathfinder.PathingManager;
import com.ellirion.buildframework.pathfinder.model.PathingSession;

public class CommandFindPath implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        // If the sender is not a player, we can't get their selection.
        if (!(sender instanceof Player)) {
            return false;
        }

        // Get the player and their selection
        Player player = (Player) sender;

        // Get the intended start and goal
        Point start, goal;

        if (args.length > 0) {
            int size = Integer.parseInt(args[0]) - 1;
            start = new Point(player.getLocation()).floor();
            goal = new Point(start.getX() + size, start.getY() + size, start.getZ() + size);
        } else {
            PathingSession session = PathingManager.getSession(player);
            start = session.getPoint1();
            goal = session.getPoint2();
            if (start == null) {
                player.sendMessage(ChatColor.RED + "No first point selected");
                return false;
            }
            if (goal == null) {
                player.sendMessage(ChatColor.RED + "No second point selected");
                return false;
            }
        }

        // Calculate the path
        AStar astar = new AStar(player, start, goal);
        astar.searchAsync().consumeSync((path) -> {
            // Show the new path
            PathingManager.getSession(player).setPath(path);
            PathingManager.getSession(player).setGraph(astar.getGraph());
        }).consumeFailSync((ex) -> {
            player.sendMessage("Heap failed: " + ex.getMessage());
        });

        return true;
    }
}
