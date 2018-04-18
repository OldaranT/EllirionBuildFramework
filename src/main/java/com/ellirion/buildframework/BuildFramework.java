package com.ellirion.buildframework;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BuildFramework extends JavaPlugin {


    private static BuildFramework instance;
    private FileConfiguration config = getConfig();

    public static BuildFramework getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance =  this;
        getLogger().info("[Ellirion] BuildFramework is enabled.");
        createConfig();

    }
    @Override
    public void onDisable() {
        getLogger().info("[Ellirion] BuildFramework is disabled.");
    }

    private void createConfig() {
        config.options().header("Ellirion-BuildFramework configuration file");
        config.addDefault("TerrainValidation_OverheadLimit", 0);
        config.options().copyDefaults(true);
        saveConfig();
        reloadConfig();
    }
}
