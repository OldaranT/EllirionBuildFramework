package com.ellirion.buildframework.templateengine.model;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.PacketPlayOutBlockChange;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.Point;

import java.util.Map;

public class TemplateSession {

    @Getter @Setter private Template template;
    @Getter @Setter private Point point;

    /**
     * Constructor of TemplateSession.
     * @param template Template of current session.
     * @param point Point of the current template.
     */
    public TemplateSession(final Template template, final Point point) {
        this.template = template;
        this.point = point;
    }

    /**
     * Removes all marker holograms of the current session.
     * @param player current player.
     */
    public void removeMarkersHologram(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        // Place the markers (Color of the markers is set in the config)
        for (Map.Entry pair : template.getMarkers().entrySet()) {
            Point p = (Point) pair.getValue();

            connection.sendPacket(new PacketPlayOutBlockChange(
                    ((CraftWorld) player.getWorld()).getHandle(),
                    new BlockPosition(p.getX() + point.getX(),
                                      p.getY() + point.getY(),
                                      p.getZ() + point.getZ())));
        }
    }

    /**
     * Places all marker holograms of the current session.
     * @param player current player.
     */
    public void placeMarkersHologram(Player player) {

        // Place the markers (Color of the markers is set in the config)
        for (Map.Entry pair : template.getMarkers().entrySet()) {
            Point p = (Point) pair.getValue();
            Location loc = new Location(player.getWorld(),
                                        p.getX() + point.getX(),
                                        p.getY() + point.getY(),
                                        p.getZ() + point.getZ());
            player.sendBlockChange(loc, Material.WOOL,
                                   (byte) BuildFramework.getInstance().getConfig().getInt((String) pair.getKey()));
        }
    }
}
