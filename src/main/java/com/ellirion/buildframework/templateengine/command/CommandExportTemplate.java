package com.ellirion.buildframework.templateengine.command;

import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.Template;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class CommandExportTemplate implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            Template t = TemplateManager.getSelectedTemplates().get(player);

            if (t == null) {
                player.sendMessage(ChatColor.DARK_RED + "You have no template currently selected");
                return false;
            }

            String path = "plugins/Ellirion/BuildFramework/templates/" + t.getTemplateName() + ".nbt";

            NBTTagCompound ntc = Template.toNBT(t);
            try {
                OutputStream out = new FileOutputStream(new File(path));
                NBTCompressedStreamTools.a(ntc, out);
            } catch (Exception e) {
                player.sendMessage(ChatColor.DARK_RED + "Something went wrong when trying to save the file");
            }
        }
        return false;
    }
}
