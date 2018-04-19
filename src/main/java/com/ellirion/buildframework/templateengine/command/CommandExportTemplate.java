package com.ellirion.buildframework.templateengine.command;

import com.ellirion.buildframework.util.JsonWriter;
import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.model.Template;
import com.google.gson.Gson;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandExportTemplate implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            Template t = TemplateManager.selectedTemplates.get(player);

            if (t == null) {
                player.sendMessage(ChatColor.DARK_RED + "You have no template currently selected");
                return false;
            }

            Gson templateJson = new Gson();
            String json = templateJson.toJson(t);

            if (JsonWriter.writeJsonToFile(json, t.getTemplateName())) {
                player.sendMessage(ChatColor.GREEN + "Template has been successfully exported.");
                return true;
            } else {
                player.sendMessage(ChatColor.DARK_RED + "Template failed to export.");
                return false;
            }
        }
        return false;
    }
}
