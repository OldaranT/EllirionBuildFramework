package com.ellirion.buildframework.pathfinder.model;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.model.Point;

import java.util.List;

public class PathingSession {

    @Getter private Player player;
    @Getter private List<Point> path;
    @Getter @Setter private List<Point> visited;
    @Getter @Setter private PathingGraph graph;
    @Getter @Setter private Point point1;
    @Getter @Setter private Point point2;
    @Getter @Setter private NBTTagCompound config;

    /**
     * Construct a new PathingSession for the Player {@code player}.
     * @param player The Player that this PathingSession is for
     */
    public PathingSession(final Player player) {
        this.player = player;
        this.path = null;
        this.visited = null;
        this.graph = null;
        this.point1 = null;
        this.point2 = null;
        this.config = new NBTTagCompound();

        config.setDouble("v-step", 1);
        config.setDouble("v-grounded", 0.5);
        config.setDouble("v-flying", 2.5);
        config.setDouble("v-exp", 1.3);

        config.setDouble("g-horiz", 1.0);
        config.setDouble("g-vert", Math.sqrt(2) - 1);

        config.setDouble("f-goal-fac", 2);
        config.setDouble("f-goal-exp", 1.1);

        config.setInt("turn-short-threshold", 1);
        config.setInt("turn-short-length", 3);

        config.setInt("turn-long-threshold", 2);
        config.setInt("turn-long-length", 7);

        config.setInt("path-width", 3);
        config.setInt("path-height", 4);
        config.setInt("path-length", 2);

        config.setBoolean("visual-enable", false);
        config.setInt("visual-throttle", 1000);
    }

    /**
     * Sets the last path of this player.
     * @param path The path
     */
    public void setPath(List<Point> path) {
        World world = player.getWorld();

        // If the Player is already viewing a path, remove the hologram.
        if (this.path != null) {
            for (Point point : this.path) {
                Location location = point.toLocation(world);
                Block block = world.getBlockAt(location);
                player.sendBlockChange(location, block.getType(), block.getData());
            }
        }

        // Save the new path
        this.path = path;
        if (path == null) {
            return;
        }

        // Show the new path
        for (Point point : path) {
            Location l = point.toLocation(world);
            player.sendBlockChange(l, Material.CONCRETE,
                                   world.getBlockAt(l).getType().isSolid() ? (byte) 1 : (byte) 5);
        }
    }
}
