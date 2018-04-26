package com.ellirion.buildframework.templateengine.command;

import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.model.TemplateSession;
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
        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = (Player) commandSender;

        TemplateSession ts = TemplateManager.getPointOfTemplate().get(player);

        if (ts == null) {
            player.sendMessage(ChatColor.DARK_RED + "You have no template currently selected");
            return false;
        }

        String path = BuildFramework.getInstance().getConfig().getString("templatePath") +
                      ts.getTemplate().getTemplateName() + ".nbt";

        File theDir = new File(BuildFramework.getInstance().getConfig().getString("templatePath"));

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            try {
                theDir.mkdir();
            } catch (SecurityException se) {
                //handle it
            }
        }

        NBTTagCompound ntc = Template.toNBT(ts.getTemplate());
        try {
            OutputStream out = new FileOutputStream(new File(path));
            NBTCompressedStreamTools.a(ntc, out);

            player.sendMessage(ChatColor.GREEN + "Your template has been successfully been exported");
            return true;
        } catch (Exception e) {
            BuildFramework.getInstance().getLogger().info(e.getMessage());
            player.sendMessage(ChatColor.DARK_RED + "Something went wrong when trying to save the template");
        }
        return false;
    }
}
