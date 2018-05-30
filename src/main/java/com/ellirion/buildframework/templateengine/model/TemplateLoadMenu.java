package com.ellirion.buildframework.templateengine.model;

import net.minecraft.server.v1_12_R1.NBTCompressedStreamTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.plugin.Plugin;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.templateengine.TemplateManager;
import com.ellirion.buildframework.templateengine.util.FileUtil;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class TemplateLoadMenu implements Listener {

    private Inventory inv;
    private ItemStack[] items;
    private ItemStack back;
    private Stack<String> previousMenus;
    private Player player;
    private String inventoryName;
    private String currentSelectedRace;

    /**
     * Create a menu for loading templates.
     * @param p the plugin that instantiates this menu
     * @param player the player this menu is for
     */
    public TemplateLoadMenu(final Plugin p, final Player player) {
        currentSelectedRace = "";
        back = createItemStack(DyeColor.WHITE, "Back", Arrays.asList(new String[] {"Go back"}));
        previousMenus = new Stack<>();
        this.player = player;

        createCorrectMenu("overview", this.player); //NOPMD
        Bukkit.getServer().getPluginManager().registerEvents(this, p);
    }

    private void setInventoryItems() {
        inv = Bukkit.getServer().createInventory(null, 27, inventoryName);
        inv.setContents(items);

        if (!previousMenus.peek().equals("overview")) { //NOPMD
            inv.setItem(18, back);
        }
    }

    private void createRacesInventory() {
        List<String> races = BuildFramework.getInstance().getTemplateFormatConfig().getStringList("Races");
        List<String> raceColors = BuildFramework.getInstance().getTemplateFormatConfig().getStringList("RaceColors");
        items = new ItemStack[races.size()];

        int i = 0;
        for (String s : races) {
            DyeColor color = DyeColor.valueOf(raceColors.get(i));
            ItemStack stack = createItemStack(color, "Race: " + s,
                                              Arrays.asList(new String[] {"Create a", s, "building"}));
            items[i] = stack;
            i++;
        }

        inventoryName = "Pick a race";
    }

    private void createBuildingTypesInventory() {
        List<String> buildingTypes = BuildFramework.getInstance().getTemplateFormatConfig().getStringList("Types");
        items = new ItemStack[buildingTypes.size()];

        int i = 0;
        DyeColor raceColor = getRaceColor(currentSelectedRace);
        for (String s : buildingTypes) {
            ItemStack stack = createItemStack(raceColor, "Building: " + s,
                                              Arrays.asList(new String[] {"Create a", s}));
            items[i] = stack;
            i++;
        }

        inventoryName = getCurrentState().split(" ")[1] + ". Pick a building type";
    }

    private void createLevelsInventory() {
        List<String> levels = BuildFramework.getInstance().getTemplateFormatConfig().getStringList("Levels");
        items = new ItemStack[levels.size()];

        int i = 0;
        DyeColor raceColor = getRaceColor(currentSelectedRace);
        for (String s : levels) {
            ItemStack stack = createItemStack(raceColor, "Level: " + s, Arrays.asList(new String[] {"Level", s}));
            items[i] = stack;
            i++;
        }

        inventoryName = createInventoryName() + ". Pick a level";
    }

    private void createBuildingsInventory(String level) {
        List<String> buildings = FileUtil.getListOfNBTFileNames();
        items = new ItemStack[buildings.size()];

        String buildingPrefix = inv.getName().split("\\.")[0].replaceAll(" ", "-") + level;

        int i = 0;
        DyeColor raceColor = getRaceColor(currentSelectedRace);
        for (String s : buildings) {
            if (s.startsWith(buildingPrefix)) {
                ItemStack stack = createItemStack(raceColor, s, Arrays.asList(new String[] {}));
                items[i] = stack;
                i++;
            }
        }

        inventoryName = createInventoryName() + ". Pick a template";
    }

    private DyeColor getRaceColor(String race) {
        int index = BuildFramework.getInstance().getTemplateFormatConfig().getStringList("Races").indexOf(race);
        String color = BuildFramework.getInstance().getTemplateFormatConfig().getStringList("RaceColors").get(index);
        return DyeColor.valueOf(color);
    }

    private ItemStack createItemStack(DyeColor color, String name, List<String> lore) {
        ItemStack i = new Wool(color).toItemStack(1);
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(name);
        im.setLore(lore);
        i.setItemMeta(im);
        return i;
    }

    private String createInventoryName() {
        StringBuilder builder = new StringBuilder();

        int i = 0;
        for (String s : previousMenus) {
            if (i > 0) {
                builder.append(s.split(" ")[1] + " ");
            }
            i++;
        }

        return builder.toString();
    }

    private String getCurrentState() {
        return previousMenus.peek();
    }

    /**
     * Show menu to a player.
     * @param p the player to whom to show the menu
     */
    public void show(Player p) {
        p.openInventory(inv);
    }

    private void loadTemplate(String templateName) {
        player.closeInventory();
        try {
            // Load the template and create a hologram in front of the player
            Template t = Template.fromNBT(
                    NBTCompressedStreamTools.a(new FileInputStream(FileUtil.getTemplate(templateName))));
            TemplateSession ts = new TemplateSession(t, null);
            TemplateManager.getTemplateSessions().put(player, ts);

            Location playerLocation = player.getLocation();
            Location hologramLocation = new Location(
                    player.getWorld(),
                    playerLocation.getBlockX(),
                    playerLocation.getBlockY(),
                    playerLocation.getBlockZ()
            );
            TemplateHologram hologram = new TemplateHologram(t, hologramLocation);
            TemplateManager.getSelectedHolograms().put(player, hologram);
            hologram.create(player);
        } catch (Exception e) {
            BuildFramework.getInstance().getLogger().severe(e.getMessage());
            player.sendMessage(ChatColor.DARK_RED + "Something went wrong when trying to load this template");
        }
    }

    /**
     * Listen for inventory click events.
     * @param e the inventory click event data
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getInventory().equals(inv) || !((Player) e.getWhoClicked()).equals(player)) {
            return;
        }

        String item = e.getCurrentItem().getItemMeta().getDisplayName();

        if (item.equals("Back")) {
            // Pop twice, because the stack also has the current state
            previousMenus.pop();
            String currentState = previousMenus.pop();

            createCorrectMenu(currentState, (Player) e.getWhoClicked());
        } else {
            createCorrectMenu(item, (Player) e.getWhoClicked());
        }
    }

    private void createCorrectMenu(String item, Player player) {
        if (item.startsWith("overview")) { //NOPMD
            // Create overview
            previousMenus.push(item);
            createRacesInventory();
        } else if (item.startsWith("Race: ")) {
            // Create building types menu
            previousMenus.push(item);
            currentSelectedRace = item.split(" ")[1];
            createBuildingTypesInventory();
        } else if (item.startsWith("Building: ")) {
            // Create building levels menu
            previousMenus.push(item);
            createLevelsInventory();
        } else if (item.startsWith("Level: ")) {
            previousMenus.push(item);
            createBuildingsInventory(item.split(" ")[1]);
        } else {
            // We must have clicked on a template, load this template
            loadTemplate(item);
            return;
        }

        setInventoryItems();
        show(player);
    }
}
