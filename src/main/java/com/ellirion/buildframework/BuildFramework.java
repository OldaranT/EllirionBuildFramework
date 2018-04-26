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
        this.getLogger().info("BuildFramework is disabled.");
    }

    private void createConfig() {
        this.config.options().header("Ellirion-BuildFramework configuration file");
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

        if (!blockValueConfigFile.exists() && blockValueConfigFile.getParentFile().mkdirs()) {
            saveResource("BlockValues.yml", false);
        }

        blockValueConfig = YamlConfiguration.loadConfiguration(blockValueConfigFile);

        if (blockValueConfigFile.exists()) {
            return;
        }

        blockValueConfig.options().header("The values for each block type of block material.\n" +
                                          "These values are used by the terrain validator.");
        for (Material m : Material.values()) {
            blockValueConfig.set(m.toString(), 1);
        }
        try {
            blockValueConfig.save(blockValueConfigFile);
        } catch (IOException e) {
            getLogger().warning(e.toString());
        }
    }

    private void createTemplateFormatConfig() {

        File templateFormatConfigFile = new File(getDataFolder(), "TemplateFormat.yml");

        if (!templateFormatConfigFile.exists() && templateFormatConfigFile.getParentFile().mkdirs()) {
            saveResource("TemplateFormat.yml", false);
        }

        templateFormatConfig = YamlConfiguration.loadConfiguration(templateFormatConfigFile);

        if (templateFormatConfigFile.exists()) {
            return;
        }

        templateFormatConfig.options().header("This file has all the filters to create a template.\n" +
                                              "their are 3 options: RACE, TYPE, LEVEL.\n" +
                                              "Markers is a list of all the possible markers you can use.\n");

        List<String> raceList = new ArrayList<>();
        raceList.add("ARGORIAN");
        raceList.add("DWARF");
        raceList.add("ELF");
        raceList.add("KHAIJT");
        raceList.add("ORC");
        raceList.add("VIKING");
        raceList.add("INFECTED");
        raceList.add("HUMAN");

        List<String> typeList = new ArrayList<>();
        typeList.add("HOUSE");
        typeList.add("BLACKSMITH");
        typeList.add("TOWNHALL");
        typeList.add("SAWMILL");
        typeList.add("STABLE");
        typeList.add("BARRACK");
        typeList.add("WINDMILL");
        typeList.add("HARBOR");

        List<String> levelList = new ArrayList<>();
        levelList.add("1");
        levelList.add("2");
        levelList.add("3");
        levelList.add("4");
        levelList.add("5");
        levelList.add("6");
        levelList.add("7");
        levelList.add("8");
        levelList.add("9");
        levelList.add("10");

        List<String> markerList = new ArrayList<>();
        markerList.add("DOOR");
        markerList.add("GROUND");
        markerList.add("PATH");

        templateFormatConfig.set("Races", raceList);
        templateFormatConfig.set("Types", typeList);
        templateFormatConfig.set("Levels", levelList);
        templateFormatConfig.set("Markers", markerList);

        try {
            templateFormatConfig.save(templateFormatConfigFile);
        } catch (IOException e) {
            getLogger().warning(e.toString());
        }
    }
}
