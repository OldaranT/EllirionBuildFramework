package com.ellirion.buildframework.terraincorrector.command;

import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.terraincorrector.TerrainValidator;
import net.minecraft.server.v1_12_R1.Position;
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
            final Player player = (Player) commandSender;


            final TerrainValidator validator = new TerrainValidator();

            final Location location = player.getLocation();
            BoundingBox b = new BoundingBox(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getBlockX() + 1, location.getBlockY() + 1, location.getBlockZ() + 1);

            final World world = player.getWorld();
            String string = "" + (validator.validate(b, world, 1));

            player.sendMessage(string);


            final BoundingBox boundingBox = new BoundingBox(0, 0, 0, 10, 10, 10);
            final Position position = new Position(location.getBlockX(), location.getBlockY(), location.getBlockZ());

            final StringBuilder sb = new StringBuilder();
            sb.append("X : ").append(position.getX()).append(", Y : ").append(position.getY()).append(", Z : ").append(position.getZ());
            player.sendMessage(sb.toString());
            final Double distance = validator.findClosestBlock(position, boundingBox.toWorld(position), player.getWorld());
            player.sendMessage("Closest block is : " + distance);
        }

        return false;
    }
}
