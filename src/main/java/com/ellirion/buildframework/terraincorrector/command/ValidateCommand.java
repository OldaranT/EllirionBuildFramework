package com.ellirion.buildframework.terraincorrector.command;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.terraincorrector.TerrainValidator;
import com.ellirion.buildframework.terraincorrector.model.TerrainValidatorModel;
import com.ellirion.buildframework.util.WorldEditHelper;

public class ValidateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String s,
                             final String[] strings) {
        if (!(commandSender instanceof Player)) {
            return true;
        }

        Player player = (Player) commandSender;

        Selection sel = WorldEditHelper.getSelection(player);

        if (!(sel instanceof CuboidSelection)) {
            player.sendMessage(ChatColor.DARK_RED + "Invalid Selection!");
            return true;
        }

        CuboidSelection selection = (CuboidSelection) sel;

        Point start = new Point(selection.getMinimumPoint());
        Point end = new Point(selection.getMaximumPoint());

        final TerrainValidator validator = new TerrainValidator();

        final BoundingBox boundingBox = new BoundingBox(start, end);

        final TerrainValidatorModel result = validator.validate(boundingBox, player.getWorld());
        if (result.isSucceeded()) {
            player.sendMessage(ChatColor.GREEN + "The selected area can be corrected");
            return true;
        }

        for (String str : result.getErrors()) {
            player.sendMessage(ChatColor.RED + str);
        }

        return true;
    }
}
