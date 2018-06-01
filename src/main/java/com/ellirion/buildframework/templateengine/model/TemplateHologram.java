package com.ellirion.buildframework.templateengine.model;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.PacketPlayOutBlockChange;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
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

    private static final BlockFace[] FACES = {
            BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN
    };
    private static final float lookAngle = 45.0f;
    @Getter private Location location;
    @Setter @Getter private Template template;
    @Getter private BoundingBox box;
    @Getter private List<TemplateHologramBlock> hologramBlocks;

    /**
     * Constructor taking in a Template.
     * @param t The Template this is a hologram of
     * @param loc the location of the template
     */
    public TemplateHologram(final Template t, final Location loc) {
        location = loc.clone();
        location.setX(loc.getBlockX());
        location.setY(loc.getBlockY());
        location.setZ(loc.getBlockZ());
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
        BoundingBox box = template.getBoundingBox();
        World w = location.getWorld();

        int[] coordinates = CommandHelper.getCoordinates(box, location);

        for (int x = coordinates[0]; x <= coordinates[1]; x++) {
            for (int y = coordinates[2]; y <= coordinates[3]; y++) {
                for (int z = coordinates[4]; z <= coordinates[5]; z++) {

                    connection.sendPacket(new PacketPlayOutBlockChange(
                            ((CraftWorld) w).getHandle(),
                            new BlockPosition(x, y, z)));
                }
            }
        }
    }

    /**
     * Update the hologram location.
     * @param amount of how many blocks need to be moved.
     * @param blockFace facing of the player.
     */
    public void moveHologram(int amount, BlockFace blockFace) {

        hologramBlocks.clear();
        switch (blockFace) {
            case UP:
                this.location.setY((double) (location.getBlockY() + amount));
                break;
            case DOWN:
                this.location.setY((double) (location.getBlockY() - amount));
                break;
            case EAST:
                this.location.setX((double) (location.getBlockX() + amount));
                break;
            case WEST:
                this.location.setX((double) (location.getBlockX() - amount));
                break;
            case NORTH:
                this.location.setZ((double) (location.getBlockZ() - amount));
                break;
            case SOUTH:
                this.location.setZ((double) (location.getBlockZ() + amount));
                break;
            default:
                break;
        }
        fillHologramBlocks();
    }

    /**
     * Set location.
     * @param l the new location
     */
    public void setLocation(Location l) {
        hologramBlocks.clear();
        location = l;
        fillHologramBlocks();
    }

    /**
     * Check the facing of the player.
     * @param yaw of the player.
     * @param pitch of the player.
     * @return direction of the player.
     */
    public BlockFace rotationToFace(float yaw, float pitch) {

        if (pitch <= -lookAngle) {
            return FACES[4];
        } else if (pitch >= lookAngle) {
            return FACES[5];
        }

        return FACES[Math.round(yaw / 90f) & 0x3].getOppositeFace();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TemplateHologram)) {
            return false;
        }
        TemplateHologram other = (TemplateHologram) obj;

        if (!location.equals(other.location)) {
            return false;
        }

        if (!template.equals(other.template)) {
            return false;
        }

        if (!box.equals(other.box)) {
            return false;
        }

        if (!hologramBlocks.equals(other.hologramBlocks)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
