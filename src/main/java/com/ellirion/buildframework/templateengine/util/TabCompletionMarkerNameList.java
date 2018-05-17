package com.ellirion.buildframework.templateengine.util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import com.ellirion.buildframework.BuildFramework;

import java.util.ArrayList;
import java.util.List;

public class TabCompletionMarkerNameList implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        FileConfiguration templateFormatConfig = BuildFramework.getInstance().getTemplateFormatConfig();

        if (args.length == 1) {
            List<String> races = templateFormatConfig.getStringList("Markers");
            for (String s : races) {
                if (s.startsWith(args[args.length - 1].toUpperCase())) {
                    completions.add(s);
                }
            }
            if (completions.isEmpty()) {
                completions = races;
            }
            return completions;
        }

        return completions;
    }
}
