package com.ellirion.buildframework.terraincorrector.command;

import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.terraincorrector.TerrainValidator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Test implements CommandExecutor {
    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;


            TerrainValidator t = new TerrainValidator();

            Location location = player.getLocation();
            BoundingBox b = new BoundingBox(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getBlockX() + 1, location.getBlockY() + 1, location.getBlockZ() + 1);

            World world = player.getWorld();
            String string = "" + (t.validate(b, world, 1));

            player.sendMessage(string);

        }
        return false;
    }
}
