package com.ellirion.buildframework.terraincorrector.command;

import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.terraincorrector.TerrainValidator;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ValidateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] strings) {
        if (commandSender instanceof Player) {
            final Player player = (Player) commandSender;

            final TerrainValidator validator = new TerrainValidator();

            final BoundingBox boundingBox = new BoundingBox(-10, 0, -10, 10, 10, 10);
            final Location location = player.getLocation();
            final Point position = new Point(location.getBlockX(), location.getBlockY(), location.getBlockZ());

            final StringBuilder sb = new StringBuilder();
            sb.append("X : ").append(position.getX()).append(", Y : ").append(position.getY()).append(", Z : ").append(position.getZ());
            player.sendMessage(sb.toString());
            
            final double result = validator.validate(boundingBox.toWorld(position), player.getWorld());
            player.sendMessage(" Result  : " + result);
        }
        return false;
    }
}
