package com.ellirion.buildframework.templateengine.command;

import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.Template;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPutTemplate implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            Template t = TemplateManager.selectedTemplates.get(player);
            if (t == null) {
                player.sendMessage(ChatColor.DARK_RED + "You have no template currently selected");
                return false;
            }

            putTempalteInWorld(t, player.getLocation());
            return true;
        }
        return false;
    }

    private void putTempalteInWorld(Template template, Location loc) {
        int xDepth = template.getTemplateBlocks().length;
        int yDepth = template.getTemplateBlocks()[0].length;
        int zDepth = template.getTemplateBlocks()[0][0].length;
        World w = loc.getWorld();

        for (int x = 0; x < xDepth; x++) {
            for (int y = 0; y < yDepth; y++) {
                for (int z = 0; z < zDepth; z++) {
                    Block b = w.getBlockAt((int) loc.getX() + x, (int) loc.getY() + y, (int) loc.getZ() + z);
                    b.setType(template.getTemplateBlocks()[x][y][z].getBlock().getType());
                    b.getState().setData(template.getTemplateBlocks()[x][y][z].getMetadata().getData());
                }
            }
        }

    }

}
