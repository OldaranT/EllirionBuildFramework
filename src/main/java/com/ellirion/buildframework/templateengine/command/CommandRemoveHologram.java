package com.ellirion.buildframework.templateengine.command;

import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.PacketPlayOutBlockChange;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.TemplateHologram;

public class CommandRemoveHologram implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;

        TemplateHologram hologram = TemplateManager.getSelectedHolograms().get(player);
        if (hologram == null) {
            player.sendMessage(ChatColor.DARK_RED + "You have no hologram currently selected");
            return false;
        }

        //remove the hologram
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        BoundingBox box = hologram.getBox();
        Location location = hologram.getLocation();
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

        return true;
    }
}
