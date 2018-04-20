package com.ellirion.buildframework.templateengine.command;

import com.ellirion.buildframework.BuildFramework;
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
import java.io.FileInputStream;

public class CommandImportTemplate implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) { return false; }
        Player player = (Player) commandSender;

        String templateName = String.join(" ", strings);

        // Load template
        String path = BuildFramework.getInstance().getConfig().getString("templatePath") + templateName + ".nbt";
        NBTTagCompound ntc;
        try {
            ntc = NBTCompressedStreamTools.a(new FileInputStream(new File(path)));
        } catch (Exception e) {
            player.sendMessage(ChatColor.DARK_RED + "Something went wrong while loading the file.");
            return false;
        }

        Template t = Template.fromNBT(ntc);

        // Update templatemanager
        TemplateManager.getSelectedTemplates().remove(player);
        TemplateManager.getSelectedTemplates().put(player, t);

        //tell player what happened
        player.sendMessage(ChatColor.GREEN + "The template " + ChatColor.BOLD + templateName + ChatColor.RESET + ChatColor.GREEN + " has been loaded");
        return true;
    }
}
