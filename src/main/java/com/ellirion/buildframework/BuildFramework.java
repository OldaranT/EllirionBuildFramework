package com.ellirion.buildframework;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import com.ellirion.buildframework.terraincorrector.command.Test;
import com.ellirion.buildframework.terraincorrector.command.ValidateCommand;

import java.io.File;
import java.io.IOException;

public class BuildFramework extends JavaPlugin {

    private static BuildFramework instance;
    private FileConfiguration config = getConfig();
    private FileConfiguration blockValueConfig;

    /***
     *
     * @return instance
     */

    public static BuildFramework getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        getLogger().info("[Ellirion] BuildFramework is disabled.");
    }

    @Override
    public void onEnable() {

        instance = this;
        getLogger().info("[Ellirion] BuildFramework is enabled.");
        getCommand("Test").setExecutor(new Test());
        getCommand("Validate").setExecutor(new ValidateCommand());
        createConfig();
        createBlockValueConfig();
        getCommand("test").setExecutor(new Test());
    }

    public FileConfiguration getBlockValueConfig() {
        return blockValueConfig;
    }

    private void createConfig() {
        config.options().header("Ellirion-BuildFramework configuration file");
        config.addDefault("TerrainValidation_OverheadLimit", 0);
        config.addDefault("TerrainValidation_BlocksLimit", 0);
        config.addDefault("TerrainValidation_TotalLimit", 0);
        config.addDefault("TerrainValidation_Offset", 5);
        config.options().copyDefaults(true);
        saveConfig();
        reloadConfig();
    }

    private void createBlockValueConfig() {
        final File blockValueConfigFile = new File(getDataFolder(), "BlockValues.yml");

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

        blockValueConfig.options().header("The values for each block type of block material.\n" +
                                          "These values are used by the terrain validator.");
        blockValueConfig.addDefault(Material.STONE.toString(), 1);

        try {
            blockValueConfig.save(blockValueConfigFile);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
