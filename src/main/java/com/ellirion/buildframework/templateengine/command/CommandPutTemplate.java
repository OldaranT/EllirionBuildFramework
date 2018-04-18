package com.ellirion.buildframework.templateengine.command;

import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.Template;
import net.md_5.bungee.api.ChatColor;
//import net.minecraft.server.v1_12_R1.TileEntity;
//import net.minecraft.server.v1_12_R1.BlockPosition;
//import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
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

            putTempalteInWorld(t, player.getLocation(), player);
            return true;
        }
        return false;
    }

    private void putTempalteInWorld(Template template, Location loc, Player player) {
        int xDepth = template.getTemplateBlocks().length;
        int yDepth = template.getTemplateBlocks()[0].length;
        int zDepth = template.getTemplateBlocks()[0][0].length;
        CraftWorld w = (CraftWorld) loc.getWorld();

        for (int x = 0; x < xDepth; x++) {
            for (int y = 0; y < yDepth; y++) {
                for (int z = 0; z < zDepth; z++) {
                    int locX = (int) loc.getX() + x;
                    int locY = (int) loc.getY() + y;
                    int locZ = (int) loc.getZ() + z;
                    Block b = w.getBlockAt(locX, locY, locZ);
                    BlockState copiedState = template.getTemplateBlocks()[x][y][z].getMetadata();
                    b.setType(template.getTemplateBlocks()[x][y][z].getMaterial());
                    b.getState().update();
                    BlockState blockState = b.getState();
                    blockState.setData(copiedState.getData());
                    blockState.update();

//                    TileEntity te = w.getHandle().getTileEntity(new BlockPosition(locX, locY, locZ));
//                    if (te != null) {
//                        NBTTagCompound ntc = template.getTemplateBlocks()[x][y][z].getData();
//                        te.load(ntc.g());
//                        te.update();
//                    }
                }
            }
        }


    }

}
