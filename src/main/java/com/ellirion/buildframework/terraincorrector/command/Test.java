package com.ellirion.buildframework.terraincorrector.command;

import com.ellirion.buildframework.BuildFramework;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Test implements CommandExecutor {
    private static final Logger LOGGER = Logger.getGlobal();
    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] strings) {

        final FileConfiguration config = BuildFramework.getPlugin(BuildFramework.class).getConfig();

        if (LOGGER.isLoggable(Level.INFO))
        {
            LOGGER.info("" + config.getInt("TerrainValidation_OverheadLimit", 0));
        }

//        if (commandSender instanceof Player) {
//            final Player player = (Player) commandSender;
//
//            final TerrainValidator validator = new TerrainValidator();
//
//            final BoundingBox boundingBox = new BoundingBox(0, 0, 0, 10, 10, 10);
//            final Location loc =  player.getLocation();
//            final Position pos = new Position(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
//
//            final StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append("X : ").append(pos.getX()).append(", Y : ").append(pos.getY()).append(", Z : ").append(pos.getZ());
//            player.sendMessage(stringBuilder.toString());
//            final Double score = validator.calculateOverhang(boundingBox.toWorld(pos), player.getWorld());
//            player.sendMessage("score is : " + score);
//        }
        return false;
    }
}
