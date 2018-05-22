package com.ellirion.buildframework;

import com.ellirion.buildframework.pathfinder.command.CommandFindPath;
import com.ellirion.buildframework.pathfinder.command.CommandHidePath;
import com.ellirion.buildframework.pathfinder.command.CommandHideVisited;
import com.ellirion.buildframework.pathfinder.command.CommandPathConfig;
import com.ellirion.buildframework.pathfinder.event.PathingListener;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import com.ellirion.buildframework.templateengine.command.CommandAddMarker;
import com.ellirion.buildframework.templateengine.command.CommandCreateTemplate;
import com.ellirion.buildframework.templateengine.command.CommandCreateTemplateHologram;
import com.ellirion.buildframework.templateengine.command.CommandExportTemplate;
import com.ellirion.buildframework.templateengine.command.CommandImportTemplate;
import com.ellirion.buildframework.templateengine.command.CommandPutTemplate;
import com.ellirion.buildframework.templateengine.command.CommandRemoveHologram;
import com.ellirion.buildframework.templateengine.command.CommandRemoveMarker;
import com.ellirion.buildframework.terraincorrector.command.ValidateCommand;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuildFramework extends JavaPlugin {

    @SuppressWarnings("PMD.SuspiciousConstantFieldName")
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

    @Override
    public void onEnable() {
        getCommand("CreateTemplate").setExecutor(new CommandCreateTemplate());
        getCommand("PutTemplate").setExecutor(new CommandPutTemplate());
        getCommand("ExportTemplate").setExecutor(new CommandExportTemplate());
        getCommand("ImportTemplate").setExecutor(new CommandImportTemplate());
        getCommand("Validate").setExecutor(new ValidateCommand());
        getCommand("AddMarker").setExecutor(new CommandAddMarker());
        getCommand("RemoveMarker").setExecutor(new CommandRemoveMarker());
        getCommand("CreateHologram").setExecutor(new CommandCreateTemplateHologram());
        getCommand("RemoveHologram").setExecutor(new CommandRemoveHologram());
        getCommand("FindPath").setExecutor(new CommandFindPath());
        getCommand("HidePath").setExecutor(new CommandHidePath());
        getCommand("HideVisited").setExecutor(new CommandHideVisited());
        getCommand("PathConfig").setExecutor(new CommandPathConfig());
        getServer().getPluginManager().registerEvents(new PathingListener(), this);
        createConfig();
        createBlockValueConfig();
        createTemplateFormatConfig();
        getLogger().info("BuildFramework is enabled.");
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

    private void createConfig() {
        config.options().header("Ellirion-BuildFramework configuration file");
        // terrain validation config settings
        config.addDefault("TerrainCorrector.OverheadLimit", 20);
        config.addDefault("TerrainCorrector.BlocksLimit", 40);
        config.addDefault("TerrainCorrector.TotalLimit", 50);
        config.addDefault("TerrainCorrector.Offset", 5);
        config.addDefault("TerrainCorrector.BoundingBoxMinDist", 5);
        // template config settings
        config.addDefault("TemplateEngine.Path", "plugins/Ellirion-BuildFramework/templates/");
        config.options().copyDefaults(true);
        saveConfig();
        reloadConfig();
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

        //try and save the file

        try {
            blockValueConfig.save(blockValueConfigFile);
        } catch (IOException e) {
            getLogger().throwing(BuildFramework.class.toString(), "createBlockValueConfig", e);
        }
    }

    private void createTemplateFormatConfig() {
        //set the variables that are needed for the config

        List<String> raceList = Arrays.asList("ARGORIAN", "DWARF", "ELF", "KHAJIIT", "ORC", "VIKING", "INFECTED",
                                              "HUMAN");

        List<String> typeList = Arrays.asList("HOUSE", "BLACKSMITH", "TOWNHALL", "SAWMILL", "STABLE", "BARRACK",
                                              "WINDMILL", "HARBOR");

        List<String> levelList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            levelList.add(Integer.toString(i));
        }

        List<String> markerList = Arrays.asList("DOOR", "GROUND", "PATH");

        String racePath = "Races";
        String typePath = "Types";
        String levelPath = "Levels";
        String markersPath = "Markers";

        //get the file and load the config from the file
        File templateFormatConfigFile = new File(getDataFolder(), "TemplateFormat.yml");
        templateFormatConfig = YamlConfiguration.loadConfiguration(templateFormatConfigFile);

        templateFormatConfig.options().header("This file has all the filters to create a template.\n" +
                                              "their are 3 options: RACE, TYPE, LEVEL.\n" +
                                              "Markers is a list of all the possible markers you can use.\n");

        templateFormatConfig.addDefault(racePath, raceList);
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
}
