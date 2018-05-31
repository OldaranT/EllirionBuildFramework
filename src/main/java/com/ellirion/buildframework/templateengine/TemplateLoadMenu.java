package com.ellirion.buildframework.templateengine;

import net.minecraft.server.v1_12_R1.NBTCompressedStreamTools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.plugin.Plugin;
import com.ellirion.buildframework.BuildFramework;
import com.ellirion.buildframework.templateengine.model.Template;
import com.ellirion.buildframework.templateengine.model.TemplateHologram;
import com.ellirion.buildframework.templateengine.model.TemplateSession;
import com.ellirion.buildframework.templateengine.util.FileUtil;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class TemplateLoadMenu implements Listener {

    private Inventory inv;
    private ItemStack[] items;
    private ItemStack back;
    private ItemStack next;
    private ItemStack prev;
    private Stack<String> previousMenus;
    private Player player;
    private String inventoryName;
    private String currentSelectedRace;
    private String currentSelectedBuildingType;
    private int currentPage;

    /**
     * Create a menu for loading templates.
     * @param p the plugin that instantiates this menu
     * @param player the player this menu is for
     */
    public TemplateLoadMenu(final Plugin p, final Player player) {
        currentSelectedRace = "";
        currentSelectedBuildingType = "";
        currentPage = 0;
        back = createItemStack(DyeColor.WHITE, "Back", Arrays.asList(new String[] {"Go back"}));
        next = createItemStack(DyeColor.WHITE, "Next page", new LinkedList<>());
        prev = createItemStack(DyeColor.WHITE, "Previous page", new LinkedList<>());
        previousMenus = new Stack<>();
        this.player = player;

        createCorrectMenu("overview", this.player); //NOPMD
        Bukkit.getServer().getPluginManager().registerEvents(this, p);
    }

    private void setInventoryItems() {
        // We have to create a new inventory every time to update the name
        inv = Bukkit.getServer().createInventory(null, 27, inventoryName);

        // Loop through the first 18 items in the items array
        int length = (items.length > 18) ? 18 : items.length;
        for (int i = 0; i < length; i++) {
            inv.setItem(i, items[i]);
        }

        if (items.length > 18) {
            inv.setItem(23, next);
        }

        if (!previousMenus.peek().equals("overview")) { //NOPMD
            inv.setItem(18, back);
        }
    }

    private void setInventoryItems(int page) {
        inv = Bukkit.getServer().createInventory(null, 27, inventoryName);

        // Loop through the amount of items on the nth page
        int length = items.length - (page * 18) > 18 ? 18 : items.length - (page * 18) - 1;
        for (int i = 18 * page; i < length + (18 * page); i++) {
            inv.setItem(i - (18 * page), items[i]);
        }

        // If the page is higher than 0, add a 'previous page' item
        if (page > 0) {
            inv.setItem(21, prev);
        }

        // If there is a next page (there are more than (page + 1) * 18 items), add a 'next page' item
        if (items.length > ((page + 1) * 18)) {
            inv.setItem(23, next);
        }

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
            int amountOfFiles = FileUtil.getNumberOfTemplatesWithPrefix(s);
            ItemStack stack = createItemStack(color, "Race: " + s,
                                              Arrays.asList("Create a", s, "building", amountOfFiles + " templates"));
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
            int amountOfFiles = FileUtil.getNumberOfTemplatesWithPrefix(currentSelectedRace + "-" + s);
            ItemStack stack = createItemStack(raceColor, "Building: " + s,
                                              Arrays.asList(
                                                      new String[] {"Create a", s, amountOfFiles + " templates"}));
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
            int amountOfFiles = FileUtil.getNumberOfTemplatesWithPrefix(
                    currentSelectedRace + "-" + currentSelectedBuildingType + "-" + s);
            ItemStack stack = createItemStack(raceColor, "Level: " + s,
                                              Arrays.asList(new String[] {"Level", s, amountOfFiles + " templates"}));
            items[i] = stack;
            i++;
        }

        inventoryName = createInventoryName() + ". Pick a level";
    }

    private void createBuildingsInventory(String level) {
        List<String> buildings = FileUtil.getListOfNBTFileNames();
        items = new ItemStack[buildings.size()];

        // This will get a string along the lines of "RACE-TYPE-LEVEL"
        String buildingPrefix = inv.getName().split("\\.")[0].replaceAll(" ", "-") + "-" + level;

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
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        i.setItemMeta(im);
        return i;
    }

    // Create a string along the lines of "RACE-TYPE-LEVEL", leaving out type and level if those haven't been selected yet
    private String createInventoryName() {
        StringBuilder builder = new StringBuilder();

        int i = 0;
        for (String s : previousMenus) {
            // Ignore the 'overview' menu
            if (i > 0) {
                builder.append(s.split(" ")[1]);
                if (i < previousMenus.size() - 1) {
                    builder.append(' ');
                }
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

        ItemStack item = e.getCurrentItem();
        if (item.getType() == Material.AIR) {
            return;
        }
        String itemName = item.getItemMeta().getDisplayName();

        if (itemName.equals("Back")) {
            // Pop twice, because the stack also has the current state
            previousMenus.pop();
            String currentState = previousMenus.pop();

            createCorrectMenu(currentState, (Player) e.getWhoClicked());
            currentPage = 0;
        } else if (itemName.equals("Previous page")) {
            currentPage--;
            setInventoryItems(currentPage);
            show(player);
        } else if (itemName.equals("Next page")) {
            currentPage++;
            setInventoryItems(currentPage);
            show(player);
        } else {
            createCorrectMenu(itemName, (Player) e.getWhoClicked());
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
            currentSelectedBuildingType = item.split(" ")[1];
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
