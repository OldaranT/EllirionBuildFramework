package com.ellirion.buildframework;


import com.ellirion.buildframework.terraincorrector.command.Test;
import com.ellirion.buildframework.terraincorrector.command.ValidateCommand;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class BuildFramework extends JavaPlugin {

    private static BuildFramework instance;
    private FileConfiguration config = getConfig();
    private FileConfiguration blockValueConfig;
    private File blockValueConfigFile;


    /***
     *
     * @return instance
     */

    public static BuildFramework getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;
        getLogger().info("[Ellirion] BuildFramework is enabled.");
        getCommand("Test").setExecutor(new Test());
        getCommand("Validate").setExecutor(new ValidateCommand());
        createConfig();
        getCommand("test").setExecutor(new Test());

    }

    @Override
    public void onDisable() {
        getLogger().info("[Ellirion] BuildFramework is disabled.");
    }

    public FileConfiguration getBlockValueConfig() {
        return blockValueConfig;
    }

    private void createConfig() {
        config.options().header("Ellirion-BuildFramework configuration file");
        config.addDefault("TerrainValidation_OverheadLimit", 0);
        config.addDefault("TerrainValidation_BocksLimit", 0);
        config.addDefault("TerrainValidation_TotalLimit", 0);
        config.addDefault("TerrainValidation_offset", 5);
        config.options().copyDefaults(true);
        saveConfig();
        reloadConfig();
    }

    private void createBlockValueConfig() {
        blockValueConfigFile = new File(getDataFolder(), "BlockValues.yml");

        if (!blockValueConfigFile.exists()) {
            blockValueConfigFile.getParentFile().mkdirs();
            saveResource("BlockValues.yml", false);
        }

        blockValueConfig = new YamlConfiguration();

        try {
            blockValueConfig.load(blockValueConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        blockValueConfig.addDefault(Material.STONE.toString(), 1);
        blockValueConfig.addDefault(Material.AIR.toString(), 0);
    }
}
