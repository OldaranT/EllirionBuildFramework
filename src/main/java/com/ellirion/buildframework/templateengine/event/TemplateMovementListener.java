package com.ellirion.buildframework.templateengine.event;

import org.bukkit.event.Listener;

public class TemplateMovementListener implements Listener {
    //
    //    /**
    //     * Move or rotate the hologram while using player interaction events.
    //     * @param event is used to get the tool that the player interacts with.
    //     */
    //    @EventHandler
    //    public void onPlayerInteract(PlayerInteractEvent event) {
    //        if (event.getItem() == null) {
    //            return;
    //        }
    //
    //        if (!event.getItem().getItemMeta().getLore().contains(Template.getTemplateTool())) {
    //            return;
    //        }
    //
    //        Player player = event.getPlayer();
    //
    //        TemplateSession ts = TemplateManager.getTemplateSessions().get(player);
    //
    //        TemplateHologram prevHologram = TemplateManager.getSelectedHolograms().get(player);
    //
    //        if (prevHologram == null) {
    //            player.sendMessage(ChatColor.RED + "Create a hologram first.");
    //            return;
    //        }
    //
    //        BlockFace blockFace = prevHologram.rotationToFace(player.getLocation().getYaw(),
    //                                                          player.getLocation().getPitch());
    //        Template t = prevHologram.getTemplate();
    //        prevHologram.remove(player);
    //
    //        Material currentItem = event.getItem().getType();
    //
    //        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
    //            if (currentItem == Material.WOOL && event.getItem().getDurability() == 13) {
    //                ts.getTemplate().putTemplateInWorld(prevHologram.getLocation());
    //                PlayerTemplateGuiSession.quitSession(prevHologram, player);
    //                return;
    //            } else if (currentItem == Material.WOOL && event.getItem().getDurability() == 14) {
    //                PlayerTemplateGuiSession.quitSession(prevHologram, player);
    //                return;
    //            } else if (currentItem == Material.DIAMOND_SPADE) {
    //                prevHologram.setLocation(new Location(player.getWorld(),
    //                                                      player.getTargetBlock(null, 35565).getX(),
    //                                                      player.getTargetBlock(null, 35565).getY(),
    //                                                      player.getTargetBlock(null, 35565).getZ()));
    //            } else if (currentItem == Material.DIAMOND_HOE) {
    //                t.rotateTemplate(false);
    //            } else if (currentItem == Material.DIAMOND_SWORD) {
    //                prevHologram.moveHologram(1, blockFace);
    //            }
    //            prevHologram = new TemplateHologram(t, prevHologram.getLocation());
    //        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
    //            if (currentItem == Material.DIAMOND_SPADE) {
    //                prevHologram.setLocation(new Location(player.getWorld(),
    //                                                      player.getLocation().getBlockX(),
    //                                                      player.getLocation().getBlockY(),
    //                                                      player.getLocation().getBlockZ()));
    //            } else if (currentItem == Material.DIAMOND_HOE) {
    //                t.rotateTemplate(true);
    //            } else if (currentItem == Material.DIAMOND_SWORD) {
    //                prevHologram.moveHologram(1, blockFace.getOppositeFace());
    //            }
    //            prevHologram = new TemplateHologram(t, prevHologram.getLocation());
    //        }
    //        prevHologram.create(player);
    //        event.setCancelled(true);
    //    }
}
