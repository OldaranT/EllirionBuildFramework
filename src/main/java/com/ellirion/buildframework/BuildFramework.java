package com.ellirion.buildframework;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import com.ellirion.buildframework.pathfinder.command.CommandFindPath;
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

public class BuildFramework extends JavaPlugin {

    private static BuildFramework instance;
    private FileConfiguration config = getConfig();
    private FileConfiguration blockValueConfig;

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
        getCommand("fp").setExecutor(new CommandFindPath());
        createConfig();
        createBlockValueConfig();
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
        config.addDefault("templatePath", "plugins/Ellirion/BuildFramework/templates/");
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
        blockValueConfig.set(Material.STONE.toString(), 1);
        for (Material m : Material.values()) {
            blockValueConfig.set(m.toString(), 1);
        }
        try {
            blockValueConfig.save(blockValueConfigFile);
        } catch (IOException e) {
            getLogger().warning(e.toString());
        }
    }
}
