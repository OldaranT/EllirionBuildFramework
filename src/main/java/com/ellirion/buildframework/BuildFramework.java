package com.ellirion.buildframework;

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

    private static BuildFramework instance;
    private FileConfiguration config = getConfig();
    private FileConfiguration blockValueConfig;
    private FileConfiguration templateFormatConfig;

    @Override
    public void onEnable() {
        instance = this;
        getCommand("CreateTemplate").setExecutor(new CommandCreateTemplate());
        getCommand("PutTemplate").setExecutor(new CommandPutTemplate());
        getCommand("ExportTemplate").setExecutor(new CommandExportTemplate());
        getCommand("ImportTemplate").setExecutor(new CommandImportTemplate());
        getCommand("Validate").setExecutor(new ValidateCommand());
        getCommand("AddMarker").setExecutor(new CommandAddMarker());
        getCommand("RemoveMarker").setExecutor(new CommandRemoveMarker());
        getCommand("CreateHologram").setExecutor(new CommandCreateTemplateHologram());
        getCommand("RemoveHologram").setExecutor(new CommandRemoveHologram());
        createConfig();
        createBlockValueConfig();
        createTemplateFormatConfig();
        getLogger().info("BuildFramework is enabled.");
    }

    /**
     * @return BuildFramework instance
     */
    public static BuildFramework getInstance() {
        if (instance == null) {
            instance = new BuildFramework();
        }
        return instance;
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
        config.addDefault("templatePath", "plugins/Ellirion-BuildFramework/templates/");
        config.addDefault("TerrainValidation_OverheadLimit", 20);
        config.addDefault("TerrainValidation_BlocksLimit", 40);
        config.addDefault("TerrainValidation_TotalLimit", 50);
        config.addDefault("TerrainValidation_Offset", 5);
        config.addDefault("TerrainValidator_BoundingBoxMinDist", 5);
        config.addDefault("DOOR", 0);
        config.addDefault("PATH", 1);
        config.addDefault("GROUND", 2);
        config.options().copyDefaults(true);
        saveConfig();
        reloadConfig();
    }

    private void createBlockValueConfig() {

        File blockValueConfigFile = new File(getDataFolder(), "BlockValues.yml");
        blockValueConfig = YamlConfiguration.loadConfiguration(blockValueConfigFile);

        // custom addDefault because the normal addDefault doesn't work
        if (!blockValueConfigFile.exists()) {
            blockValueConfig.options().header("The values for each block type of block material.\n" +
                                              "These values are used by the terrain validator.");
            for (Material m : Material.values()) {
                blockValueConfig.set(m.toString(), 1);
            }
        } else {
            for (Material m : Material.values()) {
                if (!blockValueConfig.isSet(m.toString())) {
                    blockValueConfig.set(m.toString(), 1);
                }
            }
        }

        //try and save the file
        try {
            blockValueConfig.save(blockValueConfigFile);
        } catch (IOException e) {
            getLogger().throwing(getClass().toString(), "createBlockValueConfig", e);
        }
    }

    private void createTemplateFormatConfig() {
        //set the lists that need to go into the config

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

        //check if config exists and if it does not exist create the config.
        //if it does exist then check if all the paths are set.
        if (!templateFormatConfigFile.exists()) {

            templateFormatConfig.options().header("This file has all the filters to create a template.\n" +
                                                  "their are 3 options: RACE, TYPE, LEVEL.\n" +
                                                  "Markers is a list of all the possible markers you can use.\n");

            templateFormatConfig.set(racePath, raceList);
            templateFormatConfig.set(typePath, typeList);
            templateFormatConfig.set(levelPath, levelList);
            templateFormatConfig.set(markersPath, markerList);
        } else {
            if (!templateFormatConfig.isSet(racePath)) {
                templateFormatConfig.set(racePath, raceList);
            }
            if (!templateFormatConfig.isSet(typePath)) {
                templateFormatConfig.set(typePath, typeList);
            }
            if (!templateFormatConfig.isSet(levelPath)) {
                templateFormatConfig.set(levelPath, levelList);
            }
            if (!templateFormatConfig.isSet(markersPath)) {
                templateFormatConfig.set(markersPath, markerList);
            }
        }

        try {
            templateFormatConfig.save(templateFormatConfigFile);
        } catch (IOException e) {
            getLogger().warning(e.toString());
        }
    }
}
