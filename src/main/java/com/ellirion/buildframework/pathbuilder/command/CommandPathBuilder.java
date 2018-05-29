package com.ellirion.buildframework.pathbuilder.command;

import com.google.common.base.Stopwatch;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.pathbuilder.BuilderManager;
import com.ellirion.buildframework.pathbuilder.model.PathBuilder;
import com.ellirion.buildframework.pathbuilder.util.BresenhamLine3D;
import com.ellirion.buildframework.pathfinder.PathingManager;
import com.ellirion.buildframework.util.StringHelper;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandPathBuilder implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;

        if (strings.length < 1) {
            player.sendMessage(ChatColor.DARK_RED +
                               "Please enter an action\nSupported actions are: create, setname, setradius, addblock, save, load, info");
            return true;
        }

        switch (strings[0].toUpperCase()) {
            case "CREATE":
                create(player, strings);
                break;
            case "SETNAME":
                setName(player, strings);
                break;
            case "SETRADIUS":
                setRadius(player, strings);
                break;
            case "ADDBLOCK":
                addBlock(player, strings);
                break;
            case "ADDSTEP":
                addStep(player, strings);
                break;
            case "REMOVEBLOCK":
                removeBlock(player, strings);
                break;
            case "SAVE":
                save(player, strings);
                break;
            case "LOAD":
                load(player, strings);
                break;
            case "INFO":
                info(player);
                break;
            case "ANCHOR":
                anchor(player);
                break;
            case "CREATEFROMPATHFINDER":
                createPathFromPathFinder(player, strings);
                break;
            case "UNDO":
                BuilderManager.undo(player);
                break;
            case "REDO":
                BuilderManager.redo(player);
                break;
            default:
                player.sendMessage(ChatColor.DARK_RED +
                                   "Please enter an action\nSupported actions are: create, setname, setradius, addblock, save, load");
                return true;
        }

        return true;
    }

    // Create a PathBuilder with a given name
    // Params: [string]
    private void create(Player player, String[] strings) {
        if (strings.length < 2) {
            player.sendMessage(ChatColor.DARK_RED + "Please enter a name");
            return;
        }
        String[] nameStrings = Arrays.copyOfRange(strings, 1, strings.length);
        String name = String.join(" ", nameStrings);
        PathBuilder builder = new PathBuilder(name);
        builder.setRadius(3);
        BuilderManager.getBuilderSessions().put(player, builder);
        player.sendMessage("The path builder was successfully created");
    }

    // Set the name of the selected PathBuilder to the given name
    // Params: [string]
    private void setName(Player player, String[] strings) {
        if (strings.length < 2) {
            player.sendMessage(ChatColor.DARK_RED + "Please enter a name");
            return;
        }
        String[] nameStrings = Arrays.copyOfRange(strings, 1, strings.length);
        String name = String.join(" ", nameStrings);
        PathBuilder builder = BuilderManager.getBuilderSessions().get(player);
        builder.setName(name);
        player.sendMessage("The path builder was renamed to " + name);
    }

    // Set the radius of the selected PathBuilder
    // Params: <int>
    private void setRadius(Player player, String[] strings) {
        try {
            PathBuilder builder = BuilderManager.getBuilderSessions().get(player);
            int radius = Integer.parseInt(strings[1]);
            builder.setRadius(radius);
            player.sendMessage(
                    "The radius of the path in path builder " + builder.getName() + " was set to " + radius);
        } catch (Exception e) {
            player.sendMessage(ChatColor.DARK_RED + "The given radius was invalid");
        }
    }

    // Add a block to the selected PathBuilder
    // Params: <Material> <weight> <metadata as int>
    private void addBlock(Player player, String[] strings) {
        PathBuilder builder = BuilderManager.getBuilderSessions().get(player);
        if (builder == null) {
            player.sendMessage(ChatColor.DARK_RED + "You have no path builder selected");
            return;
        }
        if (strings.length < 4) {
            player.sendMessage(
                    ChatColor.DARK_RED + "Command usage: /pathbuilder addblock <material> <weight> <metadata>");
            return;
        }

        try {
            Material mat = Material.matchMaterial(strings[1]);
            double weight = Double.parseDouble(strings[2]);
            byte data = (byte) Integer.parseInt(strings[3]);
            builder.addBlock(mat, weight, data);
            player.sendMessage(
                    "Block " + mat + ":" + data + " added to path builder " + builder.getName() + " with weight " +
                    weight);
        } catch (Exception e) {
            player.sendMessage(
                    ChatColor.DARK_RED +
                    "something went wrong when trying to add this block. Did you make sure you entered everything correctly?");
            return;
        }
    }

    // Add a step block to the selected PathBuilder
    // Params: <Material> <weight> <metadata as int>
    private void addStep(Player player, String[] strings) {
        PathBuilder builder = BuilderManager.getBuilderSessions().get(player);
        if (builder == null) {
            player.sendMessage(ChatColor.DARK_RED + "You have no path builder selected");
            return;
        }
        if (strings.length < 4) {
            player.sendMessage(
                    ChatColor.DARK_RED + "Command usage: /pathbuilder addstep <material> <weight> <metadata>");
            return;
        }

        try {
            Material mat = Material.matchMaterial(strings[1]);
            double weight = Double.parseDouble(strings[2]);
            byte data = (byte) Integer.parseInt(strings[3]);
            builder.addStep(mat, weight, data);
            player.sendMessage(
                    "Step " + mat + ":" + data + " added to path builder " + builder.getName() + " with weight " +
                    weight);
        } catch (Exception e) {
            player.sendMessage(
                    ChatColor.DARK_RED +
                    "Something went wrong when trying to add this step. Did you make sure you entered everything correctly?");
            return;
        }
    }

    // Remove a block from the selected PathBuilder
    // Params: <Material> <metadata as int>
    private void removeBlock(Player player, String[] strings) {
        PathBuilder builder = BuilderManager.getBuilderSessions().get(player);
        if (builder == null) {
            player.sendMessage(ChatColor.DARK_RED + "You have no path builder selected");
            return;
        }

        if (strings.length < 3) {
            player.sendMessage(ChatColor.DARK_RED + "Command usage: /pathbuilder removeblock <material> <metadata>");
            return;
        }

        try {
            Material mat = Material.valueOf(strings[1]);
            byte data = (byte) Integer.parseInt(strings[2]);
            builder.removeBlock(mat, data);
            player.sendMessage("Block " + mat + ":" + data + " removed from path builder " + builder.getName());
        } catch (Exception e) {
            player.sendMessage(ChatColor.DARK_RED + "Command usage: /pathbuilder removeblock <material> <metadata>");
            return;
        }
    }

    // Save the selected PathBuilder to a file
    // Params: none
    private void save(Player player, String[] strings) {
        PathBuilder builder = BuilderManager.getBuilderSessions().get(player);
        //filter out illegal characters
        String name = builder.getName();
        if (StringHelper.invalidFileName(name)) {
            player.sendMessage(ChatColor.DARK_RED + "The name of the path builder is invalid");
            return;
        }

        String path = BuildFramework.getInstance().getConfig().getString("PathBuilder.pathbuilderPath") + name + ".nbt";
        if (PathBuilder.save(builder, path)) {
            player.sendMessage(ChatColor.GREEN + "The path builder was succesfully saved");
        } else {
            player.sendMessage(
                    ChatColor.DARK_RED + "Something went wrong when trying to save the path builder");
        }
    }

    // Load a PathBuilder from a file
    // Params: [string]
    private void load(Player player, String[] strings) {
        String[] nameStrings = Arrays.copyOfRange(strings, 1, strings.length);
        String name = String.join(" ", nameStrings);
        if (StringHelper.invalidFileName(name)) {
            player.sendMessage(ChatColor.DARK_RED + "That filename was invalid");
            return;
        }

        String path = BuildFramework.getInstance().getConfig().getString("PathBuilder.pathbuilderPath") + name + ".nbt";
        PathBuilder builder = PathBuilder.load(path);
        if (builder == null) {
            player.sendMessage(ChatColor.DARK_RED + "This path builder could not be loaded");
            return;
        }

        BuilderManager.getBuilderSessions().put(player, builder);
        player.sendMessage(ChatColor.GREEN + "The path builder " + name + " was successfully loaded");
    }

    // Show info about the selected PathBuilder
    // Params: none
    private void info(Player player) {
        PathBuilder builder = BuilderManager.getBuilderSessions().get(player);

        player.sendMessage(builder.toString());
    }

    // Use the anchorPoints method to find an anchor point from the player's location (for debugging purposes)
    // Params: none
    private void anchor(Player player) {
        PathBuilder builder = BuilderManager.getBuilderSessions().get(player);
        Location l = player.getLocation();
        Point start = new Point(l.getBlockX(), l.getBlockY(), l.getBlockZ());
        List<Point> anchor = builder.getAnchorPoints(start, player.getWorld(), player);
        player.sendBlockChange(start.toLocation(player.getWorld()), Material.GLOWSTONE, (byte) 0);
        player.sendBlockChange(anchor.remove(0).toLocation(player.getWorld()), Material.GLOWSTONE, (byte) 0);

        for (Point p : anchor) {
            BresenhamLine3D.drawLine(start, p, l.getWorld(), builder.getSupportType());
        }
    }

    // Build a path along the path from the path finder
    // Params: none
    private void createPathFromPathFinder(Player player, String[] args) {
        // Get path from PathingManager
        PathBuilder builder = BuilderManager.getBuilderSessions().get(player);
        List<Point> path = PathingManager.getSession(player).getPath();

        Stopwatch watch = Stopwatch.createStarted();
        BuilderManager.placePath(player, builder.build(path, player.getWorld()));
        watch.stop();
        BuildFramework.getInstance().getLogger().info("Generating and building path took " + watch.elapsed(
                TimeUnit.MILLISECONDS) + "ms");
    }
}
