package com.ellirion.buildframework.templateengine.command;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.NBTCompressedStreamTools;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.model.TemplateSession;
import com.ellirion.buildframework.templateengine.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class CommandImportTemplate implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("You need to be a player to use this command.");
            return true;
        }
        Player player = (Player) commandSender;

        String templateName = String.join(" ", strings);

        List<String> fileNames = FileUtil.getListOfNBTFileNames();

        if (!fileNames.contains(templateName.toUpperCase())) {
            player.sendMessage(ChatColor.DARK_RED + "This file does not exist.");
            return true;
        }
        // Load template
        String path = BuildFramework.getInstance().getConfig().getString("TemplateEngine.Path") + templateName + ".nbt";
        NBTTagCompound ntc;
        try {
            ntc = NBTCompressedStreamTools.a(new FileInputStream(new File(path)));
        } catch (Exception e) {
            player.sendMessage(ChatColor.DARK_RED + "Something went wrong while loading the file");
            return true;
        }

        TemplateSession ts = new TemplateSession(Template.fromNBT(ntc), null);

        // Update templatemanager
        TemplateManager.getTemplateSessions().put(player, ts);

        //tell player what happened
        player.sendMessage(
                ChatColor.GREEN + "The template " + ChatColor.BOLD + templateName + ChatColor.RESET + ChatColor.GREEN +
                " has been loaded");
        return true;
    }
}
