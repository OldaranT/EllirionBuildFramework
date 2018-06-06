package com.ellirion.buildframework;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import com.ellirion.buildframework.pathbuilder.command.CommandPathBuilder;
import com.ellirion.buildframework.pathfinder.command.CommandFindPath;
import com.ellirion.buildframework.pathfinder.command.CommandHidePath;
import com.ellirion.buildframework.pathfinder.command.CommandHideVisited;
import com.ellirion.buildframework.pathfinder.command.CommandPathConfig;
import com.ellirion.buildframework.pathfinder.event.PathingListener;
import com.ellirion.buildframework.command.PlayerRedoCommand;
import com.ellirion.buildframework.command.PlayerUndoCommand;
import com.ellirion.buildframework.templateengine.command.CommandAddMarker;
import com.ellirion.buildframework.templateengine.command.CommandCreateTemplate;
import com.ellirion.buildframework.templateengine.command.CommandCreateTemplateHologram;
import com.ellirion.buildframework.templateengine.command.CommandExportTemplate;
import com.ellirion.buildframework.templateengine.command.CommandImportTemplate;
import com.ellirion.buildframework.templateengine.command.CommandLoadTemplate;
import com.ellirion.buildframework.templateengine.command.CommandPutTemplate;
import com.ellirion.buildframework.templateengine.command.CommandRemoveHologram;
import com.ellirion.buildframework.templateengine.command.CommandRemoveMarker;
import com.ellirion.buildframework.templateengine.util.TabCompletionFileNameList;
import com.ellirion.buildframework.templateengine.util.TabCompletionMarkerNameList;
import com.ellirion.buildframework.templateengine.util.TabCompletionNameCreator;
import com.ellirion.buildframework.terraincorrector.command.AddBoundingBoxCommand;
import com.ellirion.buildframework.terraincorrector.command.CorrectCommand;
import com.ellirion.buildframework.terraincorrector.command.GetBoundingBoxesCommand;
import com.ellirion.buildframework.terraincorrector.command.ValidateCommand;
import com.ellirion.buildframework.util.EventListener;
import com.ellirion.buildframework.util.WorldHelper;
import com.ellirion.buildframework.util.async.Promise;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuildFramework extends JavaPlugin {

    private static BuildFramework INSTANCE;
    private FileConfiguration config = getConfig();
    private FileConfiguration blockValueConfig;
    private FileConfiguration templateFormatConfig;

    /**
     * Constructor to set instance.
     */
    public BuildFramework() {
        super();

        INSTANCE = this;
    }

    /**
     * @return BuildFramework instance
     */
    public static BuildFramework getInstance() {
        return INSTANCE;
    }

    public FileConfiguration getBlockValueConfig() {
        return blockValueConfig;
    }

    public FileConfiguration getTemplateFormatConfig() {
        return templateFormatConfig;
    }

    @Override
    public void onDisable() {
        getLogger().info("BuildFramework is disabled.");
    }

    @Override
    public void onEnable() {
        registerCommands();
        registerTabCompleters();
        registerListeners();
        createConfig();
        createFilePaths();
        createBlockValueConfig();
        createTemplateFormatConfig();
        getLogger().info("BuildFramework is enabled.");

        Promise.setSyncRunner(r -> Bukkit.getScheduler().runTask(this, r));
        Promise.setAsyncRunner(r -> Bukkit.getScheduler().runTaskAsynchronously(this, r));

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, WorldHelper::run, 1L, 1L);
    }

    private void createConfig() {
        config.options().header("Ellirion-BuildFramework configuration file");
        // Terrain validation config settings
        config.addDefault("TerrainCorrector.OverheadLimit", 20);
        config.addDefault("TerrainCorrector.BlocksLimit", 40);
        config.addDefault("TerrainCorrector.TotalLimit", 50);
        config.addDefault("TerrainCorrector.Offset", 5);
        config.addDefault("TerrainCorrector.BoundingBoxMinDist", 5);
        // Terrain corrector config settings
        config.addDefault("TerrainCorrector.MaxHoleDepth", 5);
        config.addDefault("TerrainCorrector.AreaLimitOffset", 5);
        config.addDefault("TerrainCorrector.BridgeCenterSupportClearancePercentage", 15);
        config.addDefault("TerrainCorrector.HoleFillerMaxDepth", 5);
        config.addDefault("TerrainCorrector.HoleFillerChanceToChangeDepth", 10);
        // Template config settings
        config.addDefault("TemplateEngine.Path", "plugins/Ellirion-BuildFramework/templates/");
        // Path builder config
        config.addDefault("PathBuilder.pathbuilderPath", "plugins/Ellirion-BuildFramework/pathbuilders/");
        config.addDefault("PathBuilder.floodFillDepth", 5000000);
        config.options().copyDefaults(true);
        saveConfig();
        reloadConfig();
    }

    // Create filepaths if they don't exist yet
    private void createFilePaths() {
        Path path = Paths.get(config.getString("templatePath"));
        if (!Files.exists(path) && !path.toFile().mkdirs()) {
            getLogger().warning("The path for templates could not be created");
        }
        path = Paths.get(config.getString("PathBuilder.pathbuilderPath"));
        if (!Files.exists(path) && !path.toFile().mkdirs()) {
            getLogger().warning("The path for PathBuilders could not be created");
        }
    }

    private void createBlockValueConfig() {
        File blockValueConfigFile = new File(getDataFolder(), "BlockValues.yml");
        blockValueConfig = YamlConfiguration.loadConfiguration(blockValueConfigFile);

        blockValueConfig.options().header("The values for each block type of block material.\n" +
                                          "These values are used by the terrain validator.");
        for (Material m : Material.values()) {
            blockValueConfig.addDefault(m.toString(), 1);
        }

        blockValueConfig.options().copyDefaults(true);

        // Try and save the file

        try {
            blockValueConfig.save(blockValueConfigFile);
        } catch (IOException e) {
            getLogger().throwing(BuildFramework.class.toString(), "createBlockValueConfig", e);
        }
    }

    private void createTemplateFormatConfig() {
        // Set the variables that are needed for the config

        List<String> raceList = Arrays.asList("ARGORIAN", "DWARF", "ELF", "KHAJIIT", "ORC", "VIKING", "INFECTED",
                                              "HUMAN");
        List<String> raceColors = Arrays.asList("GREEN", "SILVER", "CYAN", "ORANGE", "RED", "GRAY", "BLACK", "WHITE");

        List<String> typeList = Arrays.asList("HOUSE", "BLACKSMITH", "TOWNHALL", "SAWMILL", "STABLE", "BARRACK",
                                              "WINDMILL", "HARBOR");

        List<String> levelList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            levelList.add(Integer.toString(i));
        }

        List<String> markerList = Arrays.asList("DOOR", "GROUND", "PATH");

        String racePath = "Races";
        String raceColorsPath = "RaceColors";
        String typePath = "Types";
        String levelPath = "Levels";
        String markersPath = "Markers";

        // Get the file and load the config from the file
        File templateFormatConfigFile = new File(getDataFolder(), "TemplateFormat.yml");
        templateFormatConfig = YamlConfiguration.loadConfiguration(templateFormatConfigFile);

        templateFormatConfig.options().header("This file has all the filters to create a template.\n" +
                                              "their are 3 options: RACE, TYPE, LEVEL.\n" +
                                              "Markers is a list of all the possible markers you can use.\n");

        templateFormatConfig.addDefault(racePath, raceList);
        templateFormatConfig.addDefault(raceColorsPath, raceColors);
        templateFormatConfig.addDefault(typePath, typeList);
        templateFormatConfig.addDefault(levelPath, levelList);
        templateFormatConfig.addDefault(markersPath, markerList);

        templateFormatConfig.options().copyDefaults(true);

        try {
            templateFormatConfig.save(templateFormatConfigFile);
        } catch (IOException e) {
            getLogger().throwing(BuildFramework.class.toString(), "createTemplateFormatConfig", e);
        }
    }

    private void registerCommands() {
        // Template engine
        getCommand("CreateTemplate").setExecutor(new CommandCreateTemplate());
        getCommand("PutTemplate").setExecutor(new CommandPutTemplate());
        getCommand("ExportTemplate").setExecutor(new CommandExportTemplate());
        getCommand("ImportTemplate").setExecutor(new CommandImportTemplate());
        getCommand("AddMarker").setExecutor(new CommandAddMarker());
        getCommand("RemoveMarker").setExecutor(new CommandRemoveMarker());
        getCommand("RemoveMarker").setTabCompleter(new TabCompletionMarkerNameList());
        getCommand("CreateHologram").setExecutor(new CommandCreateTemplateHologram());
        getCommand("RemoveHologram").setExecutor(new CommandRemoveHologram());
        getCommand("CorrectTerain").setExecutor(new CorrectCommand());
        getCommand("GetBoundingboxes").setExecutor(new GetBoundingBoxesCommand());
        getCommand("AddBoundingBox").setExecutor(new AddBoundingBoxCommand());
        getCommand("LoadTemplate").setExecutor(new CommandLoadTemplate());

        // Terrain validator
        getCommand("Validate").setExecutor(new ValidateCommand());

        // Transactions
        getCommand("Undo").setExecutor(new PlayerUndoCommand());
        getCommand("Redo").setExecutor(new PlayerRedoCommand());

        // Path finder
        getCommand("FindPath").setExecutor(new CommandFindPath());
        getCommand("HidePath").setExecutor(new CommandHidePath());
        getCommand("HideVisited").setExecutor(new CommandHideVisited());
        getCommand("PathConfig").setExecutor(new CommandPathConfig());
        getCommand("PathTool").setExecutor(new PathingListener());

        // Path builder
        getCommand("PathBuilder").setExecutor(new CommandPathBuilder());
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getServer().getPluginManager().registerEvents(new PathingListener(), this);
    }

    private void registerTabCompleters() {
        getCommand("CreateTemplate").setTabCompleter(new TabCompletionNameCreator());
        getCommand("ImportTemplate").setTabCompleter(new TabCompletionFileNameList());
        getCommand("AddMarker").setTabCompleter(new TabCompletionMarkerNameList());
        getCommand("RemoveMarker").setTabCompleter(new TabCompletionMarkerNameList());
    }
}
