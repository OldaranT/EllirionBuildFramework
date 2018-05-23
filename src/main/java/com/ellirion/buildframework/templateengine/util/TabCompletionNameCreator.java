package com.ellirion.buildframework.templateengine.util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import com.ellirion.buildframework.BuildFramework;

import java.util.ArrayList;
import java.util.List;

public class TabCompletionNameCreator implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        FileConfiguration templateFormatConfig = BuildFramework.getInstance().getTemplateFormatConfig();

        if (args.length == 1) {
            List<String> races = templateFormatConfig.getStringList("Races");
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

        if (args.length == 2) {
            List<String> types = templateFormatConfig.getStringList("Types");
            for (String s : types) {
                if (s.startsWith(args[args.length - 1].toUpperCase())) {
                    completions.add(s);
                }
            }
            if (completions.isEmpty()) {
                completions = types;
            }
            return completions;
        }

        if (args.length == 3) {
            List<String> levels = templateFormatConfig.getStringList("Levels");
            for (String s : levels) {
                if (s.startsWith(args[args.length - 1].toUpperCase())) {
                    completions.add(s);
                }
            }
            if (completions.isEmpty()) {
                completions = levels;
            }
            return completions;
        }

        return completions;
    }
}
