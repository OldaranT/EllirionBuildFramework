package com.ellirion.buildframework.terraincorrector.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.terraincorrector.TerrainManager;

public class GetBoundingBoxesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = (Player) commandSender;

        if (TerrainManager.getBoundingBoxes().isEmpty()) {
            player.sendMessage("There are no known boundingboxes");
            return true;
        }

        player.sendMessage("Boundingboxes that are currently known");
        for (BoundingBox b : TerrainManager.getBoundingBoxes()) {
            player.sendMessage(b.toString());
        }

        return true;
    }
}
