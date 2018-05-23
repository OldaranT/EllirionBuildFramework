package com.ellirion.buildframework.templateengine.util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabCompletionFileNameList implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        List<String> completions = new ArrayList<>();
        List<String> fileNames = FileUtil.getListOfNBTFileNames();

        for (String s : fileNames) {
            if (s.startsWith(args[args.length - 1].toUpperCase())) {
                completions.add(s);
            }
        }
        if (completions.isEmpty()) {
            completions = fileNames;
        }
        return completions;
    }
}
