package com.ellirion.buildframework.pathfinder.event;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import com.ellirion.buildframework.model.Point;
import com.ellirion.buildframework.pathfinder.PathingManager;
import com.ellirion.buildframework.pathfinder.model.PathingGraph;
import com.ellirion.buildframework.pathfinder.model.PathingSession;
import com.ellirion.buildframework.pathfinder.model.PathingVertex;

import java.util.Arrays;

public class PathingListener implements Listener, CommandExecutor {

    private static ItemStack PATHING_TOOL;

    /**
     * Constructor to create the pathing tool.
     */
    public PathingListener() {
        if (PATHING_TOOL != null) {
            return;
        }
        ItemStack a = new ItemStack(Material.STICK);
        ItemMeta meta = a.getItemMeta();
        meta.setLore(Arrays.asList("Pathing Tool"));
        a.setItemMeta(meta);
        PATHING_TOOL = a;
    }

    /**
     * @param event The event to handle
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // If the item isn't a stick, ignore,
        if (event.getItem() == null) {
            return;
        }

        if (event.getItem().equals(PATHING_TOOL)) {
            // Stick = path end point selector
            Block block = event.getClickedBlock();

            // If no block was selected, ignore.
            if (block == null) {
                block = event.getPlayer().getTargetBlock(null, 35565);
            }

            if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
                PathingManager.getSession(event.getPlayer())
                        .setPoint1(new Point(block.getLocation()));
                event.getPlayer().sendMessage(ChatColor.GREEN + "Point 1 set");
                event.setCancelled(true);
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                PathingManager.getSession(event.getPlayer())
                        .setPoint2(new Point(block.getLocation()));
                event.getPlayer().sendMessage(ChatColor.GREEN + "Point 2 set");
                event.setCancelled(true);
            }
        }
    }

    /**
     * @param event The event to handle
     */
    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player p = event.getPlayer();
        if (p.getItemOnCursor().getType() == Material.STICK) {
            event.setCancelled(true);
        }
    }

    /**
     * @param event The event to handle
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Only debug when holding a stick
        if (player.getInventory().getItemInMainHand().getType() != Material.STICK) {
            return;
        }

        Point from = new Point(event.getFrom()).floor();
        Point to = new Point(event.getTo()).floor();

        if (!from.equals(to)) {
            printBlockInfo(player, to.down());
        }
    }

    private void printBlockInfo(Player player, Point point) {
        PathingSession session = PathingManager.getSession(player);
        PathingGraph graph = session.getGraph();

        if (graph == null) {
            return;
        }

        PathingVertex vert = graph.find(point);
        if (vert == null) {
            return;
        }

        player.sendMessage((vert.isVisited() ? ChatColor.GREEN : ChatColor.RED) + "" + vert);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;

        player.getInventory().addItem(PATHING_TOOL);

        return true;
    }
}
