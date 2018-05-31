package com.ellirion.buildframework;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.ellirion.buildframework.model.BoundingBox;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.util.TransactionManager;
import com.ellirion.buildframework.util.WorldEditHelper;
import com.ellirion.buildframework.util.WorldHelper;
import com.ellirion.buildframework.util.async.Promise;
import com.ellirion.buildframework.util.transact.SequenceTransaction;
import com.ellirion.buildframework.util.transact.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        new Promise<>(finisher -> {

            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("This command can only be executed by players");
            }
            Player player = (Player) commandSender;

            Selection sel = WorldEditHelper.getSelection(player);
            if (!(sel instanceof CuboidSelection)) {
                player.sendMessage(ChatColor.DARK_RED + "Invalid Selection!");
                finisher.resolve(null);
                return;
            }

            Point min = new Point(sel.getMinimumPoint());
            Point max = new Point(sel.getMaximumPoint());

            BoundingBox boundingBox = new BoundingBox(min, max);

            List<Transaction> list = new ArrayList<>();

            for (int x = boundingBox.getX1(); x <= boundingBox.getX2(); x++) {
                for (int y = boundingBox.getY1(); y <= boundingBox.getY2(); y++) {
                    for (int z = boundingBox.getZ1(); z <= boundingBox.getZ2(); z++) {
                        final Block b = WorldHelper.getBlock(player.getWorld(), x, y, z);

                        list.add(WorldHelper.setBlock(b.getLocation(), Material.COBBLESTONE, (byte) 1));
                    }
                }
            }

            TransactionManager.addDoneTransaction(player, new SequenceTransaction(list.toArray(new Transaction[0])));

            TransactionManager.undoLastTransaction(player);
            finisher.resolve(null);
        }, true);
        return true;
    }
}
