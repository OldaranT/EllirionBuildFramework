package com.ellirion.buildframework.templateengine.command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.model.TemplateHologram;
import com.ellirion.buildframework.templateengine.model.TemplateHologramBlock;

public class CommandCreateTemplateHologram implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;

        // Get the selected template
        Template t = TemplateManager.getSelectedTemplates().get(player);
        if (t == null) {
            player.sendMessage(ChatColor.DARK_RED + "You have no template currently selected");
            return false;
        }

        TemplateHologram hologram = new TemplateHologram(t, new Location(player.getLocation().getWorld(),
                                                                         player.getLocation().getBlockX(),
                                                                         player.getLocation().getBlockY(),
                                                                         player.getLocation().getBlockZ()));
        TemplateManager.getSelectedHolograms().put(player, hologram);

        player.sendMessage(ChatColor.GREEN + "Template hologram successfully created");

        BoundingBox box = hologram.getBox();
        Location location = hologram.getLocation();
        int[] coordinates = CommandHelper.getCoordinates(box, location);

        for (int x = coordinates[0]; x <= coordinates[1]; x++) {
            for (int y = coordinates[2]; y <= coordinates[3]; y++) {
                for (int z = coordinates[4]; z <= coordinates[5]; z++) {
                    World w = hologram.getLocation().getWorld();
                    Location loc = new Location(hologram.getLocation().getWorld(), x, y, z);

                    // If the block is not air, change it to a barrier block
                    if (w.getBlockAt(x, y, z).getType() != Material.AIR) {
                        player.sendBlockChange(loc, Material.BARRIER, (byte) 0);
                    }

                    // If the template has a marker here, change it to ???
                    String marker = t.getMarkerOnPoint(new Point(x, y, z));
                    if (marker.equals("")) {
                        player.sendBlockChange(loc, Material.WOOL,
                                               (byte) BuildFramework.getInstance().getConfig().get("markerDoorColour"));
                    }
                }
            }
        }

        for (TemplateHologramBlock block : hologram.getHologramBlocks()) {
            player.sendBlockChange(block.getLoc(), block.getMat(), (byte) 0);
        }

        return true;
    }
}
