package com.ellirion.buildframework.templateengine.model;

import lombok.Getter;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.PacketPlayOutBlockChange;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.templateengine.command.CommandHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TemplateHologram {

    @Getter private Location location;
    private Template template;
    @Getter private BoundingBox box;
    @Getter private List<TemplateHologramBlock> hologramBlocks;

    /**
     * Constructor taking in a Template.
     * @param t The Template this is a hologram of
     * @param loc the location of the template
     */
    public TemplateHologram(final Template t, final Location loc) {
        location = loc.clone();
        template = t;
        box = template.getBoundingBox();
        hologramBlocks = new LinkedList<>();

        fillHologramBlocks();
    }

    private void fillHologramBlocks() {
        int x = (int) location.getX();
        int y = (int) location.getY();
        int z = (int) location.getZ();
        World w = location.getWorld();

        for (Point p : box.getCorners()) {
            hologramBlocks.add(
                    new TemplateHologramBlock(
                            new Location(w, x + p.getX(), y + p.getY(), z + p.getZ()),
                            Material.GLOWSTONE
                    )
            );
        }
    }

    /**
     * Create the hologram for the given player.
     * @param player the player for which to create the hologram
     */
    public void create(Player player) {
        BoundingBox box = getBox();
        Location location = getLocation();
        int[] coordinates = CommandHelper.getCoordinates(box, location);
        World w = getLocation().getWorld();

        for (int x = coordinates[0]; x <= coordinates[1]; x++) {
            for (int y = coordinates[2]; y <= coordinates[3]; y++) {
                for (int z = coordinates[4]; z <= coordinates[5]; z++) {
                    Location loc = new Location(w, x, y, z);

                    // If the block is not air, change it to a barrier block
                    if (w.getBlockAt(x, y, z).getType() != Material.AIR) {
                        player.sendBlockChange(loc, Material.BARRIER, (byte) 0);
                    }
                }
            }
        }

        // Place the markers (Color of the markers is set in the config)
        for (Map.Entry pair : template.getMarkers().entrySet()) {
            Point p = (Point) pair.getValue();
            Location loc = new Location(w,
                                        p.getX() + location.getX(),
                                        p.getY() + location.getY(),
                                        p.getZ() + location.getZ());
            player.sendBlockChange(loc, Material.WOOL,
                                   (byte) BuildFramework.getInstance().getConfig().getInt(
                                           ((String) pair.getKey()).toUpperCase()));
        }

        for (TemplateHologramBlock block : getHologramBlocks()) {
            player.sendBlockChange(block.getLoc(), block.getMat(), (byte) 0);
        }
    }

    /**
     * Remove the hologram for the given player.
     * @param player the player to remove the hologram for
     */
    // To remove the hologram, we simply need to update all blocks where the hologram is
    public void remove(Player player) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        BoundingBox box = getBox();
        Location location = getLocation();
        World w = location.getWorld();

        int[] coordinates = CommandHelper.getCoordinates(box, location);

        for (int x = coordinates[0]; x <= coordinates[1]; x++) {
            for (int y = coordinates[2]; y <= coordinates[3]; y++) {
                for (int z = coordinates[4]; z <= coordinates[5]; z++) {

                    // If the block is not air, change it to a barrier block
                    connection.sendPacket(new PacketPlayOutBlockChange(
                            ((CraftWorld) w).getHandle(),
                            new BlockPosition(x, y, z)));
                }
            }
        }
    }
}
