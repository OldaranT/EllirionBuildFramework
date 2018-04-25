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
import com.ellirion.buildframework.terraincorrector.TerrainCorrector;
import com.ellirion.buildframework.util.WorldEditHelper;

public class CorrectCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }

        Player player = (Player) commandSender;

        Selection sel = WorldEditHelper.getSelection(player);

        if (!(sel instanceof CuboidSelection)) {
            player.sendMessage(ChatColor.DARK_RED + "Invalid Selection!");
            return false;
        }

        CuboidSelection selection = (CuboidSelection) sel;

        Point start = new Point(selection.getMinimumPoint());
        Point end = new Point(selection.getMaximumPoint());

        BoundingBox bb = new BoundingBox(start, end);

        TerrainCorrector corrector = new TerrainCorrector();
        corrector.correctTerrain(bb, player.getWorld());

        return false;
    }
}
