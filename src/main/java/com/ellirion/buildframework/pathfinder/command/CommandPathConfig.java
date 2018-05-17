package com.ellirion.buildframework.pathfinder.command;

import com.ellirion.buildframework.pathfinder.PathingManager;
import com.ellirion.buildframework.pathfinder.model.PathingConfigAccessor;
import net.minecraft.server.v1_12_R1.NBTBase;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagDouble;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommandPathConfig implements CommandExecutor {

    private static final Map<Class<? extends NBTBase>, PathingConfigAccessor> accessors;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        // Sender must be a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to do this.");
            return true;
        }
        Player player = (Player) sender;

        // Check usage
        if (args.length > 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /pc <name> [value]");
            return true;
        }

        // Get the config
        NBTTagCompound config = PathingManager.getSession(player).getConfig();
        NBTBase base;
        PathingConfigAccessor accessor;

        // List all variables if asked
        if (args.length == 0) {
            Set<String> keys = config.c();
            for (String key : keys) {
                base = config.get(key);
                accessor = accessors.get(base.getClass());
                player.sendMessage(accessor.get(base, key));
            }
            return true;
        }

        // Get the tag
        base = config.get(args[0]);

        // Handle non-existant options
        if (base == null) {
            player.sendMessage(ChatColor.RED + "No such option: " + ChatColor.RESET + args[0]);
            return true;
        }

        // Get accessor
        accessor = accessors.get(base.getClass());

        // Use accessor to modify variable
        if (args.length == 1) {
            player.sendMessage(accessor.get(base, args[0]));
        } else {
            player.sendMessage(accessor.set(config, base, args[0], args[1]));
        }

        return true;
    }

    static {
        accessors = new HashMap<>();
        accessors.put(NBTTagDouble.class, new PathingConfigAccessor() {
            @Override
            public String get(NBTBase base, String key) {
                return ChatColor.GREEN + "Current value of " + ChatColor.RESET + key + ChatColor.GREEN +
                        " is: " + ChatColor.RESET + ((NBTTagDouble) base).asDouble();
            }

            @Override
            public String set(NBTTagCompound compound, NBTBase base, String key, String value) {
                try {
                    double v = Double.parseDouble(value);
                    compound.setDouble(key, v);
                    return ChatColor.GREEN + "Set " + ChatColor.RESET + key + ChatColor.GREEN +
                            " to " + ChatColor.RESET + v;
                } catch (Exception ex) {
                    return ChatColor.RED + "Input not a double: " + ChatColor.RESET + value;
                }
            }
        });
        accessors.put(NBTTagInt.class, new PathingConfigAccessor() {
            @Override
            public String get(NBTBase base, String key) {
                return ChatColor.GREEN + "Current value of " + ChatColor.RESET + key + ChatColor.GREEN +
                        " is: " + ChatColor.RESET + ((NBTTagInt) base).asDouble();
            }

            @Override
            public String set(NBTTagCompound compound, NBTBase base, String key, String value) {
                try {
                    int v = Integer.parseInt(value);
                    compound.setInt(key, v);
                    return ChatColor.GREEN + "Set " + ChatColor.RESET + key + ChatColor.GREEN +
                            " to " + ChatColor.RESET + v;
                } catch (Exception ex) {
                    return ChatColor.RED + "Input not a double: " + ChatColor.RESET + value;
                }
            }
        });
    }

}
